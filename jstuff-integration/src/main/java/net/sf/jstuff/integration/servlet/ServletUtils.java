/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ServletUtils {

   @SuppressWarnings("unchecked")
   public static <T> T getAttribute(final ServletRequest request, final String name) {
      return (T) request.getAttribute(name);
   }

   public static @Nullable X509Certificate getClientCertificate(final ServletRequest request) {
      Args.notNull("request", request);

      final X509Certificate[] certChain = getAttribute(request, "javax.servlet.request.X509Certificate");
      if (certChain == null || certChain.length == 0)
         return null;
      return certChain[0];
   }

   public static String getContextURL(final HttpServletRequest request) {
      Args.notNull("request", request);

      final String scheme = request.getScheme();
      final int port = request.getServerPort();
      final String urlPath = request.getContextPath();

      final var url = new StringBuilder();
      url.append(scheme); // http, https
      url.append("://");
      url.append(request.getServerName());
      if (port != 80 && "http".equals(scheme) //
         || port != 443 && "https".equals(scheme) //
      ) {
         url.append(':');
         url.append(request.getServerPort());
      }
      url.append(urlPath);
      return url.toString();
   }

   public static void requestBasicAuth(final HttpServletResponse response, final String realm) throws IOException {
      response.setHeader("WWW-Authenticate", "Basic realm=\"" + realm + "\"");
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
   }

   public static void setNoCachingHeaders(final HttpServletResponse response) {
      Args.notNull("response", response);

      //http://www.onjava.com/pub/a/onjava/excerpt/jebp_3/index2.html
      // Set to expire far in the past.
      response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
      // Set standard HTTP/1.1 no-cache headers.
      response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
      // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
      response.addHeader("Cache-Control", "post-check=0, pre-check=0");
      // Set standard HTTP/1.0 no-cache header.
      response.setHeader("Pragma", "no-cache");
   }

   public static void setRecommendedSecurityHeaders(final HttpServletResponse response, final boolean alwaysHTTPS) {
      Args.notNull("response", response);

      // https://www.globaldots.com/8-http-security-headers-best-practices/
      response.setHeader("X-XSS-Protection", "1");
      response.setHeader("X-Content-Type-Options", "nosniff");
      response.setHeader("X-Frame-Options", "SAMEORIGIN");
      if (alwaysHTTPS) {
         response.setHeader("Strict-Transport-Security", "max-age=63072000; includeSubDomains");
      }
      response.setHeader("Referrer-Policy", "no-referrer");
   }
}
