/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jdt.annotation.Nullable;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.net.NetUtils;

/**
 * <b>Example:</b>
 *
 * <pre>
 * {@code
 * <filter>
 *     <filter-name>ClassPathResourcesFilter</filter-name>
 *     <filter-class>net.sf.jstuff.integration.servlet.ClassPathResourcesFilter</filter-class>
 *     <init-param>
 *         <param-name>max-age-in-seconds</param-name>
 *         <param-value>3600</param-value>
 *     </init-param>
 * </filter>
 * <filter-mapping>
 *     <filter-name>ClassPathResourcesFilter</filter-name>
 *     <url-pattern>*.jpg</url-pattern>
 * </filter-mapping>
 * }
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ClassPathResourcesFilter implements Filter {
   private static final Logger LOG = Logger.create();

   public static final int DEFAULT_CACHE_TIME_IN_SEC = 60 * 60 * 24; // one day

   public static @Nullable URL findResourceInClassPath(final String path) {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if (cl == null) {
         cl = ClassPathResourcesFilter.class.getClassLoader();
      }
      if (cl == null) {
         cl = ClassLoader.getSystemClassLoader();
      }
      URL resource = cl.getResource(path);
      if (resource == null && path.startsWith("/")) {
         resource = cl.getResource(path.substring(1));
      }
      return resource;
   }

   protected ServletContext ctx = lateNonNull();

   protected int maxAgeInSeconds;

   public ClassPathResourcesFilter() {
      LOG.infoNew(this);
   }

   @Override
   public void destroy() {
   }

   @Override
   @SuppressWarnings("resource")
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
         ServletException {
      if (!(request instanceof final HttpServletRequest httpRequest) || !(response instanceof HttpServletResponse)) {
         chain.doFilter(request, response);
         return;
      }

      final String resourcePath = httpRequest.getServletPath();

      URL resource = ctx.getResource(resourcePath);
      // if resource was found on file system simply continue
      if (resource != null) {
         chain.doFilter(httpRequest, response);
         return;
      }

      // try to lookup the resource in the classpath
      resource = findResourceInClassPath(resourcePath);

      // resource not found in classpath
      if (resource == null) {
         chain.doFilter(httpRequest, response);
         return;
      }

      // check for if-modified-since, prior to any other headers
      long ifModifiedSince = 0;
      try {
         ifModifiedSince = httpRequest.getDateHeader("If-Modified-Since");
      } catch (final IllegalArgumentException ex) {
         // ignore if date is unconvertible
      }

      final long lastModified = NetUtils.getLastModified(resource);
      final long now = System.currentTimeMillis();
      final long expires = now + maxAgeInSeconds * 1000;

      final HttpServletResponse resp = (HttpServletResponse) response;
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
