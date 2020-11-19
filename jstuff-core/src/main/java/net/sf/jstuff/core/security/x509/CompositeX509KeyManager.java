/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security.x509;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.X509KeyManager;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeX509KeyManager implements X509KeyManager {
   private final List<X509KeyManager> keyManagers;

   public CompositeX509KeyManager(final Collection<X509KeyManager> keyManagers) {
      Args.notNull("keyManagers", keyManagers);
      Args.noNulls("keyManagers", keyManagers);

      this.keyManagers = new ArrayList<>(keyManagers);
   }

   public CompositeX509KeyManager(final X509KeyManager... keyManagers) {
      Args.notNull("keyManagers", keyManagers);
      Args.noNulls("keyManagers", keyManagers);

      this.keyManagers = Arrays.asList(keyManagers);
   }

   @Override
   public String chooseClientAlias(final String[] keyType, final Principal[] issuers, final Socket socket) {
      for (final X509KeyManager keyManager : keyManagers) {
         final String alias = keyManager.chooseClientAlias(keyType, issuers, socket);
         if (alias != null)
            return alias;
      }
      return null;
   }

   @Override
   public String chooseServerAlias(final String keyType, final Principal[] issuers, final Socket socket) {
      for (final X509KeyManager keyManager : keyManagers) {
         final String alias = keyManager.chooseServerAlias(keyType, issuers, socket);
         if (alias != null)
            return alias;
      }
      return null;
   }

   @Override
   public X509Certificate[] getCertificateChain(final String alias) {
      for (final X509KeyManager keyManager : keyManagers) {
         final X509Certificate[] chain = keyManager.getCertificateChain(alias);
         if (chain != null && chain.length > 0)
            return chain;
      }
      return null;
   }

   @Override
   public String[] getClientAliases(final String keyType, final Principal[] issuers) {
      final List<String> result = new ArrayList<>();
      for (final X509KeyManager keyManager : keyManagers) {
         CollectionUtils.addAll(result, keyManager.getClientAliases(keyType, issuers));
      }
      if (result.isEmpty())
         return null;
      return result.toArray(new String[result.size()]);
   }

   @Override
   public PrivateKey getPrivateKey(final String alias) {
      for (final X509KeyManager keyManager : keyManagers) {
         final PrivateKey privateKey = keyManager.getPrivateKey(alias);
         if (privateKey != null)
            return privateKey;
      }
      return null;
   }

   @Override
   public String[] getServerAliases(final String keyType, final Principal[] issuers) {
      final List<String> result = new ArrayList<>();
      for (final X509KeyManager keyManager : keyManagers) {
         CollectionUtils.addAll(result, keyManager.getServerAliases(keyType, issuers));
      }
      if (result.isEmpty())
         return null;
      return result.toArray(new String[result.size()]);
   }
}
