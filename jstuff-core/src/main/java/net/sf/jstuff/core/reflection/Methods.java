/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Methods extends Members
{
	private static final Logger LOG = Logger.create();

	public static boolean exists(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
	{
		return find(clazz, methodName, parameterTypes) != null;
	}

	/**
	 * @return the method or null if the method does not exist
	 */
	public static Method find(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
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

	public static Method findGetter(final Class< ? > clazz, final String propertyName)
	{
		return findGetter(clazz, propertyName, null);
	}

	public static Method findGetter(final Class< ? > clazz, final String propertyName, final Class< ? > compatibleTo)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("propertyName", propertyName);

		final String appendix = propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);

		if (compatibleTo == Boolean.class || compatibleTo == boolean.class) try
		{
			final Method getter = clazz.getDeclaredMethod("is" + appendix);
			if (Types.isAssignableTo(getter.getReturnType(), compatibleTo)) return getter;
		}
		catch (final NoSuchMethodException ex)
		{
			// ignore
		}

		try
		{
			final Method getter = clazz.getDeclaredMethod("get" + appendix);
			if (getter != null)
			{
				if (compatibleTo == null) return getter;
				if (Types.isAssignableTo(getter.getReturnType(), compatibleTo)) return getter;
			}
		}
		catch (final NoSuchMethodException ex)
		{
			// ignore
		}

		LOG.trace("No getter for [%s] found in class [%s] compatible to [%s].", propertyName, clazz, compatibleTo);
		return null;
	}

	public static Method findGetterRecursive(final Class< ? > clazz, final String propertyName)
	{
		return findGetterRecursive(clazz, propertyName, null);
	}

	public static Method findGetterRecursive(final Class< ? > clazz, final String propertyName, final Class< ? > compatibleTo)
	{
		final Method m = findGetter(clazz, propertyName, compatibleTo);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return findGetterRecursive(superclazz, propertyName, compatibleTo);
	}

	/**
	 * @return the method or null if the method does not exist
	 */
	public static Method findRecursive(final Class< ? > clazz, final String methodName, final Class< ? >... parameterTypes)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("methodName", methodName);

		final Method m = find(clazz, methodName, parameterTypes);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return findRecursive(superclazz, methodName, parameterTypes);
	}

	/**
	 * @return the first found setter method or null if does not exist
	 */
	public static Method findSetter(final Class< ? > clazz, final String propertyName)
	{
		return findSetter(clazz, propertyName, null);
	}

	/**
	 * @return the first found compatible setter method or null if does not exist
	 */
	public static Method findSetter(final Class< ? > clazz, final String propertyName, final Class< ? > compatibleTo)
	{
		Args.notNull("clazz", clazz);
		Args.notNull("propertyName", propertyName);

		final String methodName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.getDefault()) + propertyName.substring(1);

		final Method[] declaredMethods = clazz.getDeclaredMethods();
		for (final Method method : declaredMethods)
			if (!isStatic(method) && methodName.equals(method.getName()) && method.getParameterTypes().length == 1)
			{
				if (compatibleTo == null) return method;
				if (Types.isAssignableTo(compatibleTo, method.getParameterTypes()[0])) return method;
			}
		LOG.trace("No setter for [%s] found in class [%s] comaptible to [%s].", propertyName, clazz, compatibleTo);
		return null;
	}

	/**
	 * @return the first found setter method or null if does not exist
	 */
	public static Method findSetterRecursive(final Class< ? > clazz, final String propertyName)
	{
		return findSetterRecursive(clazz, propertyName, null);
	}

	/**
	 * @return the first found compatible setter method or null if does not exist
	 */
	public static Method findSetterRecursive(final Class< ? > clazz, final String propertyName, final Class< ? > compatibleTo)
	{
		final Method m = findSetter(clazz, propertyName, compatibleTo);
		if (m != null) return m;

		final Class< ? > superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return findSetterRecursive(superclazz, propertyName, compatibleTo);
	}

	public static List<Method> getGetters(final Class< ? > clazz)
	{
		final List<Method> result = CollectionUtils.newArrayList();

		for (final Method m : clazz.getMethods())
			if (isGetter(m)) result.add(m);
		return result;
	}

	public static List<Method> getGettersRecursive(final Class< ? > clazz)
	{
		final List<Method> result = CollectionUtils.newArrayList();
		Class< ? > currentClass = clazz;

		while (currentClass != null && currentClass != Object.class)
		{
			currentClass = currentClass.getSuperclass();

			for (final Method m : clazz.getMethods())
				if (isGetter(m)) result.add(m);
		}
		return result;
	}

	/**
	 * @return a list of all methods declared on super type interfaces this method implements/overrides
	 */
	public static List<Method> getInterfaceMethods(final Method method)
	{
		Args.notNull("method", method);

		// static methods cannot be overridden
		if (isStatic(method)) return null;

		final Set<Class< ? >> interfaces = Types.getInterfacesRecursive(method.getDeclaringClass());
		if (interfaces.size() == 0) return null;

		final String methodName = method.getName();
		final Class< ? >[] parameterTypes = method.getParameterTypes();

		final List<Method> methods = newArrayList();
		for (final Class< ? > iface : interfaces)
		{
			final Method m = find(iface, methodName, parameterTypes);
			if (m != null) methods.add(m);
		}
		return methods;
	}

	public static Method getSuper(final Method method)
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

			final Method m = find(currentClass, methodName, parameterTypes);
			if (m != null && !isPrivate(m)) return m;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(final Object obj, final Method method, final Object... args) throws InvokingMethodFailedException
	{
		Args.notNull("obj", obj);
		Args.notNull("method", method);

		try
		{
			ensureAccessible(method);
			return (T) method.invoke(obj, args);
		}
		catch (final Exception ex)
		{
			throw new InvokingMethodFailedException(method, obj, ex);
		}
	}

	/**
	 * determines if a method is a JavaBean style getter method
	 */
	public static boolean isGetter(final Method method)
	{
		Args.notNull("method", method);

		if (method.getParameterTypes().length > 0) return false;

		if (isReturningVoid(method)) return false;

		final String methodName = method.getName();

		if (methodName.startsWith("get"))
		{
			if (methodName.length() == 3) return false;
			if (!Character.isUpperCase(methodName.charAt(3))) return false;
			return true;
		}
		if (methodName.startsWith("is"))
		{
			if (methodName.length() == 2) return false;
			if (!Character.isUpperCase(methodName.charAt(2))) return false;
			return method.getReturnType() == Boolean.class || method.getReturnType() == boolean.class;
		}
		return false;
	}

	/**
	 * determines if a method is a void method
	 */
	public static boolean isReturningVoid(final Method method)
	{
		Args.notNull("method", method);

		return method.getReturnType() == void.class;
	}

	/**
	 * determines if a method is a JavaBean style setter method
	 */
	public static boolean isSetter(final Method method)
	{
		Args.notNull("method", method);

		final Class< ? >[] methodParameterTypes = method.getParameterTypes();

		if (methodParameterTypes.length != 1) return false;

		if (!isReturningVoid(method) && !Types.isAssignableTo(method.getReturnType(), method.getDeclaringClass())) return false;

		final String methodName = method.getName();

		if (methodName.length() < 4) return false;

		if (!methodName.startsWith("set")) return false;

		if (!Character.isUpperCase(methodName.charAt(3))) return false;

		return true;
	}

}
