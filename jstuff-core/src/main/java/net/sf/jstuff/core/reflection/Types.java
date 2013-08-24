/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.reflection;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.reflection.visitor.ClassVisitor;
import net.sf.jstuff.core.reflection.visitor.ClassVisitorWithTypeArguments;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Types
{
	private static Logger LOG = Logger.create();

	@SuppressWarnings("unchecked")
	public static <T> T cast(final Object obj)
	{
		return (T) obj;
	}

	public static <T> T createMixin(final Class<T> objectInterface, final Object... mixins)
	{
		Args.notNull("objectInterface", objectInterface);
		Args.notEmpty("mixins", mixins);

		return Proxies.create(objectInterface, new InvocationHandler()
			{
				final Map<Method, Tuple2<Object, Method>> mappedMethodsCache = new ConcurrentHashMap<Method, Tuple2<Object, Method>>();

				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					Tuple2<Object, Method> mixedInMethod = mappedMethodsCache.get(method);
					if (mixedInMethod == null) for (final Object mixin : mixins)
					{
						final Method methodImpl = Methods.findRecursive(mixin.getClass(), method.getName(), method.getParameterTypes());
						if (methodImpl != null)
						{
							mixedInMethod = Tuple2.create(mixin, methodImpl);
							mappedMethodsCache.put(method, mixedInMethod);
							break;
						}
					}
					if (mixedInMethod == null) throw new UnsupportedOperationException("Method is not implemented.");
					return Methods.invoke(mixedInMethod.get1(), mixedInMethod.get2(), args);
				}
			});
	}

	public static <T> T createSynchronized(final Class<T> objectInterface, final T object)
	{
		Args.notNull("objectInterface", objectInterface);
		Args.notNull("object", object);

		return createSynchronized(objectInterface, object, object);
	}

	public static <T> T createSynchronized(final Class<T> objectInterface, final T object, final Object lock)
	{
		Args.notNull("objectInterface", objectInterface);
		Args.notNull("object", object);

		return Proxies.create(objectInterface, new InvocationHandler()
			{
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					synchronized (lock)
					{
						return method.invoke(object, args);
					}
				}
			});
	}

	public static <T> T createThreadLocalized(final Class<T> objectInterface, final ThreadLocal<T> threadLocal)
	{
		Args.notNull("objectInterface", objectInterface);
		Args.notNull("threadLocal", threadLocal);

		return Proxies.create(objectInterface, new InvocationHandler()
			{
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					return method.invoke(threadLocal.get(), args);
				}
			});
	}

	/**
	 * @return null if class not found
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class< ? extends T> find(final String className)
	{
		Args.notNull("className", className);

		LOG.trace("Trying to load class [%s]...", className);
		try
		{
			return (Class< ? extends T>) Types.class.getClassLoader().loadClass(className);
		}
		catch (final ClassNotFoundException ex)
		{}

		try
		{
			return (Class< ? extends T>) Thread.currentThread().getContextClassLoader().loadClass(className);
		}
		catch (final ClassNotFoundException ex)
		{}
		return null;
	}

	public static <T> Type[] findGenericTypeArguments(final Class< ? extends T> searchIn, final Class<T> searchFor)
	{
		Args.notNull("searchIn", searchIn);
		Args.notNull("searchFor", searchFor);

		// if the searchFor type is not a generic type there is nothing to find
		if (searchFor.getTypeParameters().length == 0) return ArrayUtils.EMPTY_CLASS_ARRAY;

		if (!searchFor.isAssignableFrom(searchIn))
			throw new IllegalArgumentException("Class [searchIn=" + searchIn.getName() + "] is assignable to [searchFor="
					+ searchFor.getName() + "]");

		final boolean isSearchForInterface = searchFor.isInterface();

		/*
		 * traverse the class hierarchy and collect generic variable => concrete variable argument (type) mappings
		 */
		final Map<TypeVariable< ? >, Type> genericVariableToArgumentMappings = newHashMap();
		final ParameterizedType[] searchForType = {null};

		visit(searchIn, new ClassVisitorWithTypeArguments()
			{
				public boolean isVisiting(final Class< ? > clazz, final ParameterizedType type)
				{
					return searchFor.isAssignableFrom(clazz);
				}

				public boolean isVisitingInterfaces(final Class< ? > clazz, final ParameterizedType type)
				{
					return isSearchForInterface && searchFor.isAssignableFrom(clazz);
				}

				public boolean isVisitingSuperclass(final Class< ? > clazz, final ParameterizedType type)
				{
					return searchFor.isAssignableFrom(clazz);
				}

				public boolean visit(final Class< ? > clazz, final ParameterizedType type)
				{
					if (type != null) CollectionUtils.putAll(genericVariableToArgumentMappings, //
							/*generic variable*/(TypeVariable< ? >[]) clazz.getTypeParameters(), //
							/*arguments (concrete types) for generic variables*/type.getActualTypeArguments() //
							);

					if (clazz == searchFor)
					{
						searchForType[0] = type;
						return false;
					}
					return true;
				}
			});

		/*
		 * build the result list based on the information collected in genericVariableToTypeMappings
		 */
		final Type[] genericVariables;
		if (searchForType[0] == null)
			genericVariables = searchFor.getTypeParameters();
		else
			genericVariables = searchForType[0].getActualTypeArguments();
		final Class< ? >[] res = new Class< ? >[genericVariables.length];
		for (int i = 0, l = genericVariables.length; i < l; i++)
		{
			Type genericVariable = genericVariables[i];
			while (genericVariableToArgumentMappings.containsKey(genericVariable))
				genericVariable = genericVariableToArgumentMappings.get(genericVariable);
			res[i] = getUnderlyingClass(genericVariable);
		}
		return res;
	}

	/**
	 * @param clazz the class to inspect
	 * @return a set with all implemented interfaces
	 */
	public static Set<Class< ? >> getInterfacesRecursive(final Class< ? > clazz)
	{
		Args.notNull("clazz", clazz);

		return getInterfacesRecursive(clazz, new HashSet<Class< ? >>(2));
	}

	private static Set<Class< ? >> getInterfacesRecursive(Class< ? > clazz, final Set<Class< ? >> result)
	{
		while (clazz != null)
		{
			for (final Class< ? > next : clazz.getInterfaces())
			{
				result.add(next);
				getInterfacesRecursive(next, result);
			}
			clazz = clazz.getSuperclass();
		}
		return result;
	}

	public static Class< ? > getPrimitiveWrapper(final Class< ? > primitive)
	{
		if (!primitive.isPrimitive()) return primitive;

		if (boolean.class == primitive) return Boolean.class;

		if (byte.class == primitive) return Byte.class;
		if (short.class == primitive) return Short.class;
		if (int.class == primitive) return Integer.class;
		if (long.class == primitive) return Long.class;

		if (float.class == primitive) return Float.class;
		if (double.class == primitive) return Double.class;

		if (char.class == primitive) return Character.class;

		if (void.class == primitive) return Void.class;

		throw new RuntimeException("Unknown primitive type [" + primitive + "]");
	}

	public static Class< ? > getUnderlyingClass(final Type type)
	{
		if (type instanceof Class) return (Class< ? >) type;
		if (type instanceof ParameterizedType) return getUnderlyingClass(((ParameterizedType) type).getRawType());
		if (type instanceof GenericArrayType)
		{
			final Type ctype = ((GenericArrayType) type).getGenericComponentType();
			final Class< ? > cclass = getUnderlyingClass(ctype);
			if (cclass != null) return Array.newInstance(cclass, 0).getClass();
		}
		// type has no underlying class, e.g. TypeVariable
		return null;
	}

	public static boolean isAbstract(final Class< ? > type)
	{
		Args.notNull("type", type);

		return (type.getModifiers() & Modifier.ABSTRACT) != 0;
	}

	/**
	 * @return true if an object of type <code>fromType</code> can be used as value for a field or parameter of type <code>toType</code>
	 */
	public static boolean isAssignableTo(final Class< ? > valueType, final Class< ? > memberType)
	{
		Args.notNull("valueType", valueType);
		Args.notNull("memberType", memberType);

		return getPrimitiveWrapper(memberType).isAssignableFrom(getPrimitiveWrapper(valueType));
	}

	public static boolean isClassAvailable(final String className)
	{
		return find(className) != null;
	}

	public static boolean isInnerClass(final Class< ? > type)
	{
		Args.notNull("type", type);

		return type.getName().indexOf('$') > -1;
	}

	public static boolean isNonStaticInnerClass(final Class< ? > type)
	{
		return isInnerClass(type) && (type.getModifiers() & Modifier.STATIC) == 0;
	}

	public static boolean isScalar(final Class< ? > type)
	{
		Args.notNull("type", type);

		return type.isPrimitive() || type == Boolean.class || //
				type == Character.class || //
				Enum.class.isAssignableFrom(type) || //
				Number.class.isAssignableFrom(type) || //
				CharSequence.class.isAssignableFrom(type) || //
				Date.class.isAssignableFrom(type);
	}

	public static <T> T newInstance(final Class<T> type, final Object... constructorArgs)
	{
		final Constructor<T> ctor = Constructors.findCompatible(type, constructorArgs);
		if (ctor == null)
			throw new IllegalArgumentException("No constructor found in class [" + type.getName() + "] compatible with give arguments!");
		return Constructors.invoke(ctor, constructorArgs);
	}

	/**
	 * Tries to read the given value using a getter method or direct field access
	 */
	public static <T> T readPropety(final Object obj, final String propertyName, final Class< ? extends T> compatibleTo)
			throws ReflectionException
	{
		Args.notNull("obj", obj);
		Args.notNull("propertyName", propertyName);

		final Class< ? > clazz = obj.getClass();

		final Method getter = Methods.findGetterRecursive(clazz, propertyName, compatibleTo);
		if (getter != null) return Methods.invoke(obj, getter);

		final Field field = Fields.findRecursive(clazz, propertyName, compatibleTo);
		if (field != null) return Fields.read(obj, field);

		throw new ReflectionException("No corresponding getter method or field found for property [" + propertyName + "] in class ["
				+ clazz + "]");
	}

	public static void visit(final Class< ? > clazz, final ClassVisitor visitor)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("visitor", visitor);

		final Queue<Class< ? >> toVisit = new LinkedList<Class< ? >>();
		toVisit.add(clazz);
		while (!toVisit.isEmpty())
		{
			final Class< ? > current = toVisit.poll();

			if (!visitor.visit(current)) return;
			if (visitor.isVisitingFields(current)) for (final Field f : current.getDeclaredFields())
				if (visitor.isVisitingField(f)) if (!visitor.visit(f)) return;

			if (visitor.isVisitingMethods(current)) for (final Method m : current.getDeclaredMethods())
				if (visitor.isVisitingMethod(m)) if (!visitor.visit(m)) return;

			if (visitor.isVisitingSuperclass(current))
			{
				final Class< ? > sclass = current.getSuperclass();
				if (sclass != null && visitor.isVisiting(sclass)) toVisit.add(sclass);
			}
			if (visitor.isVisitingInterfaces(current)) for (final Class< ? > iface : current.getInterfaces())
				if (visitor.isVisiting(iface)) toVisit.add(iface);
		}
	}

	public static void visit(final Class< ? > clazz, final ClassVisitorWithTypeArguments visitor)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("visitor", visitor);

		final Queue<Type> toVisit = new LinkedList<Type>();
		toVisit.add(clazz);
		while (!toVisit.isEmpty())
		{
			final Type current = toVisit.poll();

			final Class< ? > currentClass;
			final ParameterizedType currentType;
			if (current instanceof ParameterizedType)
			{
				currentType = (ParameterizedType) current;
				currentClass = (Class< ? >) currentType.getRawType();
			}
			else
			{
				currentType = null;
				currentClass = (Class< ? >) current;
			}

			if (!visitor.visit(currentClass, currentType)) return;

			if (visitor.isVisitingSuperclass(currentClass, currentType))
			{
				final Type sclass = currentClass.getGenericSuperclass();
				if (sclass != null) if (sclass instanceof ParameterizedType)
				{
					final ParameterizedType sptype = (ParameterizedType) sclass;
					if (visitor.isVisiting((Class< ? >) sptype.getRawType(), sptype)) toVisit.add(sclass);
				}
				else if (visitor.isVisiting((Class< ? >) sclass, null)) toVisit.add(sclass);
			}

			if (visitor.isVisitingInterfaces(currentClass, currentType)) for (final Type itype : currentClass.getGenericInterfaces())
				if (itype instanceof ParameterizedType)
				{
					final ParameterizedType iptype = (ParameterizedType) itype;
					if (visitor.isVisiting((Class< ? >) iptype.getRawType(), iptype)) toVisit.add(itype);
				}
				else if (visitor.isVisiting((Class< ? >) itype, null)) toVisit.add(itype);
		}
	}

	/**
	 * Tries to write the given value using a setter method or direct field access
	 */
	public static void writeProperty(final Object obj, final String propertyName, final Object value) throws ReflectionException
	{
		Args.notNull("obj", obj);
		Args.notNull("propertyName", propertyName);

		final Class< ? > clazz = obj.getClass();

		final Method setter = Methods.findSetterRecursive(clazz, propertyName, value == null ? null : value.getClass());
		if (setter != null)
		{
			Methods.invoke(obj, setter, value);
			return;
		}

		final Field field = Fields.findRecursive(clazz, propertyName, value == null ? null : value.getClass());
		if (field != null)
		{
			Fields.write(obj, field, value);
			return;
		}
		throw new ReflectionException("No corresponding getter method or field found for property [" + propertyName + "] in class ["
				+ clazz + "]");
	}
}
