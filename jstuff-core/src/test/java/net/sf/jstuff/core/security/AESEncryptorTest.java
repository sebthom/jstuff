/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.security;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AESEncryptorTest extends TestCase {
   public void testAES() {
      final byte[] plain = "Hello World!".getBytes();

      AESEncryptor aes = new AESEncryptor("mySalt");
      final byte[] encrypted = aes.encrypt(plain, "mySecretKey");
      assertFalse(Arrays.equals(plain, encrypted));

      aes = new AESEncryptor("mySalt");
      final byte[] decrypted = aes.decrypt(encrypted, "mySecretKey");
      assertTrue(Arrays.equals(plain, decrypted));
   }
}
