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
package net.sf.jstuff.core.security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
      final TrustManager[] trustAllCerts = {new javax.net.ssl.X509TrustManager() {
         public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
            // trust all
         }

         public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
            // trust all
         }

         public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
         }
      }};

      // Install the all-trusting trust manager
      try {
         // create the factory where we can set some parameters for the connection
         final SSLContext sc = SSLContext.getInstance("TLSv1");
         sc.init(null, trustAllCerts, new java.security.SecureRandom());
         HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
         HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(final String hostname, final SSLSession session) {
               return true;
            }
         });
      } catch (final Exception ex) {
         throw new RuntimeException("Failed to install all-trusting trust manager", ex);
      }
   }
}
