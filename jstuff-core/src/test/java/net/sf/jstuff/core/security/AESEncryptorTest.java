/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Verifies byte-array and sealed-object AES-GCM round trips.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class AESEncryptorTest {

   @Test
   void testAES() {
      final byte[] plain = "Hello World!".getBytes();

      var aes = new AESEncryptor("mySalt");
      final byte[] encrypted = aes.encrypt(plain, "mySecretKey");
      assertThat(plain).isNotEqualTo(encrypted);

      aes = new AESEncryptor("mySalt");
      final byte[] decrypted = aes.decrypt(encrypted, "mySecretKey");
      assertThat(plain).isEqualTo(decrypted);
   }

   @Test
   void testSealedObject() {
      final var aes = new AESEncryptor("mySalt");
      final var sealed = aes.seal("Hello World!", "mySecretKey");

      final String unsealed = aes.unseal(sealed, "mySecretKey");
      assertThat(unsealed).isEqualTo("Hello World!");
   }
}
