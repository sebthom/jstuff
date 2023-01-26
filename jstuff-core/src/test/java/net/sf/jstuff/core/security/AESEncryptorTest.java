/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.security;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AESEncryptorTest {

   @Test
   public void testAES() {
      final byte[] plain = "Hello World!".getBytes();

      var aes = new AESEncryptor("mySalt");
      final byte[] encrypted = aes.encrypt(plain, "mySecretKey");
      assertThat(plain).isNotEqualTo(encrypted);

      aes = new AESEncryptor("mySalt");
      final byte[] decrypted = aes.decrypt(encrypted, "mySecretKey");
      assertThat(plain).isEqualTo(decrypted);
   }
}
