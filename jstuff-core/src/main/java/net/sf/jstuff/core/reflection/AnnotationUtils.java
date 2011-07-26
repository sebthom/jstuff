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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jstuff.core.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AnnotationUtils extends org.apache.commons.lang3.AnnotationUtils
{
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

		if (!inspectInterfaces || !ReflectionUtils.isPublic(method)) return method.getAnnotations();

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

	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A getDefaults(final Class<A> annotation)
	{
		return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[]{annotation}, new InvocationHandler()
			{
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					return method.getDefaultValue();
				}
			});
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

		if (!inspectInterfaces || !ReflectionUtils.isPublic(method)) return method.getParameterAnnotations();

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

	public static Map<String, Object> getParameters(final Annotation annotation) throws ReflectionException
	{
		final Method[] methods = annotation.annotationType().getDeclaredMethods();
		final Map<String, Object> parameters = new HashMap<String, Object>(methods.length);
		for (final Method m : methods)
			try
			{
				parameters.put(m.getName(), m.invoke(annotation));
			}
			catch (final Exception ex)
			{
				throw new InvokingMethodFailedException(m, annotation, ex);
			}
		return parameters;
	}

	/**
	 * Returns true if an annotation for the specified type is present on this method, else false. 
	 *  
	 * @param method the method to inspect
	 * @param annotationClass the Class object corresponding to the annotation type
	 * @param inspectInterfaces whether to also check annotations declared on interface method declaration
	 * @return true if an annotation for the specified annotation type is present on this method, else false
	 */
	public static boolean hasAnnotation(final Method method, final Class< ? extends Annotation> annotationClass,
			final boolean inspectInterfaces)
	{
		Assert.argumentNotNull("method", method);
		Assert.argumentNotNull("annotationClass", annotationClass);

		if (method.isAnnotationPresent(annotationClass)) return true;

		if (!inspectInterfaces || !ReflectionUtils.isPublic(method)) return false;

		final String methodName = method.getName();
		final Class< ? >[] methodParameterTypes = method.getParameterTypes();

		for (final Class< ? > next : ReflectionUtils.getInterfacesRecursive(method.getDeclaringClass()))
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

	public static <T extends Annotation> T makeAnnotationInstance(final Class<T> annotationType) throws ReflectionException
	{
		return makeAnnotationInstance(annotationType, null);
	}

	public static <T extends Annotation> T makeAnnotationInstance(final Class<T> annotationType,
			final Map<String, Object> attributes) throws ReflectionException
	{
		/*
		 * build the final map of attributes to be used
		 */
		final Map<String, Object> attrValues = new HashMap<String, Object>();
		int count = 0;
		for (final Method m : annotationType.getDeclaredMethods())
		{
			final String attrName = m.getName();
			if (attributes != null && attributes.containsKey(attrName))
			{
				attrValues.put(attrName, attributes.get(attrName));
				count++;
			}
			else
			{
				final Object defaultValue = m.getDefaultValue();
				if (defaultValue == null)
					throw new IllegalArgumentException("Missing value for required annotation parameter [" + attrName
							+ "]");
				attrValues.put(attrName, defaultValue);
			}
		}
		if (attributes != null && count != attributes.size())
			throw new IllegalArgumentException("Parameter [attributes] contains attributes not present in annotation  "
					+ annotationType);

		/*
		 * try to create the dynamic annotation instance
		 */
		try
		{
			return ReflectionUtils.makeProxyInstance(annotationType, new InvocationHandler()
				{
					public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
					{
						final String name = method.getName();
						if ("hashCode".equals(name) && args == null && method.getReturnType() == int.class)
							return AnnotationUtils.hashCode((Annotation) proxy);
						if ("equals".equals(name) && args.length == 1 && method.getReturnType() == boolean.class)
							return AnnotationUtils.equals((Annotation) proxy, (Annotation) args[0]);
						if ("toString".equals(name) && args == null && method.getReturnType() == String.class)
							return AnnotationUtils.toString((Annotation) proxy);
						if ("annotationType".equals(name) && args == null && method.getReturnType() == Class.class)
							return annotationType;
						return attrValues.get(method.getName());
					}
				});
		}
		catch (final Exception ex)
		{
			throw new ReflectionException("Failed to create an instance of annotation " + annotationType, ex);
		}
	}

	protected AnnotationUtils()
	{
		super();
	}
}
