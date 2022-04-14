/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RequestResponseHoldingFilter implements Filter {
   private static final ThreadLocal<ServletRequest> REQ = new ThreadLocal<>();
   private static final ThreadLocal<ServletResponse> RESP = new ThreadLocal<>();

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

   @Override
   public void destroy() {
   }

   @Override
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

   @Override
   public void init(final FilterConfig filterConfig) throws ServletException {
   }
}
