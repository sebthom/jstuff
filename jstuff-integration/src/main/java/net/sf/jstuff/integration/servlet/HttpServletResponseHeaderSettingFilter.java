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
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HttpServletResponseHeaderSettingFilter implements Filter {
   private static final Logger LOG = Logger.create();

   private final Map<String, String> parameter = new LinkedHashMap<String, String>();

   public void destroy() {
   }

   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
      LOG.trace("For request %s setting HTTP response headers: %s", request, parameter);

      final HttpServletResponse res = (HttpServletResponse) response;
      for (final Entry<String, String> entry : parameter.entrySet()) {
         res.setHeader(entry.getKey(), entry.getValue());
      }

      chain.doFilter(request, response);
   }

   public void init(final FilterConfig filterConfig) throws ServletException {
      for (final String param : Enumerations.toIterable((Enumeration<String>) filterConfig.getInitParameterNames())) {
         parameter.put(param, filterConfig.getInitParameter(param));
      }
   }
}
