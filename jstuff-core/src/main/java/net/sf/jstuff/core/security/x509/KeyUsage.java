/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * See https://tools.ietf.org/html/rfc5280#section-4.2.1.3
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum KeyUsage {
   DIGITAL_SIGNATURE(0, "digitalSignature"),
   NON_REPUDIATION(1, "nonRepudiation"),
   KEY_ENCIPHERMENT(2, "keyEncipherment"),
   DATA_ENCIPHERMENT(3, "dataEncipherment"),
   KEY_AGREEMENT(4, "keyAgreement"),
   KEY_CERT_SIGN(5, "keyCertSign"),
   CRL_SIGN(6, "cRLSign"),
   ENCIPHER_ONLY(7, "encipherOnly"),
   DECIPHER_ONLY(8, "decipherOnly");

   public static Set<KeyUsage> getAllowedBy(final X509Certificate cert) {
      final boolean[] keyUsage = cert.getKeyUsage();
      if (keyUsage == null || keyUsage.length == 0)
         return Collections.emptySet();
      final var usages = new HashSet<KeyUsage>();
      for (int i = 0; i < keyUsage.length; i++) {
         if (keyUsage[i]) {
            final var ku = getByRFCBit(i);
            if (ku != null) {
               usages.add(ku);
            }
         }
      }
      return usages.isEmpty() ? Collections.emptySet() : usages;
   }

   @Nullable
   private static KeyUsage getByRFCBit(final int bit) {
      return switch (bit) {
         case 0 -> DIGITAL_SIGNATURE;
         case 1 -> NON_REPUDIATION;
         case 2 -> KEY_ENCIPHERMENT;
         case 3 -> DATA_ENCIPHERMENT;
         case 4 -> KEY_AGREEMENT;
         case 5 -> KEY_CERT_SIGN;
         case 6 -> CRL_SIGN;
         case 7 -> ENCIPHER_ONLY;
         case 8 -> DECIPHER_ONLY;
         default -> null;
      };
   }

   public final int rfcBit;
   public final String rfcName;

   KeyUsage(final int rfcBit, final String rfcName) {
      this.rfcBit = rfcBit;
      this.rfcName = rfcName;
   }

   public boolean isAllowedBy(final X509Certificate cert) {
      Args.notNull("cert", cert);
      final boolean[] keyUsage = cert.getKeyUsage();
      if (keyUsage == null)
         return false;
      return keyUsage[rfcBit];
   }
}
