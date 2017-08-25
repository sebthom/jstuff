/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attribute;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.FileUtils;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class X509Utils {

    private static final Logger LOG = Logger.create();

    private static final Base64 BASE64 = new Base64();
    public static final CertificateFactory CERTIFICATE_FACTORY;
    private static final Pattern CRL_PATTERN = Pattern.compile("BEGIN X509 CRL-+\r?\n?(.*[^-])\r?\n?-+END X509 CRL", Pattern.DOTALL);
    private static final Pattern CERTIFICATE_PATTERN = Pattern.compile("BEGIN CERTIFICATE-+\r?\n?(.*[^-])\r?\n?-+END CERTIFICATE", Pattern.DOTALL);
    private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile("BEGIN PRIVATE KEY-+\r?\n?(.*[^-])\r?\n?-+END PRIVATE KEY", Pattern.DOTALL);
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
        Args.notNull("pemFile", pemFile);
        return getCertificateFromPEM(FileUtils.readFileToString(pemFile));
    }

    /**
     * Constructs a X509Certificate instance from a PEM encoded certificate
     */
    public static X509Certificate getCertificateFromPEM(final InputStream pemStream) throws GeneralSecurityException, IOException {
        Args.notNull("pemStream", pemStream);
        try {
            return getCertificateFromPEM(IOUtils.toString(pemStream));
        } finally {
            IOUtils.closeQuietly(pemStream);
        }
    }

    /**
     * Constructs a X509Certificate instance from a PEM encoded certificate
     */
    public static X509Certificate getCertificateFromPEM(final String pemContent) throws GeneralSecurityException {
        Args.notNull("pemContent", pemContent);
        final Matcher m = CERTIFICATE_PATTERN.matcher(pemContent);
        final byte[] certBytes;
        if (m.find()) {
            certBytes = pemContent.getBytes();
        } else {
            certBytes = ("-----BEGIN CERTIFICATE-----\n" + pemContent + "\n-----END CERTIFICATE-----").getBytes();
        }

        final Certificate cert = CERTIFICATE_FACTORY.generateCertificate(new ByteArrayInputStream(certBytes));

        if ("X.509".equals(cert.getType()))
            return (X509Certificate) cert;

        throw new GeneralSecurityException("PEM-encoded certificate [" + pemContent + "] is not X.509 but [" + cert.getType() + "]");
    }

    public static List<X509Certificate> getCertificates(final KeyStore ks) {
        Args.notNull("ks", ks);
        final List<X509Certificate> certs = new ArrayList<X509Certificate>();
        try {
            for (final Enumeration<String> en = ks.aliases(); en.hasMoreElements();) {
                final Certificate cert = ks.getCertificate(en.nextElement());
                if (cert instanceof X509Certificate) {
                    certs.add((X509Certificate) cert);
                }
            }
        } catch (final KeyStoreException ex) {
            LOG.error(ex);
        }
        return certs;
    }

    /**
     * @return the first CN found
     */
    public static String getCN(final X509Certificate cert) {
        Args.notNull("cert", cert);
        try {
            final String subjectPrincipal = cert.getSubjectX500Principal().getName(X500Principal.RFC2253);
            for (final Rdn rdn : new LdapName(subjectPrincipal).getRdns()) {
                final Attribute cnAttr = rdn.toAttributes().get("cn");
                if (cnAttr != null) {
                    try {
                        final Object cnValue = cnAttr.get();
                        if (cnValue != null)
                            return cnValue.toString();
                    } catch (final Exception ex) {}
                }
            }
        } catch (final InvalidNameException ex) {}
        return null;
    }

    public static List<String> getCNs(final X509Certificate cert) {
        Args.notNull("cert", cert);
        final List<String> cns = new ArrayList<String>();
        try {
            final String subjectPrincipal = cert.getSubjectX500Principal().getName(X500Principal.RFC2253);
            for (final Rdn rdn : new LdapName(subjectPrincipal).getRdns()) {
                final Attribute cnAttr = rdn.toAttributes().get("cn");
                if (cnAttr != null) {
                    try {
                        final Object cnValue = cnAttr.get();
                        if (cnValue != null) {
                            cns.add(cnValue.toString());
                        }
                    } catch (final Exception ex) {}
                }
            }
        } catch (final InvalidNameException ex) {}
        return cns;
    }

    /**
     * Constructs a X509CRL instance from a PEM encoded CRL
     */
    public static X509CRL getCRLFromPEM(final File pemFile) throws GeneralSecurityException, IOException {
        Args.notNull("pemFile", pemFile);
        return getCRLFromPEM(FileUtils.readFileToString(pemFile));
    }

    /**
     * Constructs a X509CRL instance from a PEM encoded CRL
     */
    public static X509CRL getCRLFromPEM(final InputStream pemStream) throws GeneralSecurityException, IOException {
        Args.notNull("pemStream", pemStream);
        try {
            return getCRLFromPEM(IOUtils.toString(pemStream));
        } finally {
            IOUtils.closeQuietly(pemStream);
        }
    }

    /**
     * Constructs a X509CRL instance from a PEM encoded CRL
     */
    public static X509CRL getCRLFromPEM(final String pemContent) throws GeneralSecurityException {
        Args.notNull("pemContent", pemContent);
        final Matcher m = CRL_PATTERN.matcher(pemContent);
        final byte[] certBytes;
        if (m.find()) {
            certBytes = pemContent.getBytes();
        } else {
            certBytes = ("-----BEGIN X509 CRL-----\n" + pemContent + "\n-----END X509 CRL-----").getBytes();
        }

        final CRL cert = CERTIFICATE_FACTORY.generateCRL(new ByteArrayInputStream(certBytes));

        if ("X.509".equals(cert.getType()))
            return (X509CRL) cert;

        throw new GeneralSecurityException("PEM-encoded CRL [" + pemContent + "] is not X.509 but [" + cert.getType() + "]");
    }

    public static List<String> getCRLURLs(final X509Certificate cert) {
        Args.notNull("cert", cert);
        final byte[] crlExtValueRaw = cert.getExtensionValue("2.5.29.31");
        if (crlExtValueRaw == null)
            return Collections.emptyList();

        final List<String> crls = new ArrayList<String>();

        try {
            final String crlExtValue = new String(crlExtValueRaw, "UTF-8");
            int searchPos = 0;
            final int[] foundAt = new int[4];
            final int NOT_FOUND = -1;
            while (searchPos + 1 < crlExtValue.length()) {
                foundAt[0] = crlExtValue.indexOf("http", searchPos);
                foundAt[1] = crlExtValue.indexOf("ldap", searchPos);
                foundAt[2] = crlExtValue.indexOf("ftp", searchPos);
                foundAt[3] = crlExtValue.indexOf("file", searchPos);
                Arrays.sort(foundAt);

                int crlStartPos = NOT_FOUND;
                for (final int i : foundAt) {
                    if (i > NOT_FOUND) {
                        crlStartPos = i;
                        break;
                    }
                }
                if (crlStartPos == NOT_FOUND) {
                    break;
                }

                final int crlEndPos = crlExtValue.indexOf((char) 65533, crlStartPos);
                if (crlEndPos == NOT_FOUND) {
                    final String url = crlExtValue.substring(crlStartPos).trim();
                    if (!crls.contains(url)) {
                        crls.add(url);
                    }
                    break;
                }

                final String url = crlExtValue.substring(crlStartPos, crlEndPos - 2).trim();
                if (!crls.contains(url)) {
                    crls.add(url);
                }
                searchPos = crlEndPos + 1;
            }

            return crls;
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static String getFingerprint(final X509Certificate cert) throws GeneralSecurityException {
        Args.notNull("cert", cert);
        return DigestUtils.shaHex(cert.getEncoded());
    }

    /**
     * Extracts the OCSP Responder URL from the given certificate if specified.
     *
     * https://en.wikipedia.org/wiki/Online_Certificate_Status_Protocol
     *
     * @return null if OCSP URL is not specified or is not a HTTP or LDAP URL
     */
    public static String getOcspResponderURL(final X509Certificate cert) {
        Args.notNull("cert", cert);
        // https://tools.ietf.org/html/rfc4325#section-2
        final byte[] ocspExtValueRaw = cert.getExtensionValue("1.3.6.1.5.5.7.1.1");
        if (ocspExtValueRaw == null)
            return null;

        final String url;
        try {
            final String ocspExtValue = new String(ocspExtValueRaw, "US-ASCII");
            if (ocspExtValue.contains("http")) {
                url = "http" + Strings.substringAfter(new String(ocspExtValueRaw, "US-ASCII"), "http").trim();
            } else if (ocspExtValue.contains("ldap")) {
                url = "ldap" + Strings.substringAfter(new String(ocspExtValueRaw, "US-ASCII"), "ldap").trim();
            } else {
                url = null;
            }
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
        return url;
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static PrivateKey getPrivateKeyFromPEM(final File pemFile, final String algorithm) throws GeneralSecurityException, IOException {
        Args.notNull("pemFile", pemFile);
        Args.notNull("algorithm", algorithm);
        return getPrivateKeyFromPEM(FileUtils.readFileToString(pemFile), algorithm);
    }

    /**
     * Constructs a public key instance from PEM encoded public key, NOT from a PEM encoded certificate
     */
    public static PrivateKey getPrivateKeyFromPEM(final InputStream pemStream, final String algorithm) throws GeneralSecurityException, IOException {
        Args.notNull("pemStream", pemStream);
        Args.notNull("algorithm", algorithm);
        try {
            return getPrivateKeyFromPEM(IOUtils.toString(pemStream), algorithm);
        } finally {
            IOUtils.closeQuietly(pemStream);
        }
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static PrivateKey getPrivateKeyFromPEM(final String pemContent, final String algorithm) throws GeneralSecurityException {
        Args.notNull("pemContent", pemContent);
        Args.notNull("algorithm", algorithm);
        final Matcher keyMatcher = PRIVATE_KEY_PATTERN.matcher(pemContent);
        final byte[] privateKey;
        if (keyMatcher.find()) {
            privateKey = BASE64.decode(keyMatcher.group(1).getBytes());
        } else {
            privateKey = BASE64.decode(pemContent.getBytes());
        }
        return toPrivateKey(privateKey, algorithm);
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static PublicKey getPublicKeyFromPEM(final File pemFile, final String algorithm) throws GeneralSecurityException, IOException {
        Args.notNull("pemFile", pemFile);
        Args.notNull("algorithm", algorithm);
        return getPublicKeyFromPEM(FileUtils.readFileToString(pemFile), algorithm);
    }

    /**
     * Constructs a public key instance from PEM encoded public key, NOT from a PEM encoded certificate
     */
    public static PublicKey getPublicKeyFromPEM(final InputStream pemStream, final String algorithm) throws GeneralSecurityException, IOException {
        Args.notNull("pemStream", pemStream);
        Args.notNull("algorithm", algorithm);
        try {
            return getPublicKeyFromPEM(IOUtils.toString(pemStream), algorithm);
        } finally {
            IOUtils.closeQuietly(pemStream);
        }
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static PublicKey getPublicKeyFromPEM(final String pemContent, final String algorithm) throws GeneralSecurityException {
        Args.notNull("pemContent", pemContent);
        Args.notNull("algorithm", algorithm);
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
    public static RSAPrivateKey getRSAPrivateKeyFromPEM(final InputStream pemStream) throws GeneralSecurityException, IOException {
        Args.notNull("pemStream", pemStream);
        return (RSAPrivateKey) getPrivateKeyFromPEM(pemStream, "RSA");
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static RSAPrivateKey getRSAPrivateKeyFromPEM(final String pemContent) throws GeneralSecurityException {
        Args.notNull("pemContent", pemContent);
        return (RSAPrivateKey) getPrivateKeyFromPEM(pemContent, "RSA");
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static RSAPublicKey getRSAPublicKeyFromPEM(final InputStream pemStream) throws GeneralSecurityException, IOException {
        Args.notNull("pemStream", pemStream);
        return (RSAPublicKey) getPublicKeyFromPEM(pemStream, "RSA");
    }

    /**
     * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
     */
    public static RSAPublicKey getRSAPublicKeyFromPEM(final String pemContent) throws GeneralSecurityException {
        Args.notNull("pemContent", pemContent);
        return (RSAPublicKey) getPublicKeyFromPEM(pemContent, "RSA");
    }

    public static boolean isSelfSignedCertificate(final X509Certificate cert) {
        Args.notNull("cert", cert);
        try {
            final PublicKey key = cert.getPublicKey();
            cert.verify(key);
            return true;
        } catch (final GeneralSecurityException ex) {
            return false;
        }
    }

    /**
     * @return true if the current date/time is within the validity period given in the certificate
     */
    public static boolean isValid(final X509Certificate cert) {
        if (cert == null)
            return false;
        try {
            cert.checkValidity();
            return true;
        } catch (final CertificateExpiredException e) {
            return false;
        } catch (final CertificateNotYetValidException e) {
            return false;
        }
    }

    public static boolean isX509Certificate(final Certificate cert) {
        if (cert == null)
            return false;
        return cert instanceof X509Certificate;
    }

    private static String toPEM(final byte[] data, final String type, final int charsPerLine) {
        final char[] encoded = BASE64.encodeAsString(data).toCharArray();
        final StringBuilder sb = new StringBuilder(encoded.length + 70);
        sb.append("-----BEGIN ");
        sb.append(type);
        sb.append("-----");
        sb.append(Strings.NEW_LINE);
        for (int i = 0; i < encoded.length; i += charsPerLine) {
            if (encoded.length - i < charsPerLine) {
                sb.append(encoded, i, encoded.length - i);
            } else {
                sb.append(encoded, i, charsPerLine);
            }
            sb.append(Strings.NEW_LINE);
        }
        sb.append("-----END ");
        sb.append(type);
        sb.append("-----");
        sb.append(Strings.NEW_LINE);
        return sb.toString();
    }

    public static String toPEM(final PrivateKey key) {
        return key == null ? null : toPEM(key.getEncoded(), "PRIVATE KEY", 64);
    }

    public static String toPEM(final PublicKey key) {
        return key == null ? null : toPEM(key.getEncoded(), "PUBLIC KEY", 64);
    }

    public static String toPEM(final X509Certificate cert) throws CertificateEncodingException {
        return cert == null ? null : toPEM(cert.getEncoded(), "CERTIFICATE", 64);
    }

    public static String toPEM(final X509CRL crl) throws CRLException {
        return crl == null ? null : toPEM(crl.getEncoded(), "X509 CRL", 64);
    }

    private static PrivateKey toPrivateKey(final byte[] pkcs8PrivateKey, final String algorithm) throws GeneralSecurityException {
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8PrivateKey);
        final KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePrivate(spec);
    }

    private static PublicKey toPublicKey(final byte[] x509PublicKey, final String algorithm) throws GeneralSecurityException {
        final X509EncodedKeySpec spec = new X509EncodedKeySpec(x509PublicKey);
        final KeyFactory kf = KeyFactory.getInstance(algorithm);
        return kf.generatePublic(spec);
    }
}
