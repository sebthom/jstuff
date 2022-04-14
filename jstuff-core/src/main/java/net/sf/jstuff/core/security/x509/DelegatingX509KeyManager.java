/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingX509KeyManager implements X509KeyManager {

   private final X509KeyManager wrapped;

   public DelegatingX509KeyManager(final X509KeyManager wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
      return wrapped.chooseClientAlias(keyType, issuers, socket);
   }

   @Override
   public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
      return wrapped.chooseServerAlias(keyType, issuers, socket);
   }

   @Override
   public X509Certificate[] getCertificateChain(final String alias) {
      return wrapped.getCertificateChain(alias);
   }

   @Override
   public String[] getClientAliases(final String keyType, final Principal[] issuers) {
      return wrapped.getClientAliases(keyType, issuers);
   }

   @Override
   public PrivateKey getPrivateKey(final String alias) {
      return wrapped.getPrivateKey(alias);
   }

   @Override
   public String[] getServerAliases(final String keyType, final Principal[] issuers) {
      return wrapped.getServerAliases(keyType, issuers);
   }
}
