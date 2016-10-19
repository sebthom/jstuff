/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.security.x509;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import net.sf.jstuff.core.io.FileUtils;
import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class X509Utils {

    public static final class PublicKeyEntry {
        public final PublicKey publicKey;
        public final String subjectDN;
        public final String issuerDN;

        public PublicKeyEntry(final String subjectDN, final String issuerDN, final PublicKey publicKey) {
            this.subjectDN = subjectDN;
            this.issuerDN = issuerDN;
            this.publicKey = publicKey;
        }
    }

    private static final Base64 BASE64 = new Base64();
    private static final CertificateFactory CERTIFICATE_FACTORY;
    private static final Pattern CERTIFICATE_PATTERN = Pattern.compile("BEGIN CERTIFICATE-+\r?\n?(.*[^-])\r?\n?-+END CERTIFICATE", Pattern.DOTALL);
    private static final Pattern PUBLIC_KEY_PATTERN = Pattern.compile("BEGIN PUBLIC KEY-+\r?\n?(.*[^-])\r?\n?-+END PUBLIC KEY", Pattern.DOTALL);

    static {
        try {
            CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509");
        } catch (final CertificateException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Constructs a X509Certificate instance from a PEM encoded certificate
     */
    public static X509Certificate getCertificateFromPEM(final File pemFile) throws GeneralSecurityException, IOException {
        return getCertificateFromPEM(FileUtils.readFileToString(pemFile));
    }

    /**
     * Constructs a X509Certificate instance from a PEM encoded certificate
     */
    public static X509Certificate getCertificateFromPEM(final InputStream pemCertificate) throws GeneralSecurityException, IOException {
        try {
            return getCertificateFromPEM(IOUtils.toString(pemCertificate));
        } finally {
            IOUtils.closeQuietly(pemCertificate);
        }
    }

    /**
     * Constructs a X509Certificate instance from a PEM encoded certificate
     */
    public static X509Certificate getCertificateFromPEM(final String pemCertificate) throws GeneralSecurityException {
        final Matcher m = CERTIFICATE_PATTERN.matcher(pemCertificate);
        final byte[] certBytes;
        if (m.find()) {
            certBytes = pemCertificate.getBytes();
        } else {
            certBytes = ("-----BEGIN CERTIFICATE-----\n" + pemCertificate + "\n-----END CERTIFICATE-----").getBytes();
        }

        final X509Certificate cert = (X509Certificate) CERTIFICATE_FACTORY.generateCertificate(new ByteArrayInputStream(certBytes));
        return cert;
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static PublicKey getPublicKeyFromPEM(final File pemFile, final String algorithm) throws GeneralSecurityException, IOException {
        return getPublicKeyFromPEM(FileUtils.readFileToString(pemFile), algorithm);
    }

    /**
     * Constructs a public key instance from PEM encoded public key, NOT from a PEM encoded certificate
     */
    public static PublicKey getPublicKeyFromPEM(final InputStream pemContent, final String algorithm) throws GeneralSecurityException, IOException {
        try {
            return getPublicKeyFromPEM(IOUtils.toString(pemContent), algorithm);
        } finally {
            IOUtils.closeQuietly(pemContent);
        }
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static PublicKey getPublicKeyFromPEM(final String pemContent, final String algorithm) throws GeneralSecurityException {
        final Matcher keyMatcher = PUBLIC_KEY_PATTERN.matcher(pemContent);
        final byte[] publicKey;
        if (keyMatcher.find()) {
            publicKey = BASE64.decode(keyMatcher.group(1).getBytes());
        } else {
            publicKey = BASE64.decode(pemContent.getBytes());
        }
        return toPublicKey(publicKey, algorithm);
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static RSAPublicKey getRSAPublicKeyFromPEM(final InputStream pemContent) throws GeneralSecurityException, IOException {
        return (RSAPublicKey) getPublicKeyFromPEM(pemContent, "RSA");
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static RSAPublicKey getRSAPublicKeyFromPEM(final String pemContent) throws GeneralSecurityException {
        return (RSAPublicKey) getPublicKeyFromPEM(pemContent, "RSA");
    }

    private static PublicKey toPublicKey(final byte[] publicKey, final String algorithm) throws GeneralSecurityException {
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        final KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }

    public static boolean isSelfSignedCertificate(final X509Certificate cert) {
        try {
            final PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (final GeneralSecurityException ex) {
            return false;
        }
    }
}
