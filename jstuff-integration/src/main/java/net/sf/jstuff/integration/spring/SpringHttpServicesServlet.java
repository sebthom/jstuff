/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.integration.rest.AbstractRestServiceExporter;

/**
 * <u>applicationContext.xml:</u>
 * 
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
 * 
 * <u>web.xml</u>:
 * 
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
public class SpringHttpServicesServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private static final Logger LOG = Logger.create();

   private boolean showIndex = true;

   public SpringHttpServicesServlet() {
      LOG.infoNew(this);
   }

   @Override
   protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
      final String relativePath = request.getPathInfo();

      final WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

      if (relativePath == null || "/".equals(relativePath)) {
         if (showIndex) {
            final Map<String, HttpRequestHandler> beans = springContext.getBeansOfType(HttpRequestHandler.class);
            final PrintWriter pw = response.getWriter();
            pw.write("<html><body>");
            pw.write("<h3>Available services:</h3>");
            pw.write("<ul>");
            String contextRoot = request.getRequestURI();
            if (!contextRoot.endsWith("/")) {
               contextRoot += "/";
            }
            for (final Entry<String, HttpRequestHandler> entry : beans.entrySet()) {
               if (!entry.getKey().startsWith("/")) {
                  continue;
               }

               final String serviceID = entry.getKey().substring(1);
               final String parameter = entry.getValue() instanceof AbstractRestServiceExporter ? "?explainAsHTML" : "";

               pw.write("<li>");
               pw.write("<a href=\"" + contextRoot + serviceID + parameter + "\">" + serviceID + "</a>");
               pw.write("</li>");
            }
            pw.write("</ul></body></html>");
            pw.flush();
            pw.close();
         } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
         }
         return;
      }

      try {
         // going backwards to the relativePath to find the best matching bean
         String beanName = relativePath;
         while (beanName.contains("/")) {
            if (springContext.containsBean(beanName)) {
               request.setAttribute("beanName", beanName);
               springContext.getBean(beanName, HttpRequestHandler.class).handleRequest(request, response);
               return;
            }
            beanName = StringUtils.substringBeforeLast(beanName, "/");
         }
      } catch (final BeansException ex) {
         LOG.error(ex, "Unexpected exception occured while retrieving bean [%s].", relativePath);
      }
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
   }

   public void setShowIndex(final boolean showIndex) {
      this.showIndex = showIndex;
   }
}
