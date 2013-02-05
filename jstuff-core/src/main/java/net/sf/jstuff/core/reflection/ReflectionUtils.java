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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.ReflectPermission;
import java.security.AccessController;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ReflectionUtils
{
	private static final Logger LOG = Logger.create();

	private static final ReflectPermission SUPPRESS_ACCESS_CHECKS_PERMISSION = new ReflectPermission("suppressAccessChecks");

	/**
	 * @throws SecurityException
	 */
	public static void assertPrivateAccessAllowed()
	{
		final SecurityManager mgr = System.getSecurityManager();
		if (mgr != null)
			try
			{
				mgr.checkPermission(SUPPRESS_ACCESS_CHECKS_PERMISSION);
			}
			catch (final SecurityException ex)
			{
				throw new ReflectionException(
						"Current security manager configuration does not allow access to private fields and methods.", ex);
			}
	}

	/**
	 * @return the field or null if the field does not exist
	 */
	public static Field getField(final Class< ? > clazz, final String fieldName)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("fieldName", fieldName);

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
		Args.notNull("setter", setter);

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
				LOG.warn("Found field [%s] in class [%s] that matches setter [%s] name, but mismatches parameter type.", fieldName,
						clazz.getName(), methodName);
				field = null;
			}
		}
		catch (final NoSuchFieldException e)
		{
			LOG.debug("Field not found", e);
		}

		// if method parameter type is boolean then check if a field with name isXXX exists (e.g. method setEnabled() =>
		// field isEnabled)
		if (field == null && (boolean.class.equals(methodParameterTypes[0]) || Boolean.class.equals(methodParameterTypes[0])))
		{
			fieldName = "is" + methodName.substring(3);

			try
			{
				field = clazz.getDeclaredField(fieldName);

				// check if found field is of boolean or Boolean
				if (!boolean.class.equals(field.getType()) && Boolean.class.equals(field.getType()))
				{
					LOG.warn("Found field [%s] in class [%s] that matches setter [%s] name, but mismatches parameter type.", fieldName,
							clazz.getName(), methodName);
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
		Args.notNull("clazz", clazz);
		Args.notNull("fieldName", fieldName);

		final Field f = getField(clazz, fieldName);
		if (f != null) return f;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getFieldRecursive(superclazz, fieldName);
	}

	public static Object getFieldValue(final Field field, final Object obj) throws AccessingFieldValueFailedException
	{
		Args.notNull("field", field);
		Args.notNull("obj", obj);

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
		Args.notNull("clazz", clazz);
		Args.notNull("propertyName", propertyName);

		final String appendix = propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);
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
		Args.notNull("clazz", clazz);
		Args.notNull("propertyName", propertyName);

		final Method m = getGetter(clazz, propertyName);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getGetterRecursive(superclazz, propertyName);
	}

	public static List<Method> getInterfaceMethods(final Method method)
	{
		Args.notNull("method", method);

		// static methods cannot be overridden
		if (isStatic(method)) return null;

		final Class< ? >[] interfaces = method.getDeclaringClass().getInterfaces();
		if (interfaces.length == 0) return null;

		final String methodName = method.getName();
		final Class< ? >[] parameterTypes = method.getParameterTypes();

		final List<Method> methods = newArrayList(interfaces.length);
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

	/**
	 * @return the method or null if the method does not exist
	 */
	public static Method getMethod(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("methodName", methodName);
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
	public static Method getMethodRecursive(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("methodName", methodName);

		final Method m = getMethod(clazz, methodName, parameterTypes);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getMethodRecursive(superclazz, methodName, parameterTypes);
	}

	/**
	 * @return the setter method or null if does not exist
	 */
	public static Method getSetter(final Class< ? > clazz, final String propertyName)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("propertyName", propertyName);

		final String methodName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);

		final Method[] declaredMethods = clazz.getDeclaredMethods();
		for (final Method method : declaredMethods)
			if (methodName.equals(method.getName()) && method.getParameterTypes().length == 1) return method;
		LOG.trace("No setter for [%s] found on class %s.", propertyName, clazz);
		return null;
	}

	/**
	 * @return the setter method or null if does not exist
	 */
	public static Method getSetterRecursive(final Class< ? > clazz, final String propertyName)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("propertyName", propertyName);

		final Method m = getSetter(clazz, propertyName);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getSetterRecursive(superclazz, propertyName);
	}

	public static Method getSuperMethod(final Method method)
	{
		Args.notNull("method", method);

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
		Args.notNull("getter", getter);

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
		Args.notNull("clazz", clazz);
		Args.notNull("fieldName", fieldName);

		return getField(clazz, fieldName) != null;
	}

	public static boolean hasMethod(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("methodName", methodName);

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
	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(final Method method, final Object obj, final Object... args) throws InvokingMethodFailedException
	{
		Args.notNull("method", method);
		Args.notNull("obj", obj);

		try
		{
			final SetAccessibleAction act = method.isAccessible() ? null : new SetAccessibleAction(method);
			if (act != null) AccessController.doPrivileged(act);
			try
			{
				return (T) method.invoke(obj, args);
			}
			finally
			{
				if (act != null) AccessController.doPrivileged(act.setAccessible(false));
			}
		}
		catch (final Exception ex)
		{
			throw new InvokingMethodFailedException(method, obj, ex);
		}
	}

	/**
	 * @return true if objects of type <code>from</code> can be casted to type <code>to</code>
	 */
	public static boolean isCastable(final Class< ? > fromType, final Class< ? > toType)
	{
		Args.notNull("fromType", fromType);
		Args.notNull("toType", toType);

		return toType.isAssignableFrom(fromType);
	}

	public static boolean isClassPresent(final String className)
	{
		Args.notNull("className", className);

		try
		{
			Class.forName(className);
			return true;
		}
		catch (final ClassNotFoundException ex)
		{
			return false;
		}
	}

	public static boolean isFinal(final Member member)
	{
		Args.notNull("member", member);

		return (member.getModifiers() & Modifier.FINAL) != 0;
	}

	/**
	 * determines if a method is a JavaBean style getter method
	 */
	public static boolean isGetter(final Method method)
	{
		Args.notNull("method", method);

		return method.getParameterTypes().length == 0 && (method.getName().startsWith("is") || method.getName().startsWith("get"));
	}

	public static boolean isInnerClass(final Class< ? > clazz)
	{
		Args.notNull("clazz", clazz);

		return clazz.getName().indexOf('$') > -1;
	}

	public static boolean isNonStaticInnerClass(final Class< ? > clazz)
	{
		Args.notNull("clazz", clazz);

		return clazz.getName().indexOf('$') > -1 && (clazz.getModifiers() & Modifier.STATIC) == 0;
	}

	public static boolean isPackage(final Member member)
	{
		Args.notNull("member", member);

		return (member.getModifiers() & (Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED)) == 0;
	}

	public static boolean isPrivate(final Member member)
	{
		Args.notNull("member", member);

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
		Args.notNull("member", member);

		return (member.getModifiers() & Modifier.PROTECTED) != 0;
	}

	public static boolean isPublic(final Member member)
	{
		Args.notNull("member", member);

		return (member.getModifiers() & Modifier.PUBLIC) != 0;
	}

	/**
	 * determines if a method is a JavaBean style setter method
	 */
	public static boolean isSetter(final Method method)
	{
		Args.notNull("method", method);

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
		Args.notNull("member", member);

		return (member.getModifiers() & Modifier.STATIC) != 0;
	}

	public static boolean isTransient(final Member member)
	{
		Args.notNull("member", member);

		return (member.getModifiers() & Modifier.TRANSIENT) != 0;
	}

	/**
	 * determines if a method is a void method
	 */
	public static boolean isVoidMethod(final Method method)
	{
		Args.notNull("method", method);

		return method.getReturnType() == void.class;
	}

	public static Class< ? > loadClass(final String className)
	{
		Args.notNull("className", className);

		try
		{
			LOG.trace("Trying to load class [%s]...", className);
			return Class.forName(className);
		}
		catch (final ClassNotFoundException ex)
		{
			throw new ReflectionException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Class< ? extends T> loadClass(final String className, final Class<T> baseClass)
	{
		Args.notNull("className", className);
		Args.notNull("baseClass", baseClass);

		try
		{
			LOG.trace("Trying to load class [%s] extending [%s]...", className, baseClass);
			return (Class< ? extends T>) Class.forName(className);
		}
		catch (final ClassNotFoundException ex)
		{
			throw new ReflectionException(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxyInstance(final Class<T> interfaceType, final InvocationHandler handler)
	{
		Args.notNull("interfaceType", interfaceType);
		Args.notNull("handler", handler);

		return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T createProxyInstance(final Class<T> interfaceType, final InvocationHandler handler, final ClassLoader loader)
	{
		Args.notNull("interfaceType", interfaceType);
		Args.notNull("handler", handler);
		Args.notNull("loader", loader);

		return (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType}, handler);
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

		return createProxyInstance(objectInterface, new InvocationHandler()
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

	public static void setFieldValue(final Field field, final Object obj, final Object value) throws SettingFieldValueFailedException
	{
		Args.notNull("field", field);
		Args.notNull("obj", obj);

		try
		{
			if (!field.isAccessible()) AccessController.doPrivileged(new SetAccessibleAction(field));
			field.set(obj, value);
		}
		catch (final Exception ex)
		{
			throw new SettingFieldValueFailedException(field, obj, ex);
		}
	}

	public static boolean setViaSetter(final Object target, final String propertyName, final Object propertyValue)
	{
		Args.notNull("target", target);
		Args.notNull("propertyName", propertyName);

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
