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
package net.sf.jstuff.integration.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.ObjectCache;
import net.sf.jstuff.core.StringUtils;

import org.springframework.beans.BeansException;
import org.springframework.web.HttpRequestHandler;
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
 *    &lt;servlet-name&gt;httpInvokerServicesServlet&lt;/servlet-name&gt;
 *    &lt;servlet-class&gt;net.sf.jstuff.integration.spring.SpringHttpInvokerServicesServlet&lt;/servlet-class&gt; 
 * &lt;/servlet&gt;
 * &lt;servlet-mapping&gt;
 *    &lt;servlet-name&gt;httpInvokerServicesServlet&lt;/servlet-name&gt; 
 *    &lt;url-pattern&gt;/remoting/*&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;
 * </pre>
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringHttpInvokerServicesServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Logger.make();

	private final ObjectCache<String, HttpRequestHandler> mappedBeans = new ObjectCache<String, HttpRequestHandler>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void service(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException
	{
		final String reqURI = request.getRequestURI();
		HttpRequestHandler bean = mappedBeans.get(reqURI);

		if (bean == null)
		{
			final String beanName = StringUtils.substringAfterLast(reqURI, "/");
			try
			{
				bean = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext()).getBean(
						'/' + beanName, HttpRequestHandler.class);
			}
			catch (final BeansException ex)
			{
				LOG.error("Unexpected exception occured while retrieving bean [%s].", ex, beanName);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			mappedBeans.put(reqURI, bean);
		}
		bean.handleRequest(request, response);
	}
}
