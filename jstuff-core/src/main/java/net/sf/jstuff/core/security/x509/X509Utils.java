/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security.x509;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
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
import java.time.Duration;
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

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.io.MoreFiles;
import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.security.Base64;
import net.sf.jstuff.core.security.Hash;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class X509Utils {

   private static final Logger LOG = Logger.create();

   public static final CertificateFactory CERTIFICATE_FACTORY;

   private static final Pattern CRL_PATTERN = Pattern.compile("BEGIN X509 CRL-+\r?\n?(.*[^-])\r?\n?-+END X509 CRL", Pattern.DOTALL);
   private static final Pattern CERTIFICATE_PATTERN = Pattern.compile("BEGIN .*CERTIFICATE-+\r?\n?(.*[^-])\r?\n?-+END .*CERTIFICATE",
      Pattern.DOTALL);
   private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile("BEGIN .*PRIVATE KEY-+\r?\n?(.*[^-])\r?\n?-+END .*PRIVATE KEY",
      Pattern.DOTALL);
   private static final Pattern PUBLIC_KEY_PATTERN = Pattern.compile("BEGIN .*PUBLIC KEY-+\r?\n?(.*[^-])\r?\n?-+END .*PUBLIC KEY",
      Pattern.DOTALL);

   static {
      try {
         CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509");
      } catch (final CertificateException ex) {
         throw new IllegalStateException(ex);
      }
   }

   /**
    * Converts a javax.security.cert.X509Certificate to java.security.cert.X509Certificate
    */
   @Deprecated
   public static X509Certificate convert(final javax.security.cert.X509Certificate cert) {
      try (var bis = new FastByteArrayInputStream(cert.getEncoded())) {
         return (X509Certificate) CERTIFICATE_FACTORY.generateCertificate(bis);
      } catch (final Exception ex) {
         throw new IllegalArgumentException("[cert] " + cert + " is not convertable!", ex);
      }
   }

   /**
    * Converts a java.security.cert.X509Certificate to javax.security.cert.X509Certificate
    */
   @Deprecated
   public static javax.security.cert.X509Certificate convert(final X509Certificate cert) {
      try {
         return javax.security.cert.X509Certificate.getInstance(cert.getEncoded());
      } catch (final Exception ex) {
         throw new IllegalArgumentException("[cert] " + cert + " is not convertable!", ex);
      }
   }

   /**
    * Constructs a X509Certificate instance from a DER or PEM encoded certificate
    */
   @SuppressWarnings("resource")
   public static X509Certificate getCertificate(final byte[] data) throws GeneralSecurityException {
      Args.notEmpty("data", data);
      return getCertificate(new FastByteArrayInputStream(data));
   }

   /**
    * Constructs a X509Certificate instance from a DER or PEM encoded certificate
    */
   public static X509Certificate getCertificate(final File file) throws GeneralSecurityException, IOException {
      try (var is = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
         return getCertificate(is);
      }
   }

   /**
    * Constructs a X509Certificate instance from a DER or PEM encoded certificate
    */
   @SuppressWarnings("resource")
   public static X509Certificate getCertificate(final InputStream is) throws GeneralSecurityException {
      Args.notNull("is", is);
      try {
         return (X509Certificate) CERTIFICATE_FACTORY.generateCertificate(IOUtils.toBufferedInputStream(is));
      } finally {
         IOUtils.closeQuietly(is);
      }
   }

   /**
    * Constructs a X509Certificate instance from a PEM encoded certificate
    */
   public static X509Certificate getCertificateFromPEM(final File pemFile) throws GeneralSecurityException, IOException {
      return getCertificateFromPEM(MoreFiles.readAsString(pemFile.toPath()));
   }

   /**
    * Constructs a X509Certificate instance from a PEM encoded certificate
    */
   @SuppressWarnings("resource")
   public static X509Certificate getCertificateFromPEM(final InputStream pemStream) throws GeneralSecurityException, IOException {
      Args.notNull("pemStream", pemStream);
      try {
         return getCertificateFromPEM(IOUtils.toString(IOUtils.toBufferedInputStream(pemStream)));
      } finally {
         IOUtils.closeQuietly(pemStream);
      }
   }

   /**
    * Constructs a X509Certificate instance from a PEM encoded certificate
    */
   @SuppressWarnings("resource")
   public static X509Certificate getCertificateFromPEM(final String pemContent) throws GeneralSecurityException {
      Args.notNull("pemContent", pemContent);
      final Matcher m = CERTIFICATE_PATTERN.matcher(pemContent);
      final byte[] certBytes;
      if (m.find()) {
         certBytes = pemContent.getBytes(StandardCharsets.UTF_8);
      } else {
         certBytes = ("-----BEGIN CERTIFICATE-----\n" + pemContent + "\n-----END CERTIFICATE-----").getBytes(StandardCharsets.UTF_8);
      }
      final Certificate cert = CERTIFICATE_FACTORY.generateCertificate(new FastByteArrayInputStream(certBytes));

      if ("X.509".equals(cert.getType()))
         return (X509Certificate) cert;

      throw new GeneralSecurityException("PEM-encoded certificate [" + pemContent + "] is not X.509 but [" + cert.getType() + "]");
   }

   public static List<X509Certificate> getCertificates(final KeyStore ks) {
      final var certs = new ArrayList<X509Certificate>();
      try {
         for (final Enumeration<String> en = ks.aliases(); en.hasMoreElements();) {
            final Certificate cert = ks.getCertificate(en.nextElement());
            if (cert instanceof final X509Certificate xcert) {
               certs.add(xcert);
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
   public static @Nullable String getCN(final X509Certificate cert) {
      final String subjectPrincipal = cert.getSubjectX500Principal().getName(X500Principal.RFC2253);
      try {
         for (final Rdn rdn : new LdapName(subjectPrincipal).getRdns()) {
            final Attribute cnAttr = rdn.toAttributes().get("cn");
            if (cnAttr != null) {
               try {
                  final Object cnValue = cnAttr.get();
                  if (cnValue != null)
                     return cnValue.toString();
               } catch (final Exception ex) {
                  LOG.debug(ex);
               }
            }
         }
      } catch (final InvalidNameException ex) {
         LOG.debug(ex);
      }
      return null;
   }

   public static List<String> getCNs(final X509Certificate cert) {
      final var cns = new ArrayList<String>();
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
               } catch (final Exception ex) {
                  LOG.debug(ex);
               }
            }
         }
      } catch (final InvalidNameException ex) {
         LOG.debug(ex);
      }
      return cns;
   }

   /**
    * Constructs a X509CRL instance from a PEM encoded CRL
    */
   public static X509CRL getCRLFromPEM(final File pemFile) throws GeneralSecurityException, IOException {
      return getCRLFromPEM(MoreFiles.readAsString(pemFile.toPath()));
   }

   /**
    * Constructs a X509CRL instance from a PEM encoded CRL
    */
   @SuppressWarnings("resource")
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
   @SuppressWarnings("resource")
   public static X509CRL getCRLFromPEM(final String pemContent) throws GeneralSecurityException {
      Args.notNull("pemContent", pemContent);
      final Matcher m = CRL_PATTERN.matcher(pemContent);
      final byte[] certBytes;
      if (m.find()) {
         certBytes = pemContent.getBytes(StandardCharsets.UTF_8);
      } else {
         certBytes = ("-----BEGIN X509 CRL-----\n" + pemContent + "\n-----END X509 CRL-----").getBytes(StandardCharsets.UTF_8);
      }

      final CRL cert = CERTIFICATE_FACTORY.generateCRL(new FastByteArrayInputStream(certBytes));

      if ("X.509".equals(cert.getType()))
         return (X509CRL) cert;

      throw new GeneralSecurityException("PEM-encoded CRL [" + pemContent + "] is not X.509 but [" + cert.getType() + "]");
   }

   public static List<String> getCRLURLs(final X509Certificate cert) { // CHECKSTYLE:IGNORE AbbreviationAsWordInName
      Args.notNull("cert", cert);
      final byte[] crlExtValueRaw = cert.getExtensionValue("2.5.29.31");
      if (crlExtValueRaw == null)
         return Collections.emptyList();

      final var crls = new ArrayList<String>();

      final var crlExtValue = new String(crlExtValueRaw, StandardCharsets.UTF_8);
      int searchPos = 0;
      final var foundAt = new int[4];
      final int notFound = -1;
      while (searchPos + 1 < crlExtValue.length()) {
         foundAt[0] = crlExtValue.indexOf("http", searchPos);
         foundAt[1] = crlExtValue.indexOf("ldap", searchPos);
         foundAt[2] = crlExtValue.indexOf("ftp", searchPos);
         foundAt[3] = crlExtValue.indexOf("file", searchPos);
         Arrays.sort(foundAt);

         int crlStartPos = notFound;
         for (final int i : foundAt) {
            if (i > notFound) {
               crlStartPos = i;
               break;
            }
         }
         if (crlStartPos == notFound) {
            break;
         }

         final int crlEndPos = crlExtValue.indexOf((char) 65533, crlStartPos);
         if (crlEndPos == notFound) {
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
   }

   public static String getFingerprint(final X509Certificate cert) throws CertificateEncodingException {
      return Hash.SHA1.hash(cert.getEncoded());
   }

   /**
    * Extracts the OCSP Responder URL from the given certificate if specified.
    *
    * https://en.wikipedia.org/wiki/Online_Certificate_Status_Protocol
    *
    * @return null if OCSP URL is not specified or is not a HTTP or LDAP URL
    */
   public static @Nullable String getOcspResponderURL(final X509Certificate cert) {
      // https://tools.ietf.org/html/rfc4325#section-2
      final byte[] ocspExtValueRaw = cert.getExtensionValue("1.3.6.1.5.5.7.1.1");
      if (ocspExtValueRaw == null)
         return null;

      final String url;
      final var ocspExtValue = new String(ocspExtValueRaw, StandardCharsets.US_ASCII);
      if (ocspExtValue.contains("http")) {
         url = "http" + Strings.substringAfter(new String(ocspExtValueRaw, StandardCharsets.US_ASCII), "http").trim();
      } else if (ocspExtValue.contains("ldap")) {
         url = "ldap" + Strings.substringAfter(new String(ocspExtValueRaw, StandardCharsets.US_ASCII), "ldap").trim();
      } else {
         url = null;
      }
      return url;
   }

   /**
    * Constructs a private key instance from PKCS#8 PEM encoded private key
    *
    * @throws InvalidKeyException if the private key is not PKCS#8 PEM encoded
    */
   public static PrivateKey getPrivateKeyFromPEM(final File pemFile, final String algorithm) throws GeneralSecurityException, IOException {
      Args.notNull("algorithm", algorithm);
      return getPrivateKeyFromPEM(MoreFiles.readAsString(pemFile.toPath()), algorithm);
   }

   /**
    * Constructs a private key instance from PKCS#8 PEM encoded private key
    *
    * @throws InvalidKeyException if the private key is not PKCS#8 PEM encoded
    */
   @SuppressWarnings("resource")
   public static PrivateKey getPrivateKeyFromPEM(final InputStream pemStream, final String algorithm) throws GeneralSecurityException,
         IOException {
      Args.notNull("pemStream", pemStream);
      Args.notNull("algorithm", algorithm);
      try {
         return getPrivateKeyFromPEM(IOUtils.toString(pemStream), algorithm);
      } finally {
         IOUtils.closeQuietly(pemStream);
      }
   }

   /**
    * Constructs a private key instance from PKCS#8 PEM encoded private key
    *
    * @throws InvalidKeyException if the private key is not PKCS#8 PEM encoded
    */
   public static PrivateKey getPrivateKeyFromPEM(final String pemContent, final String algorithm) throws GeneralSecurityException {
      Args.notNull("algorithm", algorithm);

      // https://stackoverflow.com/questions/15344125/load-a-rsa-private-key-in-java-algid-parse-error-not-a-sequence
      // https://stackoverflow.com/questions/20065304/what-is-the-differences-between-begin-rsa-private-key-and-begin-private-key
      if (pemContent.contains("BEGIN RSA PRIVATE KEY"))
         throw new InvalidKeyException("PKCS#1 PEM encoded private keys are not supported.");

      final Matcher keyMatcher = PRIVATE_KEY_PATTERN.matcher(pemContent);
      final byte[] privateKey;
      if (keyMatcher.find()) {
         privateKey = Base64.decode(asNonNull(keyMatcher.group(1)));
      } else {
         privateKey = Base64.decode(pemContent);
      }
      return toPrivateKey(privateKey, algorithm);
   }

   /**
    * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
    */
   public static PublicKey getPublicKeyFromPEM(final File pemFile, final String algorithm) throws GeneralSecurityException, IOException {
      Args.notNull("algorithm", algorithm);
      return getPublicKeyFromPEM(MoreFiles.readAsString(pemFile.toPath()), algorithm);
   }

   /**
    * Constructs a public key instance from PEM encoded public key, NOT from a PEM encoded certificate
    */
   @SuppressWarnings("resource")
   public static PublicKey getPublicKeyFromPEM(final InputStream pemStream, final String algorithm) throws GeneralSecurityException,
         IOException {
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
         publicKey = Base64.decode(asNonNull(keyMatcher.group(1)));
      } else {
         publicKey = Base64.decode(pemContent);
      }
      return toPublicKey(publicKey, algorithm);
   }

   /**
    * Constructs a private key instance from PEM encoded X509 private key
    */
   @SuppressWarnings("resource")
   public static RSAPrivateKey getRSAPrivateKeyFromPEM(final InputStream pemStream) throws GeneralSecurityException, IOException {
      Args.notNull("pemStream", pemStream);
      return (RSAPrivateKey) getPrivateKeyFromPEM(pemStream, "RSA");
   }

   /**
    * Constructs a private key instance from PEM encoded X509 private key
    */
   public static RSAPrivateKey getRSAPrivateKeyFromPEM(final String pemContent) throws GeneralSecurityException {
      Args.notNull("pemContent", pemContent);
      return (RSAPrivateKey) getPrivateKeyFromPEM(pemContent, "RSA");
   }

   /**
    * Constructs a public key instance from PEM encoded X509 public key, NOT from a PEM encoded certificate
    */
   @SuppressWarnings("resource")
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

   /**
    * Gets the duration (from now) the given certificate is valid.
    */
   public static Duration getValidityDuration(final X509Certificate cert) {
      if (!isValid(cert))
         return Duration.ZERO;

      final long durationMS = cert.getNotAfter().getTime() - System.currentTimeMillis();
      if (durationMS < 1)
         return Duration.ZERO;
      return Duration.ofMillis(durationMS);
   }

   /**
    * Performs a case-insensitive comparison of the DNs ignoring whitespaces between name components.
    */
   public static boolean isEqualDN(@Nullable String dn1, @Nullable String dn2) {
      if (dn1 == null)
         return dn2 == null;
      if (dn2 == null)
         return false;
      if (dn1.equalsIgnoreCase(dn2))
         return true;

      if (dn1.contains(", ")) {
         final String[] parts = Strings.splitPreserveAllTokens(dn1, ',');
         for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
         }
         dn1 = Strings.join(parts, ',');
      }

      if (dn2.contains(", ")) {
         final String[] parts = Strings.splitPreserveAllTokens(dn2, ',');
         for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
         }
         dn2 = Strings.join(parts, ',');
      }
      return asNonNullUnsafe(dn1).equalsIgnoreCase(dn2);
   }

   public static boolean isExpired(final X509Certificate cert) {
      return cert.getNotAfter().getTime() < System.currentTimeMillis();
   }

   /**
    * Performs a case-insensitive comparison of the issuer DNs.
    */
   public static boolean isIssuerDN(final @Nullable X509Certificate cert, final @Nullable String issuerDN) {
      if (cert == null || issuerDN == null)
         return false;

      return isEqualDN(cert.getIssuerX500Principal().getName(), issuerDN);
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

   /**
    * Performs a case-insensitive comparison of the subject DNs.
    */
   public static boolean isSubjectDN(final @Nullable X509Certificate cert, final @Nullable String subjectDN) {
      if (cert == null || subjectDN == null)
         return false;

      return isEqualDN(cert.getSubjectX500Principal().getName(), subjectDN);
   }

   /**
    * @return true if the current date/time is within the validity period given in the certificate
    */
   public static boolean isValid(final @Nullable X509Certificate cert) {
      if (cert == null)
         return false;

      try {
         cert.checkValidity();
         return true;
      } catch (final CertificateExpiredException | CertificateNotYetValidException ex) {
         return false;
      }
   }

   public static boolean isValidFor(final @Nullable X509Certificate cert, final Duration duration) {
      if (cert == null)
         return false;

      Args.notNull("duration", duration);
      return getValidityDuration(cert).compareTo(duration) >= 0;
   }

   public static boolean isX509Certificate(final @Nullable Certificate cert) {
      if (cert == null)
         return false;
      return cert instanceof X509Certificate;
   }

   private static @Nullable String toPEM(final byte @Nullable [] data, final String type, final int charsPerLine) {
      if (data == null)
         return null;

      final char[] encoded = Base64.encode(data).toCharArray();
      final var sb = new StringBuilder(encoded.length + 70);
      sb.append("-----BEGIN ").append(type).append("-----").append(Strings.NEW_LINE);
      for (int i = 0; i < encoded.length; i += charsPerLine) {
         if (encoded.length - i < charsPerLine) {
            sb.append(encoded, i, encoded.length - i);
         } else {
            sb.append(encoded, i, charsPerLine);
         }
         sb.append(Strings.NEW_LINE);
      }
      sb.append("-----END ").append(type).append("-----").append(Strings.NEW_LINE);
      return sb.toString();
   }

   public static @Nullable String toPEM(final @Nullable PrivateKey key) {
      return key == null ? null : toPEM(key.getEncoded(), "PRIVATE KEY", 64);
   }

   public static @Nullable String toPEM(final @Nullable PublicKey key) {
      return key == null ? null : toPEM(key.getEncoded(), "PUBLIC KEY", 64);
   }

   public static @Nullable String toPEM(final @Nullable X509Certificate cert) throws CertificateEncodingException {
      return cert == null ? null : toPEM(cert.getEncoded(), "CERTIFICATE", 64);
   }

   public static @Nullable String toPEM(final @Nullable X509CRL crl) throws CRLException {
      return crl == null ? null : toPEM(crl.getEncoded(), "X509 CRL", 64);
   }

   private static PrivateKey toPrivateKey(final byte[] pkcs8PrivateKey, final String algorithm) throws GeneralSecurityException {
      final var spec = new PKCS8EncodedKeySpec(pkcs8PrivateKey);
      return KeyFactory.getInstance(algorithm).generatePrivate(spec);
   }

   private static PublicKey toPublicKey(final byte[] x509PublicKey, final String algorithm) throws GeneralSecurityException {
      final var spec = new X509EncodedKeySpec(x509PublicKey);
      return KeyFactory.getInstance(algorithm).generatePublic(spec);
   }
}
