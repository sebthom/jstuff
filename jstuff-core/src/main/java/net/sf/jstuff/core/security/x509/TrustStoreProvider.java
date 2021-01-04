/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.net.ssl.TrustManager;

import net.sf.jstuff.core.event.EventListenable;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface TrustStoreProvider extends EventListenable<TrustStoreProvider.Event> {

   interface CertificateExpiredEvent extends Event {
      X509Certificate getExpired();
   }

   interface CertificatesAddedEvent extends Event {
      Collection<X509Certificate> getAdded();
   }

   interface CertificatesRemovedEvent extends Event {
      Collection<X509Certificate> getRemoved();
   }

   interface Event {
      EventType getEventType();
   }

   enum EventType {
      CERTIFICATE_EXPIRED,
      CERTIFICATES_ADDED,
      CERTIFICATES_REMOVED;
   }

   static TrustStoreProvider toImmutable(final TrustStoreProvider trustStoreProvider) {
      Args.notNull("trustStoreProvider", trustStoreProvider);
      return new TrustStoreProvider() {
         @Override
         public TrustManager[] getTrustManagers() {
            return trustStoreProvider.getTrustManagers();
         }

         @Override
         public KeyStore getTrustStore() {
            return trustStoreProvider.getTrustStore();
         }

         @Override
         public boolean subscribe(final EventListener<Event> listener) {
            return trustStoreProvider.subscribe(listener);
         }

         @Override
         public boolean unsubscribe(final EventListener<Event> listener) {
            return trustStoreProvider.unsubscribe(listener);
         }
      };
   }

   /**
    * @return a copy of the internal trust managers array or an empty array
    */
   TrustManager[] getTrustManagers();

   /**
    * @return never null
    */
   KeyStore getTrustStore();
}
