/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Crypto {

   private static final ThreadLocalSecureRandom SECURE_RANDOM = ThreadLocalSecureRandom.builder().reseedEvery(Duration.ofMinutes(30)).build();

   public static byte[] createRandomBytes(final int numBytes) {
      final byte[] arr = new byte[numBytes];
      SECURE_RANDOM.nextBytes(arr);
      return arr;
   }

   public static CharSequence createRandomDigits(final int len) {
      final StringBuilder sb = new StringBuilder(len);
      for (final byte b : createRandomBytes(len)) {
         sb.append((char) (48 + Math.abs(b % 10)));
      }
      return sb;
   }

   /**
    * @param keysize e.g. 1024
    * @param algorithm e.g. RSA
    */
   public KeyPair newKeyPair(final int keysize, final String algorithm) throws NoSuchAlgorithmException {
      final KeyPairGenerator kg = KeyPairGenerator.getInstance(algorithm);
      kg.initialize(keysize, SECURE_RANDOM);
      return kg.genKeyPair();
   }

   /**
    * @param keysize e.g. 512
    * @param algorithm e.g. AES
    */
   public SecretKey newSecretKey(final int keysize, final String algorithm) throws NoSuchAlgorithmException {
      final KeyGenerator kg = KeyGenerator.getInstance(algorithm);
      kg.init(keysize, SECURE_RANDOM);
      return kg.generateKey();
   }
}
