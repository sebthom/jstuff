/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Map;
import java.util.WeakHashMap;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.io.SerializationUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AESEncryptor {
   public static final class AESSealedObject extends SealedObject {
      private static final long serialVersionUID = 1L;

      private byte[] iv;

      public AESSealedObject(final Serializable obj, final Cipher cipher) throws IOException, IllegalBlockSizeException {
         super(obj, cipher);
      }
   }

   private final Map<String, SecretKey> _cachedAESKeys = new WeakHashMap<String, SecretKey>();

   private final ThreadLocal<Cipher> _ciphers = new ThreadLocal<Cipher>() {
      @Override
      protected Cipher initialValue() {
         try {
            return Cipher.getInstance("AES/CBC/PKCS5Padding");
         } catch (final GeneralSecurityException ex) {
            throw new SecurityException(ex);
         }
      }
   };

   private final byte[] _keySalt;

   public AESEncryptor(final byte[] keySalt) {
      _keySalt = keySalt;
   }

   public AESEncryptor(final String keySalt) {
      try {
         _keySalt = keySalt.getBytes("UTF-8");
      } catch (final UnsupportedEncodingException ex) {
         throw new RuntimeException(ex);
      }
   }

   private SecretKey _getKey(final String passphrase) throws NoSuchAlgorithmException, InvalidKeySpecException {
      SecretKey key = _cachedAESKeys.get(passphrase);
      if (key == null) {
         final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
         final KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), _keySalt, 1024, 128);
         key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
         _cachedAESKeys.put(passphrase, key);
      }
      return key;
   }

   /**
    * @param data first 16 bytes of the array expected to be the initial vector
    */
   public byte[] decrypt(final byte[] data, final String passphrase) throws SecurityException {
      Args.notNull("data", data);

      try {
         final SecretKey key = _getKey(passphrase);
         final Cipher cipher = _ciphers.get();
         // the first 16 bytes are the initial vector
         final byte[] iv = ArrayUtils.subarray(data, 0, 16);
         final byte[] encrypted = ArrayUtils.subarray(data, 16, data.length);
         cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
         return cipher.doFinal(encrypted);
      } catch (final GeneralSecurityException ex) {
         throw new SecurityException(ex);
      }
   }

   @SuppressWarnings("unchecked")
   public <T extends Serializable> T deserialize(final byte[] data, final String passphrase) {
      return (T) SerializationUtils.deserialize(decrypt(data, passphrase));
   }

   /**
    * @return first 16 bytes of the array are the initial vector
    */
   public byte[] encrypt(final byte[] data, final String passphrase) throws SecurityException {
      Args.notNull("data", data);

      try {
         final SecretKey key = _getKey(passphrase);
         final Cipher cipher = _ciphers.get();
         // generate a new initial vector for each encryption
         final byte[] iv = Crypto.createRandomBytes(16);
         cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
         final byte[] encrypted = cipher.doFinal(data);
         // the first 16 bytes are the initial vector
         return ArrayUtils.addAll(iv, encrypted);
      } catch (final GeneralSecurityException ex) {
         throw new SecurityException(ex);
      }
   }

   public AESSealedObject seal(final Serializable object, final String passphrase) throws SecurityException {
      Args.notNull("object", object);

      try {
         final SecretKey key = _getKey(passphrase);
         final Cipher cipher = _ciphers.get();
         // generate a new initial vector on each invocation
         final byte[] iv = Crypto.createRandomBytes(16);
         cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
         final AESSealedObject sealedObject = new AESSealedObject(object, cipher);
         sealedObject.iv = iv;
         return sealedObject;
      } catch (final Exception ex) {
         throw new SecurityException(ex);
      }
   }

   public byte[] serialize(final Serializable object, final String passphrase) {
      return encrypt(SerializationUtils.serialize(object), passphrase);
   }

   @SuppressWarnings("unchecked")
   public <T extends Serializable> T unseal(final AESSealedObject object, final String passphrase) throws SecurityException {
      Args.notNull("object", object);

      try {
         final SecretKey key = _getKey(passphrase);
         final Cipher cipher = _ciphers.get();
         cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(object.iv));
         return (T) object.getObject(cipher);
      } catch (final Exception ex) {
         throw new SecurityException(ex);
      }
   }
}
