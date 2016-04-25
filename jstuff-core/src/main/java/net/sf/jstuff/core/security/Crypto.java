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
package net.sf.jstuff.core.security;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.io.IOUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Crypto {
    private static final class LazyInitialized {
        private static final SecureRandom _RANDOM = new SecureRandom();
    }

    public static byte[] createRandomBytes(final int numBytes) {
        final byte[] arr = new byte[numBytes];
        LazyInitialized._RANDOM.nextBytes(arr);
        return arr;
    }

    public static CharSequence createRandomDigits(final int len) {
        final StringBuilder sb = new StringBuilder(len);
        for (final byte b : createRandomBytes(len))
            sb.append((char) (48 + Math.abs(b % 10)));
        return sb;
    }

    /**
     * Creates a X509 certificate object from a PEM encoded certificate stored in a file
     * 
     * @param pemFile The PEM encoded certificate file
     */
    public Certificate getCertificateFromPEMFile(final File pemFile) throws CertificateException, FileNotFoundException {
        final InputStream is = new BufferedInputStream(new FileInputStream(pemFile));
        try {
            return CertificateFactory.getInstance("X.509").generateCertificate(is);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    /**
     * @param keysize e.g. 1024
     * @param algorithm e.g. RSA
     */
    public KeyPair newKeyPair(final int keysize, final String algorithm) throws NoSuchAlgorithmException {
        final KeyPairGenerator kg = KeyPairGenerator.getInstance(algorithm);
        kg.initialize(keysize, LazyInitialized._RANDOM);
        return kg.genKeyPair();
    }

    /**
     * @param keysize e.g. 512
     * @param algorithm e.g. AES
     */
    public SecretKey newSecretKey(final int keysize, final String algorithm) throws NoSuchAlgorithmException {
        final KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        kg.init(keysize, LazyInitialized._RANDOM);
        return kg.generateKey();
    }
}
