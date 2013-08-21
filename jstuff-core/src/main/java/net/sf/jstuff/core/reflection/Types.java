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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.validation.Args;

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
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					for (final Object mixin : mixins)
					{
						final Method methodImpl = Methods.findRecursive(mixin.getClass(), method.getName(), method.getParameterTypes());
						if (methodImpl != null) return Methods.invoke(mixin, methodImpl, args);
					}
					throw new UnsupportedOperationException("Method is not implemented.");
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

	/**
	 * @return true if objects of type <code>from</code> can be casted to type <code>to</code>
	 */
	public static boolean isCastable(final Class< ? > fromType, final Class< ? > toType)
	{
		Args.notNull("fromType", fromType);
		Args.notNull("toType", toType);

		return getPrimitiveWrapper(toType).isAssignableFrom(getPrimitiveWrapper(fromType));
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
