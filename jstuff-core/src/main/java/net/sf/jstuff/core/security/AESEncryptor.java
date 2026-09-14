/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.io.SerializationUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * Encrypts byte arrays and serializable objects with passphrase-derived AES-GCM keys.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AESEncryptor {
   public static final class AESSealedObject extends SealedObject {
      private static final long serialVersionUID = 1L;

      private final byte[] iv;

      public AESSealedObject(final Serializable obj, final Cipher cipher) throws IOException, IllegalBlockSizeException {
         super(obj, cipher);

         final byte[] cipherIV = cipher.getIV();
         if (cipherIV == null)
            throw new IllegalArgumentException("Cipher did not provide an initialization vector.");

         // Store the provider's actual IV instead of assuming it retained the requested bytes unchanged.
         iv = cipherIV;
      }
   }

   // https://crypto.stackexchange.com/a/26787
   private static final int IV_SIZE = 12;
   private static final int AUTH_TAG_LEN = 128;

   private final Map<String, SecretKey> cachedAESKeys = new WeakHashMap<>();
   private final ThreadLocal<Cipher> ciphers = ThreadLocal.withInitial(() -> {
      try {
         return Cipher.getInstance("AES/GCM/NoPadding");
      } catch (final GeneralSecurityException ex) {
         throw new SecurityException(ex);
      }
   });
   private final byte[] keySalt;

   public AESEncryptor(final byte[] keySalt) {
      this.keySalt = keySalt;
   }

   public AESEncryptor(final String keySalt) {
      this.keySalt = keySalt.getBytes(StandardCharsets.UTF_8);
   }

   /**
    * @param data first 12 bytes of the array expected to be the initialization vector
    */
   public byte[] decrypt(final byte[] data, final String passphrase) throws SecurityException {
      Args.notNull("data", data);

      try {
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         // the first IV_SIZE bytes are the initialization vector
         final byte[] iv = Arrays.copyOfRange(data, 0, IV_SIZE);
         final byte[] encrypted = Arrays.copyOfRange(data, IV_SIZE, data.length);
         cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
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
    * @return first 12 bytes of the array are the initialization vector
    */
   public byte[] encrypt(final byte[] data, final String passphrase) throws SecurityException {
      Args.notNull("data", data);

      try {
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         // Reusing a GCM IV with the cached passphrase-derived key would break GCM's security guarantees.
         final byte[] iv = Crypto.createRandomBytes(IV_SIZE);
         cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
         final byte[] encrypted = cipher.doFinal(data);
         // the first IV_SIZE bytes are the initialization vector
         return asNonNull(ArrayUtils.addAll(iv, encrypted));
      } catch (final GeneralSecurityException ex) {
         throw new SecurityException(ex);
      }
   }

   private SecretKey getKey(final String passphrase) throws NoSuchAlgorithmException, InvalidKeySpecException {
      SecretKey key = cachedAESKeys.get(passphrase);
      if (key == null) {
         final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
         final var spec = new PBEKeySpec(passphrase.toCharArray(), keySalt, 1024, 128);
         final byte[] encodedKey = factory.generateSecret(spec).getEncoded();
         if (encodedKey == null)
            throw new InvalidKeySpecException("PBKDF2 provider returned a key without an encoding");
         key = new SecretKeySpec(encodedKey, "AES");
         cachedAESKeys.put(passphrase, key);
      }
      return key;
   }

   public AESSealedObject seal(final Serializable object, final String passphrase) throws SecurityException {
      Args.notNull("object", object);

      try {
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         // Reusing a GCM IV with the cached passphrase-derived key would break GCM's security guarantees.
         final byte[] iv = Crypto.createRandomBytes(IV_SIZE);
         cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
         return new AESSealedObject(object, cipher);
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
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, object.iv));
         return (T) object.getObject(cipher);
      } catch (final Exception ex) {
         throw new SecurityException(ex);
      }
   }
}
