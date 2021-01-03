/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security.x509;

import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jstuff.core.validation.Args;

/**
 * See https://tools.ietf.org/html/rfc5280#section-4.2.1.12
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum ExtendedKeyUsage {

   SERVER_AUTH("1.3.6.1.5.5.7.3.1", "id-kp-serverAuth"),
   CLIENT_AUTH("1.3.6.1.5.5.7.3.2", "id-kp-clientAuth"),
   CODE_SIGNING("1.3.6.1.5.5.7.3.3", "id-kp-codeSigning"),
   EMAIL_PROTECTION("1.3.6.1.5.5.7.3.4", "id-kp-emailProtection"),
   TIME_STAMPING("1.3.6.1.5.5.7.3.8", "id-kp-timeStamping"),
   OCSP_SIGNING("1.3.6.1.5.5.7.3.9", "id-kp-OCSPSigning");

   public static Set<ExtendedKeyUsage> getAllowedBy(final X509Certificate cert) throws CertificateParsingException {
      final List<String> oids = cert.getExtendedKeyUsage();
      if (oids == null || oids.isEmpty())
         return Collections.emptySet();
      final Set<ExtendedKeyUsage> usages = new HashSet<>();
      for (final ExtendedKeyUsage keyUsage : ExtendedKeyUsage.values()) {
         if (oids.contains(keyUsage.rfcOID)) {
            usages.add(keyUsage);
         }
      }
      return usages;
   }

   public final String rfcOID;
   public final String rfcName;

   ExtendedKeyUsage(final String rfcOID, final String rfcName) {
      this.rfcOID = rfcOID;
      this.rfcName = rfcName;
   }

   public boolean isAllowedBy(final X509Certificate cert) throws CertificateParsingException {
      Args.notNull("cert", cert);

      final List<String> oids = cert.getExtendedKeyUsage();
      if (oids == null || oids.isEmpty())
         return false;
      return oids.contains(rfcOID);
   }
}
