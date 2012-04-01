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
package net.sf.jstuff.integration.rest;

import java.lang.reflect.Method;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RESTResourceAction
{
	private final HTTPRequestMethod httpMethod;
	private final Method method;
	private final String resource;
	private final RESTResourceAction fallbackFor;

	public RESTResourceAction(final Method method, final String resource, final HTTPRequestMethod httpMethod)
	{
		this.method = method;
		this.resource = resource;
		this.httpMethod = httpMethod;
		this.fallbackFor = null;
	}

	public RESTResourceAction(final Method method, final String resource, final HTTPRequestMethod httpMethod,
			final RESTResourceAction fallbackFor)
	{
		this.method = method;
		this.resource = resource;
		this.httpMethod = httpMethod;
		this.fallbackFor = fallbackFor;
	}

	/**
	 * @return the fallbackFor
	 */
	public RESTResourceAction getFallbackFor()
	{
		return fallbackFor;
	}

	/**
	 * @return the httpMethod
	 */
	public HTTPRequestMethod getHttpMethod()
	{
		return httpMethod;
	}

	/**
	 * @return the method
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * @return the resource
	 */
	public String getResource()
	{
		return resource;
	}

	public boolean isFallback()
	{
		return fallbackFor != null;
	}

	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + ": resource=" + resource + "; httpMethod = " + httpMethod
				+ "; method=" + method;
	}
}
