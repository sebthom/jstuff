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
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.logging.Logger;

/**
 * <b>Example:</b>
<pre>{@code
<filter>
    <filter-name>RequestAttributeSettingFilter</filter-name>
    <filter-class>net.sf.jstuff.integration.servlet.RequestAttributeSettingFilter</filter-class>
    <init-param>
        <param-name>attribute1</param-name>
        <param-value>value1</param-value>
    </init-param>
    <init-param>
        <param-name>attribute1</param-name>
        <param-value>value1</param-value>
    </init-param>
</filter>
<filter-mapping>
	<filter-name>RequestAttributeSettingFilter</filter-name>
	<url-pattern>/services/*</url-pattern>
</filter-mapping>
}</pre>
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RequestAttributeSettingFilter implements Filter
{
	private static final Logger LOG = Logger.create();
	private final Map<String, String> attributes = new LinkedHashMap<String, String>();

	public void destroy()
	{}

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
			ServletException
	{
		if (!attributes.isEmpty())
		{
			LOG.debug("For request [%s] setting request attributes: %s", request, attributes);

			for (final Entry<String, String> entry : attributes.entrySet())
				request.setAttribute(entry.getKey(), entry.getValue());
		}

		chain.doFilter(request, response);
	}

	public Map<String, String> getAttributes()
	{
		return attributes;
	}

	@SuppressWarnings("unchecked")
	public void init(final FilterConfig filterConfig) throws ServletException
	{
		for (final String param : Enumerations.toIterable((Enumeration<String>) filterConfig.getInitParameterNames()))
			attributes.put(param, filterConfig.getInitParameter(param));
	}
}
