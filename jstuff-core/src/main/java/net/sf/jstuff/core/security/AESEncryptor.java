/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

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

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.io.SerializationUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AESEncryptor {
   public static final class AESSealedObject extends SealedObject {
      private static final long serialVersionUID = 1L;

      private byte @Nullable [] iv;

      public AESSealedObject(final Serializable obj, final Cipher cipher) throws IOException, IllegalBlockSizeException {
         super(obj, cipher);
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
    * @param data first 16 bytes of the array expected to be the initial vector
    */
   public byte[] decrypt(final byte[] data, final String passphrase) throws SecurityException {
      Args.notNull("data", data);

      try {
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         // the first IV_SIZE bytes are the initial vector
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
    * @return first 16 bytes of the array are the initial vector
    */
   public byte[] encrypt(final byte[] data, final String passphrase) throws SecurityException {
      Args.notNull("data", data);

      try {
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         // generate a new initial vector for each encryption
         final byte[] iv = Crypto.createRandomBytes(IV_SIZE);
         cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
         final byte[] encrypted = cipher.doFinal(data);
         // the first IV_SIZE bytes are the initial vector
         return ArrayUtils.addAll(iv, encrypted);
      } catch (final GeneralSecurityException ex) {
         throw new SecurityException(ex);
      }
   }

   private SecretKey getKey(final String passphrase) throws NoSuchAlgorithmException, InvalidKeySpecException {
      SecretKey key = cachedAESKeys.get(passphrase);
      if (key == null) {
         final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
         final var spec = new PBEKeySpec(passphrase.toCharArray(), keySalt, 1024, 128);
         key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
         cachedAESKeys.put(passphrase, key);
      }
      return key;
   }

   public AESSealedObject seal(final Serializable object, final String passphrase) throws SecurityException {
      Args.notNull("object", object);

      try {
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         // generate a new initial vector on each invocation
         final byte[] iv = Crypto.createRandomBytes(16);
         cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, iv));
         final var sealedObject = new AESSealedObject(object, cipher);
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
         final SecretKey key = getKey(passphrase);
         final Cipher cipher = ciphers.get();
         cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(AUTH_TAG_LEN, object.iv));
         return (T) object.getObject(cipher);
      } catch (final Exception ex) {
         throw new SecurityException(ex);
      }
   }
}
