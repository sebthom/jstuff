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

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RequestAttributeSettingFilter implements Filter
{
	private static final Logger LOG = Logger.create();
	private final Map<String, String> parameter = new LinkedHashMap<String, String>();

	public void destroy()
	{}

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{
		LOG.trace("For request %s setting request attributes: %s", request, parameter);

		for (final Entry<String, String> entry : parameter.entrySet())
			request.setAttribute(entry.getKey(), entry.getValue());

		chain.doFilter(request, response);
	}

	@SuppressWarnings("unchecked")
	public void init(final FilterConfig filterConfig) throws ServletException
	{
		final Enumeration<String> paramNames = filterConfig.getInitParameterNames();
		while (paramNames.hasMoreElements())
		{
			final String key = paramNames.nextElement();
			parameter.put(key, filterConfig.getInitParameter(key));
		}
	}
}
