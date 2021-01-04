/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
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

import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.logging.Logger;

/**
 * <b>Example:</b>
 *
 * <pre>
 * {@code
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
}
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class RequestAttributeSettingFilter implements Filter {
   private static final Logger LOG = Logger.create();
   private final Map<String, String> attributes = new LinkedHashMap<>();

   @Override
   public void destroy() {
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
      if (!attributes.isEmpty()) {
         LOG.debug("For request [%s] setting request attributes: %s", request, attributes);

         for (final Entry<String, String> entry : attributes.entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
         }
      }

      chain.doFilter(request, response);
   }

   public Map<String, String> getAttributes() {
      return attributes;
   }

   @Override
   public void init(final FilterConfig filterConfig) throws ServletException {
      for (final String param : Enumerations.toIterable(filterConfig.getInitParameterNames())) {
         attributes.put(param, filterConfig.getInitParameter(param));
      }
   }
}
