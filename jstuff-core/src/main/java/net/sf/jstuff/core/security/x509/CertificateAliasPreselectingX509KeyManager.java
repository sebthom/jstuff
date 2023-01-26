/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.net.Socket;
import java.security.Principal;

import javax.net.ssl.X509KeyManager;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CertificateAliasPreselectingX509KeyManager extends DelegatingX509KeyManager {

   @Nullable
   private final String clientCertAlias;
   @Nullable
   private final String serverCertAlias;

   public CertificateAliasPreselectingX509KeyManager(final X509KeyManager wrapped, final @Nullable String clientCertAlias,
      final @Nullable String serverCertAlias) {
      super(wrapped);
      this.clientCertAlias = clientCertAlias;
      this.serverCertAlias = serverCertAlias;
   }

   @Override
   public @Nullable String chooseClientAlias(final @NonNull String[] keyType, final @NonNull Principal @Nullable [] issuers,
      final @Nullable Socket socket) {
      return clientCertAlias == null ? super.chooseClientAlias(keyType, issuers, socket) : clientCertAlias;
   }

   @Override
   public @Nullable String chooseServerAlias(final String keyType, final @NonNull Principal @Nullable [] issuers,
      final @Nullable Socket socket) {
      return serverCertAlias == null ? super.chooseServerAlias(keyType, issuers, socket) : serverCertAlias;
   }
}
