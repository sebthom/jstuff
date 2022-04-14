/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import static org.assertj.core.api.Assertions.*;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.junit.Test;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.security.KeyTool;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class X509UtilsTest {

   @Test
   public void testWithSelfSignedCert() throws GeneralSecurityException {
      final Tuple2<X509Certificate, PrivateKey> selfSigned = KeyTool.createSelfSignedCertificate("cn=foo", "RSA", 512, 1);
      final X509Certificate selfSignedCert = selfSigned.get1();

      assertThat(X509Utils.isSelfSignedCertificate(selfSignedCert)).isTrue();
      assertThat(X509Utils.isValid(selfSignedCert)).isTrue();
      assertThat(X509Utils.isX509Certificate(selfSignedCert)).isTrue();
      assertThat(X509Utils.getCN(selfSignedCert)).isEqualTo("foo");
      assertThat(X509Utils.getCRLURLs(selfSignedCert)).isEmpty();
      assertThat(X509Utils.getOcspResponderURL(selfSignedCert)).isNull();

      final String selfSignedCertPEM = X509Utils.toPEM(selfSignedCert);
      assertThat(selfSignedCertPEM) //
         .startsWith("-----BEGIN CERTIFICATE-----" + Strings.NEW_LINE) //
         .endsWith(Strings.NEW_LINE + "-----END CERTIFICATE-----" + Strings.NEW_LINE);
      final X509Certificate selfSignedCert2 = X509Utils.getCertificateFromPEM(selfSignedCertPEM);
      assertThat(selfSignedCert2).isEqualTo(selfSignedCert);

      final String selfSignedPublicKeyPEM = X509Utils.toPEM(selfSignedCert.getPublicKey());
      assertThat(selfSignedPublicKeyPEM) //
         .startsWith("-----BEGIN PUBLIC KEY-----" + Strings.NEW_LINE) //
         .endsWith(Strings.NEW_LINE + "-----END PUBLIC KEY-----" + Strings.NEW_LINE);
      final PublicKey selfSignedPublicKey = X509Utils.getPublicKeyFromPEM(selfSignedPublicKeyPEM, selfSignedCert.getPublicKey()
         .getAlgorithm());
      assertThat(selfSignedPublicKey).isEqualTo(selfSignedCert.getPublicKey());

      final String selfSignedPrivateKeyPEM = X509Utils.toPEM(selfSigned.get2());
      assertThat(selfSignedPrivateKeyPEM) //
         .startsWith("-----BEGIN PRIVATE KEY-----" + Strings.NEW_LINE) //
         .endsWith(Strings.NEW_LINE + "-----END PRIVATE KEY-----" + Strings.NEW_LINE);
      final PrivateKey selfSignedPrivateKey = X509Utils.getPrivateKeyFromPEM(selfSignedPrivateKeyPEM, selfSigned.get2().getAlgorithm());
      assertThat(selfSignedPrivateKey).isEqualTo(selfSigned.get2());

      System.out.println(X509Utils.getFingerprint(selfSignedCert));
      assertThat(X509Utils.getFingerprint(selfSignedCert)).hasSize(40);
   }

   @Test
   public void testIsEqualDN() {
      assertThat(X509Utils.isEqualDN("cn=Foo, o=BAR", "CN=foo,o=bar")).isTrue();
      assertThat(X509Utils.isEqualDN("cn=Foo, o=BAR", null)).isFalse();
      assertThat(X509Utils.isEqualDN("cn=Foo, o=BAR", "")).isFalse();
      assertThat(X509Utils.isEqualDN(null, "cn=Foo, o=BAR")).isFalse();
      assertThat(X509Utils.isEqualDN("", "cn=Foo, o=BAR")).isFalse();
   }
}
