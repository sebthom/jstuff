/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.ReflectPermission;
import java.security.AccessController;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.collection.CollectionUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReflectionUtils
{
	private static final Logger LOG = Logger.get();

	private static final ReflectPermission SUPPRESS_ACCESS_CHECKS_PERMISSION = new ReflectPermission(
			"suppressAccessChecks");

	/**
	 * @throws SecurityException
	 */
	public static void assertPrivateAccessAllowed()
	{
		final SecurityManager manager = System.getSecurityManager();
		if (manager != null)
			try
			{
				manager.checkPermission(SUPPRESS_ACCESS_CHECKS_PERMISSION);
			}
			catch (final SecurityException ex)
			{
				throw new ReflectionException(
						"Current security manager configuration does not allow access to private fields and methods.",
						ex);
			}
	}

	/**
	 * Creates a dynamic proxy object of type <code>duckInterface</code> forwarding all method invocations
	 * to methods with the same signature on <code>duckLikeObject</code>.
	 * 
	 * @return <code>duckLikeObject</code> if instanceof <code>duckInterface</code> or a dynamic proxy object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T duckType(final Object duckLikeObject, final Class<T> duckInterface)
	{
		final Class< ? > duckLikeClass = duckLikeObject.getClass();
		if (duckInterface.isAssignableFrom(duckLikeClass)) return (T) duckLikeObject;

		LOG.debug("Ducktyping {} to type {}", duckLikeObject, duckInterface);

		return (T) Proxy.newProxyInstance(duckInterface.getClassLoader(), new Class[]{duckInterface},
				new InvocationHandler()
					{
						public Object invoke(final Object duckProxy, final Method duckMethod, final Object[] args)
								throws Throwable
						{
							try
							{
								final Method duckLikeMethod = duckLikeClass.getMethod(duckMethod.getName(),
										duckMethod.getParameterTypes());

								// delegate method invocation on duck proxy to duckLikeObject's method
								return duckLikeMethod.invoke(duckLikeObject, args);
							}
							catch (final NoSuchMethodException ex)
							{
								throw new ReflectionException("Duck typed object " + duckLikeObject
										+ " does not implement duck method " + duckInterface + ".");
							}
						}
					});
	}

	/**
	 * Returns all annotations present on this class.
	 * @param clazz the class to inspect
	 * @param inspectInterfaces whether to also return annotations declared on interface declaration
	 * @return all annotations present on this class.
	 */
	public static Annotation[] getAnnotations(final Class< ? > clazz, final boolean inspectInterfaces)
	{
		Assert.argumentNotNull("clazz", clazz);

		if (!inspectInterfaces) return clazz.getAnnotations();

		final List<Annotation> annotations = Arrays.asList(clazz.getAnnotations());
		for (final Class< ? > next : ReflectionUtils.getInterfacesRecursive(clazz))
		{
			final Annotation[] declaredAnnotations = next.getDeclaredAnnotations();
			annotations.addAll(Arrays.asList(declaredAnnotations));
		}
		return annotations.toArray(new Annotation[annotations.size()]);
	}

	/**
	 * Returns all annotations present on this method.
	 * @param method the method to inspect
	 * @param inspectInterfaces whether to also return annotations declared on interface method declaration
	 * @return all annotations present on this method.
	 */
	public static Annotation[] getAnnotations(final Method method, final boolean inspectInterfaces)
	{
		Assert.argumentNotNull("method", method);

		if (!inspectInterfaces || !isPublic(method)) return method.getAnnotations();

		final String methodName = method.getName();
		final Class< ? >[] methodParameterTypes = method.getParameterTypes();

		final List<Annotation> annotations = Arrays.asList(method.getAnnotations());
		for (final Class< ? > nextClass : ReflectionUtils.getInterfacesRecursive(method.getDeclaringClass()))
			try
			{
				annotations.addAll(Arrays.asList(nextClass.getDeclaredMethod(methodName, methodParameterTypes)
						.getDeclaredAnnotations()));
			}
			catch (final NoSuchMethodException e)
			{
				// ignore
			}
		return annotations.toArray(new Annotation[annotations.size()]);
	}

	/**
	 * @return the field or null if the field does not exist
	 */
	public static Field getField(final Class< ? > clazz, final String fieldName)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("fieldName", fieldName);

		try
		{
			return clazz.getDeclaredField(fieldName);
		}
		catch (final NoSuchFieldException e)
		{
			return null;
		}
	}

	/**
	 * @param setter
	 * @return Returns the corresponding field for a setter method. Returns null if the method is not a 
	 * JavaBean style setter or the field could not be located.
	 */
	public static Field getFieldForSetter(final Method setter)
	{
		Assert.argumentNotNull("setter", setter);

		if (!isSetter(setter)) return null;

		final Class< ? >[] methodParameterTypes = setter.getParameterTypes();
		final String methodName = setter.getName();
		final Class< ? > clazz = setter.getDeclaringClass();

		// calculate the corresponding field name based on the name of the setter method (e.g. method setName() => field
		// name)
		String fieldName = methodName.substring(3, 4).toLowerCase(Locale.getDefault());
		if (methodName.length() > 4) fieldName += methodName.substring(4);

		Field field = null;
		try
		{
			field = clazz.getDeclaredField(fieldName);

			// check if field and method parameter are of the same type
			if (!field.getType().equals(methodParameterTypes[0]))
			{
				LOG.warn(
						"Found field [%s] in class [%s] that matches setter [%s] name, but mismatches parameter type.",
						fieldName, clazz.getName(), methodName);
				field = null;
			}
		}
		catch (final NoSuchFieldException e)
		{
			LOG.debug("Field not found", e);
		}

		// if method parameter type is boolean then check if a field with name isXXX exists (e.g. method setEnabled() =>
		// field isEnabled)
		if (field == null
				&& (boolean.class.equals(methodParameterTypes[0]) || Boolean.class.equals(methodParameterTypes[0])))
		{
			fieldName = "is" + methodName.substring(3);

			try
			{
				field = clazz.getDeclaredField(fieldName);

				// check if found field is of boolean or Boolean
				if (!boolean.class.equals(field.getType()) && Boolean.class.equals(field.getType()))
				{
					LOG.warn(
							"Found field [%s] in class [%s] that matches setter [%s] name, but mismatches parameter type.",
							fieldName, clazz.getName(), methodName);
					field = null;
				}
			}
			catch (final NoSuchFieldException ex)
			{
				LOG.debug("Field not found", ex);
			}
		}

		return field;
	}

	public static Field getFieldRecursive(final Class< ? > clazz, final String fieldName)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("fieldName", fieldName);

		final Field f = getField(clazz, fieldName);
		if (f != null) return f;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getFieldRecursive(superclazz, fieldName);
	}

	public static Object getFieldValue(final Field field, final Object obj) throws AccessingFieldValueFailedException
	{
		Assert.argumentNotNull("field", field);
		Assert.argumentNotNull("obj", obj);

		try
		{
			if (!field.isAccessible()) AccessController.doPrivileged(new SetAccessibleAction(field));
			return field.get(obj);
		}
		catch (final Exception ex)
		{
			throw new AccessingFieldValueFailedException(field, obj, ex);
		}
	}

	public static Method getGetter(final Class< ? > clazz, final String propertyName)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("propertyName", propertyName);

		final String appendix = propertyName.substring(0, 1).toUpperCase(Locale.getDefault())
				+ propertyName.substring(1);
		try
		{
			return clazz.getDeclaredMethod("get" + appendix);
		}
		catch (final NoSuchMethodException ex)
		{
			LOG.trace("getXXX method not found.", ex);
		}
		try
		{
			return clazz.getDeclaredMethod("is" + appendix);
		}
		catch (final NoSuchMethodException ex)
		{
			LOG.trace("isXXX method not found.", ex);
			return null;
		}
	}

	public static Method getGetterRecursive(final Class< ? > clazz, final String propertyName)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("propertyName", propertyName);

		final Method m = getGetter(clazz, propertyName);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getGetterRecursive(superclazz, propertyName);
	}

	public static List<Method> getInterfaceMethods(final Method method)
	{
		Assert.argumentNotNull("method", method);

		// static methods cannot be overridden
		if (isStatic(method)) return null;

		final Class< ? >[] interfaces = method.getDeclaringClass().getInterfaces();
		if (interfaces.length == 0) return null;

		final String methodName = method.getName();
		final Class< ? >[] parameterTypes = method.getParameterTypes();

		final List<Method> methods = CollectionUtils.newArrayList(interfaces.length);
		for (final Class< ? > iface : interfaces)
		{
			final Method m = getMethod(iface, methodName, parameterTypes);
			if (m != null) methods.add(m);
		}
		return methods;
	}

	/**
	 * @param clazz the class to inspect
	 * @return a set with all implemented interfaces
	 */
	public static Set<Class< ? >> getInterfacesRecursive(final Class< ? > clazz)
	{
		Assert.argumentNotNull("clazz", clazz);

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

	/**
	 * @return the method or null if the method does not exist
	 */
	public static Method getMethod(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("methodName", methodName);
		try
		{
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		}
		catch (final NoSuchMethodException e)
		{
			return null;
		}
	}

	/**
	 * @return the method or null if the method does not exist
	 */
	public static Method getMethodRecursive(final Class< ? > clazz, final String methodName,
			final Class< ? >... parameterTypes)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("methodName", methodName);

		final Method m = getMethod(clazz, methodName, parameterTypes);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getMethodRecursive(superclazz, methodName, parameterTypes);
	}

	/**
	 * Returns an array of arrays that represent the annotations on the formal parameters, in declaration order, 
	 * of the method represented by this method.
	 *  
	 * @param method the method to inspect
	 * @param inspectInterfaces whether to also return annotations declared on interface method declaration
	 * @return an array of arrays that represent the annotations on the formal parameters, in declaration order, 
	 * of the method represented by this method.
	 */
	public static Annotation[][] getParameterAnnotations(final Method method, final boolean inspectInterfaces)
	{
		Assert.argumentNotNull("method", method);

		if (!inspectInterfaces || !isPublic(method)) return method.getParameterAnnotations();

		final String methodName = method.getName();
		final Class< ? >[] methodParameterTypes = method.getParameterTypes();
		final int methodParameterTypesCount = methodParameterTypes.length;

		@SuppressWarnings("unchecked")
		final HashSet<Annotation>[] methodParameterAnnotations = new HashSet[methodParameterTypesCount];

		final Class< ? > clazz = method.getDeclaringClass();
		final Set<Class< ? >> classes = ReflectionUtils.getInterfacesRecursive(clazz);
		classes.add(clazz);
		for (final Class< ? > nextClass : classes)
			try
			{
				final Method nextMethod = nextClass.getDeclaredMethod(methodName, methodParameterTypes);
				for (int i = 0; i < methodParameterTypesCount; i++)
				{
					final Annotation[] paramAnnos = nextMethod.getParameterAnnotations()[i];
					if (paramAnnos.length > 0)
					{
						HashSet<Annotation> cummulatedParamAnnos = methodParameterAnnotations[i];
						if (cummulatedParamAnnos == null)
							methodParameterAnnotations[i] = cummulatedParamAnnos = new HashSet<Annotation>();
						for (final Annotation anno : paramAnnos)
							cummulatedParamAnnos.add(anno);
					}
				}
			}
			catch (final NoSuchMethodException e)
			{
				// ignore
			}

		final Annotation[][] result = new Annotation[methodParameterTypesCount][];
		for (int i = 0; i < methodParameterTypesCount; i++)
		{
			final HashSet<Annotation> paramAnnos = methodParameterAnnotations[i];
			result[i] = paramAnnos == null ? new Annotation[0] : methodParameterAnnotations[i]
					.toArray(new Annotation[methodParameterAnnotations[i].size()]);

		}
		return result;
	}

	public static Method getSetter(final Class< ? > clazz, final String propertyName)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("propertyName", propertyName);

		final String methodName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.getDefault())
				+ propertyName.substring(1);

		final Method[] declaredMethods = clazz.getDeclaredMethods();
		for (final Method method : declaredMethods)
			if (methodName.equals(method.getName()) && method.getParameterTypes().length == 1) return method;
		LOG.trace("No setter for [%s] found on class %s.", propertyName, clazz);
		return null;
	}

	public static Method getSetterRecursive(final Class< ? > clazz, final String propertyName)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("propertyName", propertyName);

		final Method m = getSetter(clazz, propertyName);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getSetterRecursive(superclazz, propertyName);
	}

	public static Method getSuperMethod(final Method method)
	{
		Assert.argumentNotNull("method", method);

		// static methods cannot be overridden
		if (isStatic(method)) return null;

		final String methodName = method.getName();
		final Class< ? >[] parameterTypes = method.getParameterTypes();

		Class< ? > currentClass = method.getDeclaringClass();

		while (currentClass != null && currentClass != Object.class)
		{
			currentClass = currentClass.getSuperclass();

			final Method m = getMethod(currentClass, methodName, parameterTypes);
			if (m != null && !isPrivate(m)) return m;
		}
		return null;
	}

	public static String guessFieldName(final Method getter)
	{
		Assert.argumentNotNull("getter", getter);

		String fieldName = getter.getName();

		if (fieldName.startsWith("get") && fieldName.length() > 3)
		{
			fieldName = fieldName.substring(3);
			if (fieldName.length() == 1)
				fieldName = fieldName.toLowerCase(Locale.getDefault());
			else
				fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
		}
		else if (fieldName.startsWith("is") && fieldName.length() > 2)
		{
			fieldName = fieldName.substring(2);
			if (fieldName.length() == 1)
				fieldName = fieldName.toLowerCase(Locale.getDefault());
			else
				fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
		}

		return fieldName;
	}

	public static boolean hasField(final Class< ? > clazz, final String fieldName)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("fieldName", fieldName);

		return getField(clazz, fieldName) != null;
	}

	public static boolean hasMethod(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
	{
		Assert.argumentNotNull("clazz", clazz);
		Assert.argumentNotNull("methodName", methodName);

		return getMethod(clazz, methodName, parameterTypes) != null;
	}

	/**
	 * 
	 * @param method the method to invoke
	 * @param obj the object on which to invoke the method
	 * @param args the method arguments
	 * @return the return value of the invoked method
	 * @throws InvokingMethodFailedException
	 */
	public static Object invokeMethod(final Method method, final Object obj, final Object... args)
			throws InvokingMethodFailedException
	{
		Assert.argumentNotNull("method", method);
		Assert.argumentNotNull("obj", obj);

		try
		{
			if (!method.isAccessible()) AccessController.doPrivileged(new SetAccessibleAction(method));
			return method.invoke(obj, args);
		}
		catch (final Exception ex)
		{
			throw new InvokingMethodFailedException(method, obj, ex);
		}
	}

	/**
	 * Returns true if an annotation for the specified type is present on this method, else false. 
	 *  
	 * @param method the method to inspect
	 * @param annotationClass the Class object corresponding to the annotation type
	 * @param inspectInterfaces whether to also check annotations declared on interface method declaration
	 * @return true if an annotation for the specified annotation type is present on this method, else false
	 */
	public static boolean isAnnotationPresent(final Method method, final Class< ? extends Annotation> annotationClass,
			final boolean inspectInterfaces)
	{
		Assert.argumentNotNull("method", method);
		Assert.argumentNotNull("annotationClass", annotationClass);

		if (method.isAnnotationPresent(annotationClass)) return true;

		if (!inspectInterfaces || !isPublic(method)) return false;

		final String methodName = method.getName();
		final Class< ? >[] methodParameterTypes = method.getParameterTypes();

		for (final Class< ? > next : getInterfacesRecursive(method.getDeclaringClass()))
			try
			{
				if (next.getDeclaredMethod(methodName, methodParameterTypes).isAnnotationPresent(annotationClass))
					return true;
			}
			catch (final NoSuchMethodException e)
			{
				// ignore
			}
		return false;
	}

	/**
	 * @return true if objects of type <code>from</code> can be casted to type <code>to</code>
	 */
	public static boolean isCastable(final Class< ? > fromType, final Class< ? > toType)
	{
		Assert.argumentNotNull("fromType", fromType);
		Assert.argumentNotNull("toType", toType);
		return toType.isAssignableFrom(fromType);
	}

	public static boolean isClassPresent(final String className)
	{
		Assert.argumentNotNull("className", className);

		try
		{
			Class.forName(className);
			return true;
		}
		catch (final ClassNotFoundException e)
		{
			return false;
		}
	}

	/**
	 * @return true if <code>duckLikeObject</code> implements all public methods declared on <code>duckType</code>
	 */
	public static boolean isDuckType(final Object duckLikeObject, final Class< ? > duckType)
	{
		final Class< ? > duckLikeClass = duckLikeObject.getClass();
		if (duckType.isAssignableFrom(duckLikeClass)) return true;
		for (final Method method : duckType.getMethods())
			try
			{
				duckLikeClass.getMethod(method.getName(), method.getParameterTypes());
			}
			catch (final NoSuchMethodException e)
			{
				return false;
			}
		return true;
	}

	public static boolean isFinal(final Member member)
	{
		Assert.argumentNotNull("member", member);

		return (member.getModifiers() & Modifier.FINAL) != 0;
	}

	/**
	 * determines if a method is a JavaBean style getter method
	 */
	public static boolean isGetter(final Method method)
	{
		Assert.argumentNotNull("method", method);

		return method.getParameterTypes().length == 0
				&& (method.getName().startsWith("is") || method.getName().startsWith("get"));
	}

	public static boolean isInnerClass(final Class< ? > clazz)
	{
		Assert.argumentNotNull("clazz", clazz);

		return clazz.getName().indexOf('$') > -1;
	}

	public static boolean isNonStaticInnerClass(final Class< ? > clazz)
	{
		Assert.argumentNotNull("clazz", clazz);

		return clazz.getName().indexOf('$') > -1 && (clazz.getModifiers() & Modifier.STATIC) == 0;
	}

	public static boolean isPackage(final Member member)
	{
		Assert.argumentNotNull("member", member);

		return (member.getModifiers() & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED)) == 0;
	}

	public static boolean isPrivate(final Member member)
	{
		Assert.argumentNotNull("member", member);

		return (member.getModifiers() & Modifier.PRIVATE) != 0;
	}

	public static boolean isPrivateAccessAllowed()
	{
		final SecurityManager manager = System.getSecurityManager();
		if (manager != null) try
		{
			manager.checkPermission(SUPPRESS_ACCESS_CHECKS_PERMISSION);
		}
		catch (final SecurityException ex)
		{
			return false;
		}
		return true;
	}

	public static boolean isProtected(final Member member)
	{
		Assert.argumentNotNull("member", member);

		return (member.getModifiers() & Modifier.PROTECTED) != 0;
	}

	public static boolean isPublic(final Member member)
	{
		Assert.argumentNotNull("member", member);

		return (member.getModifiers() & Modifier.PUBLIC) != 0;
	}

	/**
	 * determines if a method is a JavaBean style setter method
	 */
	public static boolean isSetter(final Method method)
	{
		Assert.argumentNotNull("method", method);

		final Class< ? >[] methodParameterTypes = method.getParameterTypes();

		// check if method has exactly one parameter
		if (methodParameterTypes.length != 1) return false;

		final String methodName = method.getName();
		final int methodNameLen = methodName.length();

		// check if the method's name starts with setXXX
		if (methodNameLen < 4 || !methodName.startsWith("set")) return false;

		return true;
	}

	public static boolean isStatic(final Member member)
	{
		Assert.argumentNotNull("member", member);

		return (member.getModifiers() & Modifier.STATIC) != 0;
	}

	public static boolean isTransient(final Member member)
	{
		Assert.argumentNotNull("member", member);

		return (member.getModifiers() & Modifier.TRANSIENT) != 0;
	}

	/**
	 * determines if a method is a void method
	 */
	public static boolean isVoidMethod(final Method method)
	{
		Assert.argumentNotNull("method", method);

		return method.getReturnType() == void.class;
	}

	public static Class< ? > loadClass(final String className)
	{
		Assert.argumentNotNull("className", className);

		try
		{
			LOG.trace("Trying to load class [%s]...", className);
			return Class.forName(className);
		}
		catch (final ClassNotFoundException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class< ? extends T> loadClass(final String className, final Class<T> baseClass)
	{
		Assert.argumentNotNull("className", className);

		try
		{
			LOG.trace("Trying to load class [%s] extending [%s]...", className, baseClass);
			return (Class< ? extends T>) Class.forName(className);
		}
		catch (final ClassNotFoundException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static boolean setViaSetter(final Object target, final String propertyName, final Object propertyValue)
	{
		Assert.argumentNotNull("target", target);
		Assert.argumentNotNull("propertyName", propertyName);

		final Method setter = getSetterRecursive(target.getClass(), propertyName);
		if (setter != null) try
		{
			setter.invoke(target, propertyValue);
		}
		catch (final IllegalArgumentException ex)
		{
			LOG.debug("Setting [%s] failed on [%s] failed.", propertyName, target, ex);
			return false;
		}
		catch (final IllegalAccessException ex)
		{
			LOG.debug("Setting [%s] failed on [%s] failed.", propertyName, target, ex);
			return false;
		}
		catch (final InvocationTargetException ex)
		{
			LOG.debug("Setting [%s] failed on [%s] failed.", propertyName, target, ex);
			return false;
		}
		return false;
	}

	protected ReflectionUtils()
	{
		super();
	}
}
