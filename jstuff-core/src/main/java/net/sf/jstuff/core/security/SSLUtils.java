/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SSLUtils {
   /**
    * http://www.oracle.com/technetwork/java/javase/documentation/cve-2014-3566-2342133.html
    */
   public static void disableSSLv3() {
      if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
         java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
         java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");
      } else {
         java.lang.System.setProperty("https.protocols", "TLSv1");
      }
   }

   public static void installAllTrustManager() {
      final TrustManager[] trustAllCerts = {new TrustAllTrustManager()};

      // Install the all-trusting trust manager
      try {
         // create the factory where we can set some parameters for the connection
         final SSLContext sc = SSLContext.getInstance("TLSv1");
         sc.init(null, trustAllCerts, new java.security.SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
         HttpsURLConnection.setDefaultHostnameVerifier(new TrustAllHostnameVerifier());
      } catch (final Exception ex) {
         throw new SecurityException("Failed to install all-trusting trust manager", ex);
      }
   }
}
