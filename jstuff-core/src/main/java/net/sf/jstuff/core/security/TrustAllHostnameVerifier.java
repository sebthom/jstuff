/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TrustAllHostnameVerifier implements HostnameVerifier {

   @Override
   public boolean verify(final String hostname, final SSLSession session) {
      return true;
   }

}
