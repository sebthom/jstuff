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

import java.util.Arrays;

import junit.framework.TestCase;
import net.sf.jstuff.core.security.AESEncryptor;

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
