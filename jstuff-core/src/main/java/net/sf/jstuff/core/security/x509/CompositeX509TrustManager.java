/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeX509TrustManager implements X509TrustManager {
   private static final Logger LOG = Logger.create();

   private final List<X509TrustManager> trustManagers;

   public CompositeX509TrustManager(final List<X509TrustManager> trustManagers) {
      Args.notNull("keyManagers", trustManagers);
      Args.noNulls("trustManagers", trustManagers);

      this.trustManagers = new ArrayList<>(trustManagers);
   }

   public CompositeX509TrustManager(final X509TrustManager... trustManagers) {
      Args.notNull("keyManagers", trustManagers);
      Args.noNulls("trustManagers", trustManagers);

      this.trustManagers = Arrays.asList(trustManagers);
   }

   @Override
   public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
      for (final X509TrustManager trustManager : trustManagers) {
         try {
            trustManager.checkClientTrusted(chain, authType);
            return;
         } catch (final CertificateException ex) {
            LOG.debug(ex);
         }
      }
      throw new CertificateException("Client certificate chain not trusted by any registered trust manager");
   }

   @Override
   public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
      for (final X509TrustManager trustManager : trustManagers) {
         try {
            trustManager.checkServerTrusted(chain, authType);
            return;
         } catch (final CertificateException ex) {
            LOG.debug(ex);
         }
      }
      throw new CertificateException("Server certificate chain not trusted by any registered trust manager");
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      final List<X509Certificate> result = new ArrayList<>();
      for (final X509TrustManager trustManager : trustManagers) {
         CollectionUtils.addAll(result, trustManager.getAcceptedIssuers());
      }
      return result.toArray(new X509Certificate[result.size()]);
   }
}
