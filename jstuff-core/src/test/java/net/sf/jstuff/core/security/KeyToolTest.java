/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.*;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.junit.Test;

import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class KeyToolTest {

   private static final Logger LOG = Logger.create();

   @Test
   public void testCreateSelfSignedCertificate() throws GeneralSecurityException {

      LOG.info("Testing illegal subject DN...");
      try {
         KeyTool.createSelfSignedCertificate("sdfdsf", "RSA", 1024, 14);
         failBecauseExceptionWasNotThrown(GeneralSecurityException.class);
      } catch (final Exception ex) {
         System.out.println(ex);
         assertThat(ex.getClass()).isEqualTo(GeneralSecurityException.class);
      }

      LOG.info("Testing illegal key size...");
      try {
         KeyTool.createSelfSignedCertificate("CN=foo", "RSA", 20, 14);
         failBecauseExceptionWasNotThrown(GeneralSecurityException.class);
      } catch (final Exception ex) {
         assertThat(ex.getClass()).isEqualTo(GeneralSecurityException.class);
      }

      LOG.info("Testing illegal algorithm...");
      try {
         KeyTool.createSelfSignedCertificate("CN=foo", "BLABLA", 1024, 14);
         failBecauseExceptionWasNotThrown(GeneralSecurityException.class);
      } catch (final Exception ex) {
         assertThat(ex.getClass()).isEqualTo(GeneralSecurityException.class);
      }

      LOG.info("Testing illegal validity...");
      try {
         KeyTool.createSelfSignedCertificate("CN=foo", "RSA", 1024, -1);
         failBecauseExceptionWasNotThrown(GeneralSecurityException.class);
      } catch (final Exception ex) {
         assertThat(ex.getClass()).isEqualTo(GeneralSecurityException.class);
      }

      final Tuple2<X509Certificate, PrivateKey> result = KeyTool.createSelfSignedCertificate("CN=foo", "RSA", 1024, 14);

      assertThat(result.get1().getSubjectX500Principal().getName()).isEqualTo("CN=foo");
      assertThat(result.get1().getType()).isEqualTo("X.509");
      result.get1().checkValidity();

      assertThat(result.get1().getPublicKey().getAlgorithm()).isEqualTo("RSA");
      assertThat(result.get1().getPublicKey().getFormat()).isEqualTo("X.509");

      assertThat(result.get2().getAlgorithm()).isEqualTo("RSA");
      assertThat(result.get2().getFormat()).isEqualTo("PKCS#8");

   }
}
