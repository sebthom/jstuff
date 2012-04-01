/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DuckTypeUtils
{
	private static final Logger LOG = Logger.make();

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

		return ReflectionUtils.makeProxyInstance(duckInterface, new InvocationHandler()
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

	protected DuckTypeUtils()
	{
		super();
	}
}
