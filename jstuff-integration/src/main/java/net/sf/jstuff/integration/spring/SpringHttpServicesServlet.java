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
package net.sf.jstuff.integration.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.integration.rest.AbstractRestServiceExporter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <u>applicationContext.xml:</u>
 * <pre>
 *   &lt;bean name="/UserService" class="org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter" lazy-init="false"&gt;
 *     &lt;property name="service" ref="userService" /&gt;
 *     &lt;property name="serviceInterface" value="com.acme.services.UserService" /&gt;
 *   &lt;/bean&gt;
 *   &lt;bean name="/GroupService" class="org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter" lazy-init="false"&gt;
 *     &lt;property name="service" ref="groupService" /&gt;
 *     &lt;property name="serviceInterface" value="com.acme.services.GroupService" /&gt;
 *   &lt;/bean>
 * </pre>
 * <u>web.xml</u>:
 * <pre>
 * &lt;servlet&gt;
 *    &lt;servlet-name&gt;httpServicesServlet&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;net.sf.jstuff.integration.spring.SpringHttpServicesServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;httpServicesServlet&lt;/servlet-name&gt;
 *    &lt;url-pattern&gt;/remoting/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringHttpServicesServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.create();

	private boolean showIndex = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
	{
		final String relativePath = request.getPathInfo();

		final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

		System.out.println(request.getContextPath());
		if (relativePath == null || "/".equals(relativePath))
		{
			if (showIndex == true)
			{
				final Map<String, HttpRequestHandler> beans = springContext.getBeansOfType(HttpRequestHandler.class);
				final PrintWriter pw = response.getWriter();
				pw.write("<html><body><ul>");
				for (final Entry<String, HttpRequestHandler> entry : beans.entrySet())
				{
					if (!entry.getKey().startsWith("/")) continue;

					final String parameter = entry.getValue() instanceof AbstractRestServiceExporter ? "?explainAsHTML" : "";

					pw.write("<li>");
					pw.write("<a href=\"" + entry.getKey().substring(1) + parameter + "\">" + entry.getKey().substring(1) + "</a>");
					pw.write("</li>");
				}
				pw.write("</ul></body></html>");
				pw.flush();
				pw.close();
			}
			else
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		try
		{
			String beanName = relativePath;
			while (beanName.contains("/"))
			{
				if (springContext.containsBean(beanName))
				{
					request.setAttribute("relativePath", StringUtils.substringAfter(relativePath, beanName));
					springContext.getBean(beanName, HttpRequestHandler.class).handleRequest(request, response);
					return;
				}
				beanName = StringUtils.substringBeforeLast(beanName, "/");
			}
		}
		catch (final BeansException ex)
		{
			LOG.error("Unexpected exception occured while retrieving bean [%s].", ex, relativePath);
		}
		response.sendError(HttpServletResponse.SC_NOT_FOUND);
		return;
	}

	public void setShowIndex(final boolean showIndex)
	{
		this.showIndex = showIndex;
	}
}
