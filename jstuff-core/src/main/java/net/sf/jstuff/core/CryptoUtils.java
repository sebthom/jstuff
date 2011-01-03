/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.core;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.WeakHashMap;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CryptoUtils
{
	public static final class CryptoException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public CryptoException(final Throwable cause)
		{
			super(cause);
		}
	}

	private static final Map<String, SecretKey> CACHED_KEYS = new WeakHashMap<String, SecretKey>();

	public static byte[] decryptWithDES(final byte[] data, final String passphrase) throws CryptoException
	{
		try
		{
			return getDESCipher(passphrase, Cipher.DECRYPT_MODE).doFinal(data);
		}
		catch (final Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static byte[] encryptWithDES(final byte[] data, final String passphrase) throws CryptoException
	{
		try
		{
			return getDESCipher(passphrase, Cipher.ENCRYPT_MODE).doFinal(data);
		}
		catch (final Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	private static Cipher getDESCipher(final String passphrase, final int mode) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, InvalidKeySpecException
	{
		SecretKey key = CACHED_KEYS.get(passphrase);
		if (key == null)
		{
			final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			key = keyFactory.generateSecret(new DESedeKeySpec(passphraseToKey(passphrase)));
			CACHED_KEYS.put(passphrase, key);
		}
		final Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(mode, key);
		return cipher;
	}

	private static byte[] passphraseToKey(final String passphrase) throws CryptoException
	{
		try
		{
			// we only need 192 bit for TripleDES but it does not matter if we have 256 bit here
			return MessageDigest.getInstance("SHA-256").digest(passphrase.getBytes());
		}
		catch (final Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static SealedObject sealWithDES(final Serializable object, final String passphrase) throws CryptoException
	{
		try
		{
			return new SealedObject(object, getDESCipher(passphrase, Cipher.ENCRYPT_MODE));
		}
		catch (final Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static Serializable unsealWithDES(final SealedObject obj, final String passphrase) throws CryptoException
	{
		try
		{
			return (Serializable) obj.getObject(getDESCipher(passphrase, Cipher.DECRYPT_MODE));
		}
		catch (final Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	protected CryptoUtils()
	{
		super();
	}
}
