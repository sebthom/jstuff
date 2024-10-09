/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
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
}
