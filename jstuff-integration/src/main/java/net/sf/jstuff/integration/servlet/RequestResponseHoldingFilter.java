/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.IOException;

import org.eclipse.jdt.annotation.Nullable;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RequestResponseHoldingFilter implements Filter {
   private static final ThreadLocal<ServletRequest> REQ = new ThreadLocal<>();
   private static final ThreadLocal<ServletResponse> RESP = new ThreadLocal<>();

   public static @Nullable HttpServletRequest getHttpServletRequest() {
      final ServletRequest req = REQ.get();
      if (req instanceof final HttpServletRequest httpReq)
         return httpReq;
      return null;
   }

   public static @Nullable HttpServletResponse getHttpServletResponse() {
      final ServletResponse resp = RESP.get();
      if (resp instanceof final HttpServletResponse httpResp)
         return httpResp;
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
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
         ServletException {
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
