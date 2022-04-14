/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
      return md5(txt.getBytes(StandardCharsets.UTF_8));
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
      return sha1(txt.getBytes(StandardCharsets.UTF_8));
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
      return sha256(txt.getBytes(StandardCharsets.UTF_8));
   }
}
