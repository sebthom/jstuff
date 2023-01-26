/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509KeyManager;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingX509KeyManager implements X509KeyManager {

   private final X509KeyManager wrapped;

   public DelegatingX509KeyManager(final X509KeyManager wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public @Nullable String chooseClientAlias(final @NonNull String[] keyType, final @NonNull Principal @Nullable [] issuers,
      final @Nullable Socket socket) {
      return wrapped.chooseClientAlias(keyType, issuers, socket);
   }

   @Override
   public @Nullable String chooseServerAlias(final String keyType, final @NonNull Principal @Nullable [] issuers,
      final @Nullable Socket socket) {
      return wrapped.chooseServerAlias(keyType, issuers, socket);
   }

   @Override
   public @NonNull X509Certificate @Nullable [] getCertificateChain(final String alias) {
      return wrapped.getCertificateChain(alias);
   }

   @Override
   public @NonNull String @Nullable [] getClientAliases(final String keyType, final @NonNull Principal @Nullable [] issuers) {
      return wrapped.getClientAliases(keyType, issuers);
   }

   @Override
   public @Nullable PrivateKey getPrivateKey(final String alias) {
      return wrapped.getPrivateKey(alias);
   }

   @Override
   public @NonNull String @Nullable [] getServerAliases(final String keyType, final @NonNull Principal @Nullable [] issuers) {
      return wrapped.getServerAliases(keyType, issuers);
   }
}
