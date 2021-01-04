/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.net.Socket;
import java.security.Principal;

import javax.net.ssl.X509KeyManager;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CertificateAliasPreselectingX509KeyManager extends DelegatingX509KeyManager {

   private final String clientCertAlias;
   private final String serverCertAlias;

   public CertificateAliasPreselectingX509KeyManager(final X509KeyManager wrapped, final String clientCertAlias, final String serverCertAlias) {
      super(wrapped);
      this.clientCertAlias = clientCertAlias;
      this.serverCertAlias = serverCertAlias;
   }

   @Override
   public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
      return clientCertAlias == null ? super.chooseClientAlias(keyType, issuers, socket) : clientCertAlias;
   }

   @Override
   public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
      return serverCertAlias == null ? super.chooseServerAlias(keyType, issuers, socket) : serverCertAlias;
   }
}
