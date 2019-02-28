/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security.x509;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.net.ssl.TrustManager;

import net.sf.jstuff.core.event.EventListenable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface TrustStoreProvider extends EventListenable<TrustStoreProvider.Event> {

   interface Event {
      EventType getEventType();
   }

   interface CertificateExpiredEvent extends Event {
      X509Certificate getExpired();
   }

   interface CertificatesAddedEvent extends Event {
      Collection<X509Certificate> getAdded();
   }

   interface CertificatesRemovedEvent extends Event {
      Collection<X509Certificate> getRemoved();
   }

   enum EventType {
      CERTIFICATE_EXPIRED,
      CERTIFICATES_ADDED,
      CERTIFICATES_REMOVED;
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
