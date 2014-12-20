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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Proxies
{
	@SuppressWarnings("unchecked")
	public static <T> T create(final Class<T> interfaceType, final ClassLoader loader, final InvocationHandler handler)
	{
		Args.notNull("interfaceType", interfaceType);
		Args.notNull("handler", handler);
		Args.notNull("loader", loader);

		return (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType}, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(final Class<T> interfaceType, final InvocationHandler handler)
	{
		Args.notNull("interfaceType", interfaceType);
		Args.notNull("handler", handler);

		return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(final ClassLoader loader, final InvocationHandler handler, final Class< ? >... interfaceTypes)
	{
		Args.notNull("handler", handler);
		Args.notNull("loader", loader);
		Args.notEmpty("interfaceTypes", interfaceTypes);

		return (T) Proxy.newProxyInstance(loader, interfaceTypes, handler);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(final InvocationHandler handler, final Class< ? >... interfaceTypes)
	{
		Args.notNull("handler", handler);
		Args.notEmpty("interfaceTypes", interfaceTypes);

		return (T) Proxy.newProxyInstance(interfaceTypes[0].getClassLoader(), interfaceTypes, handler);
	}
}
