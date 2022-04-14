/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
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
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class HttpServletResponseHeaderSettingFilter implements Filter {
   private static final Logger LOG = Logger.create();

   private final Map<String, String> parameter = new LinkedHashMap<>();

   @Override
   public void destroy() {
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
      LOG.trace("For request %s setting HTTP response headers: %s", request, parameter);

      final HttpServletResponse res = (HttpServletResponse) response;
      for (final Entry<String, String> entry : parameter.entrySet()) {
         res.setHeader(entry.getKey(), entry.getValue());
      }

      chain.doFilter(request, response);
   }

   @Override
   public void init(final FilterConfig filterConfig) throws ServletException {
      for (final String param : Enumerations.toIterable(filterConfig.getInitParameterNames())) {
         parameter.put(param, filterConfig.getInitParameter(param));
      }
   }
}
