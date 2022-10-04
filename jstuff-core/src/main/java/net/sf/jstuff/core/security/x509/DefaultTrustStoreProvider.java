/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.builder.Builder;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.event.EventDispatcher;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.event.SyncEventDispatcher;
import net.sf.jstuff.core.fluent.Fluent;
import net.sf.jstuff.core.io.RuntimeIOException;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.security.RuntimeSecurityException;
import net.sf.jstuff.core.types.Modifiable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultTrustStoreProvider extends Modifiable.Default implements TrustStoreProvider {

   public interface DefaultTrustStoreProviderBuilder extends Builder<DefaultTrustStoreProvider> {
      @Fluent
      DefaultTrustStoreProviderBuilder eventDispatcher(EventDispatcher<Event> value);

      @Fluent
      DefaultTrustStoreProviderBuilder isModifiable(boolean value);

      @Fluent
      DefaultTrustStoreProviderBuilder trustCerts(Collection<X509Certificate> certs);

      @Fluent
      DefaultTrustStoreProviderBuilder trustCerts(X509Certificate... certs);
   }

   private static final Logger LOG = Logger.create();

   public static DefaultTrustStoreProviderBuilder builder() {
      return BuilderFactory.of(DefaultTrustStoreProviderBuilder.class).create();
   }

   protected EventDispatcher<Event> eventDispatcher = new SyncEventDispatcher<>();

   protected ConcurrentMap<String, X509Certificate> trustCertsByAlias = new ConcurrentHashMap<>();
   protected AtomicInteger trustCertsAliasIndex = new AtomicInteger();

   protected TrustManager[] trustManagers;
   protected KeyStore trustStore;

   protected DefaultTrustStoreProvider() {
      LOG.infoNew(this);

      try {
         trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
         trustStore.load(null, null);
         final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         tmf.init(trustStore);
         trustManagers = tmf.getTrustManagers();
      } catch (final GeneralSecurityException ex) {
         throw new RuntimeSecurityException(ex);
      } catch (final IOException ex) {
         throw new RuntimeIOException(ex);
      }
   }

   public synchronized void addTrustCerts(final @Nullable Collection<X509Certificate> certs) {
      if (certs == null || certs.isEmpty())
         return;

      assertIsModifiable();

      addTrustCerts(certs.toArray(new X509Certificate[certs.size()]));
   }

   /**
    * Adds all X509 certificates from the given truststore to this instance.
    */
   public synchronized void addTrustCerts(final @Nullable KeyStore trustStore) {
      if (trustStore == null)
         return;

      assertIsModifiable();

      try {
         final var certs = new ArrayList<X509Certificate>();
         for (final String certAlias : Enumerations.toIterable(trustStore.aliases())) {
            final Certificate cert = trustStore.getCertificate(certAlias);
            if (cert == null) {
               continue;
            }
            if (cert instanceof X509Certificate) {
               certs.add((X509Certificate) cert);
            } else {
               LOG.warn("Ignoring non-X509Certificate [%s] with alias [%s].", cert, certAlias);
            }
         }
         if (!certs.isEmpty()) {
            addTrustCerts(certs.toArray(new X509Certificate[certs.size()]));
         }
      } catch (final GeneralSecurityException ex) {
         throw new RuntimeSecurityException(ex);
      }
   }

   public synchronized void addTrustCerts(final X509Certificate @Nullable... certs) {
      if (certs == null || certs.length == 0)
         return;

      assertIsModifiable();

      /*
       * when new certificates are added we swap out the current truststore with a new copy holding the previous plus the newly added certificates
       */
      LOG.info("Adding trusted certificates...");
      final List<X509Certificate> addedCerts = new ArrayList<>();
      for (final X509Certificate cert : certs) {
         if (cert == null || trustCertsByAlias.containsValue(cert)) {
            continue;
         }

         final Date now = new Date();
         if (now.after(cert.getNotAfter())) {
            LOG.warn("  -> Certificate [%s] EXPIRED [%s]!", cert.getSubjectX500Principal().getName(), cert.getNotAfter());
            eventDispatcher.fire(new CertificateExpiredEvent() {
               @Override
               public EventType getEventType() {
                  return EventType.CERTIFICATE_EXPIRED;
               }

               @Override
               public X509Certificate getExpired() {
                  return cert;
               }
            });
         } else {
            LOG.info("  -> Certificate [%s] expires [%s].", cert.getSubjectX500Principal().getName(), cert.getNotAfter());
         }

         final String alias = Integer.toString(trustCertsAliasIndex.incrementAndGet());
         trustCertsByAlias.put(alias, cert);
         addedCerts.add(cert);
      }

      rebuildTrustStore();

      eventDispatcher.fire(new CertificatesAddedEvent() {
         @Override
         public Collection<X509Certificate> getAdded() {
            return addedCerts;
         }

         @Override
         public EventType getEventType() {
            return EventType.CERTIFICATES_ADDED;
         }
      });
   }

   @Override
   public TrustManager[] getTrustManagers() {
      return trustManagers.clone();
   }

   @Override
   public KeyStore getTrustStore() {
      return trustStore;
   }

   private synchronized void rebuildTrustStore() {
      try {
         final KeyStore newTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
         newTrustStore.load(null, null);
         for (final Entry<String, X509Certificate> e : trustCertsByAlias.entrySet()) {
            newTrustStore.setCertificateEntry(e.getKey(), e.getValue());
         }

         final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         tmf.init(newTrustStore);

         trustStore = newTrustStore;
         trustManagers = tmf.getTrustManagers();
      } catch (final GeneralSecurityException ex) {
         throw new RuntimeSecurityException(ex);
      } catch (final IOException ex) {
         throw new RuntimeIOException(ex);
      }
   }

   public synchronized void removeTrustCerts(final X509Certificate @Nullable... certs) {
      if (certs == null || certs.length == 0)
         return;

      assertIsModifiable();

      /*
       * when new certificates are added we swap out the current truststore with a new copy holding the previous plus the newly added certificates
       */
      LOG.info("Removing trusted certificates...");
      final List<X509Certificate> removedCerts = new ArrayList<>();
      final Iterator<Map.Entry<String, X509Certificate>> it = trustCertsByAlias.entrySet().iterator();
      while (it.hasNext()) {
         final Map.Entry<String, X509Certificate> entry = it.next();
         final X509Certificate cert = entry.getValue();
         if (ArrayUtils.containsIdentical(certs, cert)) {
            it.remove();
            removedCerts.add(cert);
            LOG.info("  -> Certificate [%s] expires [%s].", cert.getSubjectX500Principal().getName(), cert.getNotAfter());
         }
      }

      rebuildTrustStore();

      eventDispatcher.fire(new CertificatesRemovedEvent() {
         @Override
         public EventType getEventType() {
            return EventType.CERTIFICATES_REMOVED;
         }

         @Override
         public Collection<X509Certificate> getRemoved() {
            return removedCerts;
         }
      });
   }

   /**
    * Invoked by builder.
    */
   protected void setTrustCerts(final Collection<X509Certificate> certs) {
      addTrustCerts(certs);
   }

   /**
    * Invoked by builder.
    */
   protected void setTrustCerts(final X509Certificate... certs) {
      addTrustCerts(certs);
   }

   @Override
   public boolean subscribe(final EventListener<Event> listener) {
      return eventDispatcher.subscribe(listener);
   }

   @Override
   public boolean unsubscribe(final EventListener<Event> listener) {
      return eventDispatcher.unsubscribe(listener);
   }
}
