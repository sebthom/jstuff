package net.sf.jstuff.core;

import java.util.Arrays;

import junit.framework.TestCase;

/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
public class CryptoTest extends TestCase
{
	public void testAES()
	{
		final byte[] plain = "Hello World!".getBytes();
		final byte[] encrypted = Crypto.encryptWithAES(plain, "mySecretKey");
		assertFalse(Arrays.equals(plain, encrypted));
		final byte[] decrypted = Crypto.decryptWithAES(encrypted, "mySecretKey");
		assertTrue(Arrays.equals(plain, decrypted));
	}
}
