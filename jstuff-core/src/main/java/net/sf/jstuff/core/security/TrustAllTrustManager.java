/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TrustAllTrustManager implements X509TrustManager {

   private static final X509Certificate[] NO_CERTS = new X509Certificate[0];

   @Override
   public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
      // trust all
   }

   @Override
   public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
      // trust all
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      return NO_CERTS;
   }
}
