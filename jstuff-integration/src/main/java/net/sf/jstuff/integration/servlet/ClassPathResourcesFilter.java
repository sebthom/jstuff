/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.net.NetUtils;

/**
 * <b>Example:</b>
 *
 * <pre>
 * {@code
<filter>
    <filter-name>ClassPathResourcesFilter</filter-name>
    <filter-class>net.sf.jstuff.integration.servlet.ClassPathResourcesFilter</filter-class>
    <init-param>
        <param-name>max-age-in-seconds</param-name>
        <param-value>3600</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>ClassPathResourcesFilter</filter-name>
    <url-pattern>*.jpg</url-pattern>
</filter-mapping>
}
 * </pre>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ClassPathResourcesFilter implements Filter {
   private static final Logger LOG = Logger.create();

   public static final int DEFAULT_CACHE_TIME_IN_SEC = 60 * 60 * 24; // one day

   public static URL findResourceInClassPath(final String path) {
      final ClassLoader cl = Thread.currentThread().getContextClassLoader();
      URL resource = cl.getResource(path);
      if (resource == null && path.startsWith("/")) {
         resource = cl.getResource(path.substring(1));
      }
      return resource;
   }

   protected ServletContext ctx;

   protected int maxAgeInSeconds;

   public ClassPathResourcesFilter() {
      LOG.infoNew(this);
   }

   @Override
   public void destroy() {
   }

   @Override
   @SuppressWarnings("resource")
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
      if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
         chain.doFilter(request, response);
         return;
      }

      final HttpServletRequest req = (HttpServletRequest) request;
      final HttpServletResponse resp = (HttpServletResponse) response;

      String resourcePath = req.getServletPath();
      if (resourcePath == null) {
         resourcePath = req.getPathInfo();
      }

      URL resource = ctx.getResource(resourcePath);
      // if resource was found on file system simply continue
      if (resource != null) {
         chain.doFilter(request, response);
         return;
      }

      // try to lookup the resource in the classpath
      resource = findResourceInClassPath(resourcePath);

      // resource not found in classpath
      if (resource == null) {
         chain.doFilter(request, response);
         return;
      }

      // check for if-modified-since, prior to any other headers
      long ifModifiedSince = 0;
      try {
         ifModifiedSince = req.getDateHeader("If-Modified-Since");
      } catch (final IllegalArgumentException ex) {
         // ignore if date is unconvertible
      }

      final long lastModified = NetUtils.getLastModified(resource);
      final long now = System.currentTimeMillis();
      final long expires = now + maxAgeInSeconds * 1000;

      if (ifModifiedSince > 0 && ifModifiedSince <= lastModified) {
         resp.setDateHeader("Expires", expires);
         resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      } else {
         resp.setDateHeader("Date", now);
         resp.setDateHeader("Last-Modified", lastModified);

         // cache control
         resp.setDateHeader("Expires", expires);
         resp.setHeader("Cache-Control", "public"); // public = caching in proxies is allowed, private = only caching in clients
         resp.addHeader("Cache-Control", "max-age=" + maxAgeInSeconds);
         resp.addHeader("Cache-Control", "must-revalidate");

         try (InputStream in = resource.openStream()) {
            resp.setHeader("Content-Length", String.valueOf(in.available()));
            IOUtils.copyLarge(in, response.getOutputStream());
         }
      }
   }

   public int getMaxAgeInSeconds() {
      return maxAgeInSeconds;
   }

   @Override
   public void init(final FilterConfig cfg) throws ServletException {
      ctx = cfg.getServletContext();

      maxAgeInSeconds = DEFAULT_CACHE_TIME_IN_SEC;

      final String maxAge = cfg.getInitParameter("max-age-in-seconds");
      if (maxAge != null) {
         maxAgeInSeconds = Integer.parseInt(maxAge);
      }
   }

   public void setMaxAgeInSeconds(final int maxAgeInSeconds) {
      this.maxAgeInSeconds = maxAgeInSeconds;
   }
}
