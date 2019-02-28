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

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.security.KeyTool;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class X509UtilsTest extends TestCase {

   public void testWithSelfSignedCert() throws GeneralSecurityException {
      final Tuple2<X509Certificate, PrivateKey> selfSigned = KeyTool.createSelfSignedCertificate("cn=foo", "RSA", 512, 1);
      final X509Certificate selfSignedCert = selfSigned.get1();

      assertTrue(X509Utils.isSelfSignedCertificate(selfSignedCert));
      assertTrue(X509Utils.isValid(selfSignedCert));
      assertTrue(X509Utils.isX509Certificate(selfSignedCert));
      assertEquals("foo", X509Utils.getCN(selfSignedCert));
      assertEquals(0, X509Utils.getCRLURLs(selfSignedCert).size());
      assertEquals(null, X509Utils.getOcspResponderURL(selfSignedCert));

      final String selfSignedCertPEM = X509Utils.toPEM(selfSignedCert);
      assertTrue(selfSignedCertPEM.startsWith("-----BEGIN CERTIFICATE-----" + Strings.NEW_LINE));
      assertTrue(selfSignedCertPEM.endsWith(Strings.NEW_LINE + "-----END CERTIFICATE-----" + Strings.NEW_LINE));
      final X509Certificate selfSignedCert2 = X509Utils.getCertificateFromPEM(selfSignedCertPEM);
      assertEquals(selfSignedCert, selfSignedCert2);

      final String selfSignedPublicKeyPEM = X509Utils.toPEM(selfSignedCert.getPublicKey());
      assertTrue(selfSignedPublicKeyPEM.startsWith("-----BEGIN PUBLIC KEY-----" + Strings.NEW_LINE));
      assertTrue(selfSignedPublicKeyPEM.endsWith(Strings.NEW_LINE + "-----END PUBLIC KEY-----" + Strings.NEW_LINE));
      final PublicKey selfSignedPublicKey = X509Utils.getPublicKeyFromPEM(selfSignedPublicKeyPEM, selfSignedCert.getPublicKey().getAlgorithm());
      assertEquals(selfSignedCert.getPublicKey(), selfSignedPublicKey);

      final String selfSignedPrivateKeyPEM = X509Utils.toPEM(selfSigned.get2());
      assertTrue(selfSignedPrivateKeyPEM.startsWith("-----BEGIN PRIVATE KEY-----" + Strings.NEW_LINE));
      assertTrue(selfSignedPrivateKeyPEM.endsWith(Strings.NEW_LINE + "-----END PRIVATE KEY-----" + Strings.NEW_LINE));
      final PrivateKey selfSignedPrivateKey = X509Utils.getPrivateKeyFromPEM(selfSignedPrivateKeyPEM, selfSigned.get2().getAlgorithm());
      assertEquals(selfSigned.get2(), selfSignedPrivateKey);

      System.out.println(X509Utils.getFingerprint(selfSignedCert));
      assertEquals(40, X509Utils.getFingerprint(selfSignedCert).length());
   }

   public void testIsEqualDN() {
      assertTrue(X509Utils.isEqualDN("cn=Foo, o=BAR", "CN=foo,o=bar"));

      assertFalse(X509Utils.isEqualDN("cn=Foo, o=BAR", null));
      assertFalse(X509Utils.isEqualDN("cn=Foo, o=BAR", ""));
      assertFalse(X509Utils.isEqualDN(null, "cn=Foo, o=BAR"));
      assertFalse(X509Utils.isEqualDN("", "cn=Foo, o=BAR"));
   }
}
