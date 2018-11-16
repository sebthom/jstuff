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

import java.security.cert.X509Certificate;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TrustAllTrustManager implements javax.net.ssl.X509TrustManager {

   private static final X509Certificate[] ZERO_CERTS = new X509Certificate[0];

   public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
      // trust all
   }

   public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
      // trust all
   }

   public X509Certificate[] getAcceptedIssuers() {
      return ZERO_CERTS;
   }
}
