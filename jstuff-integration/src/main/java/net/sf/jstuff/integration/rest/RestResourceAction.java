/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.jstuff.core.reflection.SerializableMethod;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RestResourceAction implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final RestResourceAction fallbackFor;
	private final HttpRequestMethod httpRequestMethod;
	private final Class< ? > httpRequestBodyType;
	private final SerializableMethod serviceMethod;
	private final String[] parameterNames;
	private final String resourceName;
	private final int requiredURLParameterCount;
	private final String requestURITemplate;

	public RestResourceAction(final String resource, final HttpRequestMethod httpRequestMethod, final Method serviceMethod,
			final String[] parameterNames)
	{
		this(resource, httpRequestMethod, serviceMethod, parameterNames, null);
	}

	public String[] getParameterNames()
	{
		return parameterNames;
	}

	public RestResourceAction(final String resourceName, final HttpRequestMethod httpRequestMethod, final Method serviceMethod,
			final String[] parameterNames, final RestResourceAction fallbackFor)
	{
		Args.notNull("resource", resourceName);
		Args.notNull("httpRequestMethod", httpRequestMethod);
		Args.notNull("method", serviceMethod);
		Args.notNull("parameterNames", parameterNames);

		this.serviceMethod = SerializableMethod.get(serviceMethod);
		this.resourceName = resourceName;
		this.httpRequestMethod = httpRequestMethod;
		this.fallbackFor = fallbackFor;
		this.parameterNames = parameterNames;

		final int paramCount = serviceMethod.getParameterTypes().length;

		final boolean isPOST = httpRequestMethod == HttpRequestMethod.POST;
		final boolean isPUT = httpRequestMethod == HttpRequestMethod.PUT;

		if (isPUT || isPOST)
		{
			if (paramCount == 0)
				throw new IllegalArgumentException("HTTP Request Method [" + httpRequestMethod
						+ "] requires a service method with at least one parameter. ResourceId= " + resourceName + ", Service Method="
						+ serviceMethod);

			if (paramCount < 2)
				requestURITemplate = resourceName;
			else
				requestURITemplate = resourceName + "/${" + StringUtils.join(ArrayUtils.remove(parameterNames, paramCount - 1), "}/${")
						+ "}";

			// for POST and PUT the last parameter is supposed to be submitted as HTTP Body Message
			requiredURLParameterCount = paramCount - 1 - (serviceMethod.isVarArgs() ? 1 : 0);
			httpRequestBodyType = serviceMethod.getParameterTypes()[paramCount - 1];
		}
		else
		{
			if (paramCount < 1)
				requestURITemplate = resourceName;
			else
				requestURITemplate = resourceName + "/${" + StringUtils.join(parameterNames, "}/${") + "}";

			requiredURLParameterCount = paramCount - (serviceMethod.isVarArgs() ? 1 : 0);
			httpRequestBodyType = null;
		}
	}

	public String getRequestURITemplate()
	{
		return requestURITemplate;
	}

	/**
	 * @return the fallbackFor
	 */
	public RestResourceAction getFallbackFor()
	{
		return fallbackFor;
	}

	public Class< ? > getHttpRequestBodyType()
	{
		return httpRequestBodyType;
	}

	public Class< ? > getHttpResponseBodyType()
	{
		return serviceMethod.getMethod().getReturnType();
	}

	/**
	 * @return the httpMethod
	 */
	public HttpRequestMethod getHttpRequestMethod()
	{
		return httpRequestMethod;
	}

	/**
	 * @return the method
	 */
	public Method getServiceMethod()
	{
		return serviceMethod.getMethod();
	}

	public String getServiceMethodSignature()
	{
		if (parameterNames.length == 0) return serviceMethod.getDeclaringClass().getSimpleName() + "." + serviceMethod.getName() + "()";
		return serviceMethod.getDeclaringClass().getSimpleName() + "." + serviceMethod.getName() + "("
				+ StringUtils.join(parameterNames, ",") + ")";
	}

	public String getResourceName()
	{
		return resourceName;
	}

	public int getRequiredURLParameterCount()
	{
		return requiredURLParameterCount;
	}

	public boolean isFallback()
	{
		return fallbackFor != null;
	}

	public boolean isFallBackMethod()
	{
		return fallbackFor != null;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": resource=" + resourceName + "; httpMethod = " + httpRequestMethod + "; method="
				+ serviceMethod;
	}
}
