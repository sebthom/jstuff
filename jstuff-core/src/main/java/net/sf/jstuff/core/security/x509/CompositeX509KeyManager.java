/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.X509KeyManager;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeX509KeyManager implements X509KeyManager {
   private final List<X509KeyManager> keyManagers;

   public CompositeX509KeyManager(final Collection<X509KeyManager> keyManagers) {
      Args.notNull("keyManagers", keyManagers);
      Args.noNulls("keyManagers", keyManagers);

      this.keyManagers = new ArrayList<>(keyManagers);
   }

   public CompositeX509KeyManager(final @NonNull X509KeyManager... keyManagers) {
      Args.notNull("keyManagers", keyManagers);
      Args.noNulls("keyManagers", keyManagers);

      this.keyManagers = Arrays.asList(keyManagers);
   }

   @Override
   public @Nullable String chooseClientAlias(final @NonNull String[] keyType, final @NonNull Principal @Nullable [] issuers,
         final @Nullable Socket socket) {
      for (final X509KeyManager keyManager : keyManagers) {
         final String alias = keyManager.chooseClientAlias(keyType, issuers, socket);
         if (alias != null)
            return alias;
      }
      return null;
   }

   @Override
   public @Nullable String chooseServerAlias(final String keyType, final @NonNull Principal @Nullable [] issuers,
         final @Nullable Socket socket) {
      for (final X509KeyManager keyManager : keyManagers) {
         final String alias = keyManager.chooseServerAlias(keyType, issuers, socket);
         if (alias != null)
            return alias;
      }
      return null;
   }

   @Override
   public @NonNull X509Certificate @Nullable [] getCertificateChain(final String alias) {
      for (final X509KeyManager keyManager : keyManagers) {
         final X509Certificate[] chain = keyManager.getCertificateChain(alias);
         if (chain != null && chain.length > 0)
            return chain;
      }
      return null;
   }

   @Override
   public @NonNull String @Nullable [] getClientAliases(final String keyType, final @NonNull Principal @Nullable [] issuers) {
      final var result = new ArrayList<String>();
      for (final X509KeyManager keyManager : keyManagers) {
         CollectionUtils.addAll(result, keyManager.getClientAliases(keyType, issuers));
      }
      if (result.isEmpty())
         return null;
      final var arr = result.toArray(String[]::new);
      return asNonNullUnsafe(arr);
   }

   @Override
   public @Nullable PrivateKey getPrivateKey(final String alias) {
      for (final X509KeyManager keyManager : keyManagers) {
         final PrivateKey privateKey = keyManager.getPrivateKey(alias);
         if (privateKey != null)
            return privateKey;
      }
      return null;
   }

   @Override
   public @NonNull String @Nullable [] getServerAliases(final String keyType, final @NonNull Principal @Nullable [] issuers) {
      final var result = new ArrayList<String>();
      for (final X509KeyManager keyManager : keyManagers) {
         CollectionUtils.addAll(result, keyManager.getServerAliases(keyType, issuers));
      }
      if (result.isEmpty())
         return null;
      final var arr = result.toArray(String[]::new);
      return asNonNullUnsafe(arr);
   }
}
