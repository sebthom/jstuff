/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RequestResponseHoldingFilter implements Filter {
   private static final ThreadLocal<ServletRequest> REQ = new ThreadLocal<ServletRequest>();
   private static final ThreadLocal<ServletResponse> RESP = new ThreadLocal<ServletResponse>();

   public static HttpServletRequest getHttpServletRequest() {
      final ServletRequest req = REQ.get();
      if (req instanceof HttpServletRequest)
         return (HttpServletRequest) req;
      return null;
   }

   public static HttpServletResponse getHttpServletResponse() {
      final ServletResponse resp = RESP.get();
      if (resp instanceof HttpServletResponse)
         return (HttpServletResponse) resp;
      return null;
   }

   public static ServletRequest getServletRequest() {
      return REQ.get();
   }

   public static ServletResponse getServletResponse() {
      return RESP.get();
   }

   public void destroy() {
   }

   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
      REQ.set(request);
      try {
         RESP.set(response);
         try {
            chain.doFilter(request, response);
         } finally {
            RESP.remove();
         }
      } finally {
         REQ.remove();
      }
   }

   public void init(final FilterConfig filterConfig) throws ServletException {
   }
}
