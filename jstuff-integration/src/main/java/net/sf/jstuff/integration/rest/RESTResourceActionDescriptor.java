/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RESTResourceActionDescriptor implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String httpRequestMethod;
	private boolean isFallbackMethod;
	private String mappedServiceMethod;
	private String requestBodyType;
	private String requestURL;
	private String responseBodyType;

	public RESTResourceActionDescriptor()
	{}

	/**
	 * @param isFallbackMethod
	 */
	public RESTResourceActionDescriptor(final boolean isFallbackMethod)
	{
		this.isFallbackMethod = isFallbackMethod;
	}

	/**
	 * @return the httpRequestMethod
	 */
	public String getHttpRequestMethod()
	{
		return httpRequestMethod;
	}

	/**
	 * @return the isFallbackMethod
	 */
	public boolean getIsFallbackMethod()
	{
		return isFallbackMethod;
	}

	/**
	 * @return the mappedServiceMethod
	 */
	public String getMappedServiceMethod()
	{
		return mappedServiceMethod;
	}

	/**
	 * @return the requestBodyType
	 */
	public String getRequestBodyType()
	{
		return requestBodyType;
	}

	/**
	 * @return the requestURL
	 */
	public String getRequestURL()
	{
		return requestURL;
	}

	/**
	 * @return the responseBodyType
	 */
	public String getResponseBodyType()
	{
		return responseBodyType;
	}

	/**
	 * @param httpRequestMethod the httpRequestMethod to set
	 */
	public void setHttpRequestMethod(final String httpRequestMethod)
	{
		this.httpRequestMethod = httpRequestMethod;
	}

	/**
	 * @param isFallbackMethod the isFallbackMethod to set
	 */
	public void setIsFallbackMethod(final boolean isFallbackMethod)
	{
		this.isFallbackMethod = isFallbackMethod;
	}

	/**
	 * @param mappedServiceMethod the mappedServiceMethod to set
	 */
	public void setMappedServiceMethod(final String mappedServiceMethod)
	{
		this.mappedServiceMethod = mappedServiceMethod;
	}

	/**
	 * @param requestBodyType the requestBodyType to set
	 */
	public void setRequestBodyType(final String requestBodyType)
	{
		this.requestBodyType = requestBodyType;
	}

	/**
	 * @param requestURL the requestURL to set
	 */
	public void setRequestURL(final String requestURL)
	{
		this.requestURL = requestURL;
	}

	/**
	 * @param responseBodyType the responseBodyType to set
	 */
	public void setResponseBodyType(final String responseBodyType)
	{
		this.responseBodyType = responseBodyType;
	}

}
