/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.junit.Test;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.security.KeyTool;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class X509UtilsTest {

   @SuppressWarnings("null")
   @Test
   public void testWithSelfSignedCert() throws GeneralSecurityException {
      final var selfSigned = KeyTool.createSelfSignedCertificate("cn=foo", "RSA", 512, 1);
      final var selfSignedCert = selfSigned.get1();

      assertThat(X509Utils.isSelfSignedCertificate(selfSignedCert)).isTrue();
      assertThat(X509Utils.isValid(selfSignedCert)).isTrue();
      assertThat(X509Utils.isX509Certificate(selfSignedCert)).isTrue();
      assertThat(X509Utils.getCN(selfSignedCert)).isEqualTo("foo");
      assertThat(X509Utils.getCRLURLs(selfSignedCert)).isEmpty();
      assertThat(X509Utils.getOcspResponderURL(selfSignedCert)).isNull();

      final String selfSignedCertPEM = X509Utils.toPEM(selfSignedCert);
      assert selfSignedCertPEM != null;
      assertThat(selfSignedCertPEM) //
         .startsWith("-----BEGIN CERTIFICATE-----" + Strings.NEW_LINE) //
         .endsWith(Strings.NEW_LINE + "-----END CERTIFICATE-----" + Strings.NEW_LINE);
      final X509Certificate selfSignedCert2 = X509Utils.getCertificateFromPEM(selfSignedCertPEM);
      assertThat(selfSignedCert2).isEqualTo(selfSignedCert);

      final String selfSignedPublicKeyPEM = X509Utils.toPEM(selfSignedCert.getPublicKey());
      assert selfSignedPublicKeyPEM != null;
      assertThat(selfSignedPublicKeyPEM) //
         .startsWith("-----BEGIN PUBLIC KEY-----" + Strings.NEW_LINE) //
         .endsWith(Strings.NEW_LINE + "-----END PUBLIC KEY-----" + Strings.NEW_LINE);
      final PublicKey selfSignedPublicKey = X509Utils.getPublicKeyFromPEM(selfSignedPublicKeyPEM, selfSignedCert.getPublicKey()
         .getAlgorithm());
      assertThat(selfSignedPublicKey).isEqualTo(selfSignedCert.getPublicKey());

      final String selfSignedPrivateKeyPEM = X509Utils.toPEM(selfSigned.get2());
      assert selfSignedPrivateKeyPEM != null;
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
