/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Checksums {

    public static String md5(final byte[] val) throws SecurityException {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final BigInteger number = new BigInteger(1, md.digest(val));
            return Strings.leftPad(number.toString(16), 32, '0');
        } catch (final NoSuchAlgorithmException ex) {
            throw new SecurityException(ex);
        }
    }

    public static String md5(final String txt) throws SecurityException {
        try {
            return md5(txt.getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException ex) {
            throw new SecurityException(ex);
        }
    }

    public static String sha1(final byte[] val) throws SecurityException {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final BigInteger number = new BigInteger(1, md.digest(val));
            return Strings.leftPad(number.toString(16), 40, '0');
        } catch (final NoSuchAlgorithmException ex) {
            throw new SecurityException(ex);
        }
    }

    public static String sha1(final String txt) throws SecurityException {
        try {
            return sha1(txt.getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException ex) {
            throw new SecurityException(ex);
        }
    }

    public static String sha256(final byte[] val) throws SecurityException {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final BigInteger number = new BigInteger(1, md.digest(val));
            return Strings.leftPad(number.toString(16), 64, '0');
        } catch (final NoSuchAlgorithmException ex) {
            throw new SecurityException(ex);
        }
    }

    public static String sha256(final String txt) throws SecurityException {
        try {
            return sha256(txt.getBytes("UTF-8"));
        } catch (final UnsupportedEncodingException ex) {
            throw new SecurityException(ex);
        }
    }
}
