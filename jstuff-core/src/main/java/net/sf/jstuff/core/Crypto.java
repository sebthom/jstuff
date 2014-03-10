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
package net.sf.jstuff.core;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Map;
import java.util.WeakHashMap;

import javax.crypto.Cipher;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Crypto
{
	public static final class CryptoException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public CryptoException(final Throwable cause)
		{
			super(cause);
		}
	}

	private static final byte[] AES_IV = {(byte) 0x23, (byte) 0x22, (byte) 0xf8, (byte) 0x98, (byte) 0x3a, (byte) 0xbc, (byte) 0xf8,
			(byte) 0x22, (byte) 0x99, (byte) 0xff, (byte) 0x34, (byte) 0xc2, (byte) 0xc7, (byte) 0xd2, (byte) 0x24, (byte) 0xc5};

	private static final byte[] AES_SALT = {(byte) 0xf3, (byte) 0x31, (byte) 0x43, (byte) 0x13, (byte) 0x7d, (byte) 0x22, (byte) 0xad,
			(byte) 0x04};;

	private static final Map<String, SecretKey> CACHED_AES_KEYS = new WeakHashMap<String, SecretKey>();

	public static byte[] decryptWithAES(final byte[] data, final String passphrase) throws CryptoException
	{
		try
		{
			return getAESCipher(passphrase, Cipher.DECRYPT_MODE).doFinal(data);
		}
		catch (final GeneralSecurityException ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static byte[] encryptWithAES(final byte[] data, final String passphrase) throws CryptoException
	{
		try
		{
			return getAESCipher(passphrase, Cipher.ENCRYPT_MODE).doFinal(data);
		}
		catch (final GeneralSecurityException ex)
		{
			throw new CryptoException(ex);
		}
	}

	private static Cipher getAESCipher(final String passphrase, final int mode) throws GeneralSecurityException
	{
		SecretKey key = CACHED_AES_KEYS.get(passphrase);
		if (key == null)
		{
			final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			final KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), AES_SALT, 1024, 128);
			key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");

			CACHED_AES_KEYS.put(passphrase, key);
		}
		final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(mode, key, new IvParameterSpec(AES_IV));
		return cipher;
	}

	public static String getMD5(final String txt) throws CryptoException
	{
		try
		{
			final MessageDigest md = MessageDigest.getInstance("MD5");
			final BigInteger number = new BigInteger(1, md.digest(txt.getBytes("UTF-8")));
			return StringUtils.leftPad(number.toString(16), 32, '0');
		}
		catch (final NoSuchAlgorithmException ex)
		{
			throw new CryptoException(ex);
		}
		catch (final UnsupportedEncodingException ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static String getSHA1(final String txt) throws CryptoException
	{
		try
		{
			final MessageDigest md = MessageDigest.getInstance("SHA-1");
			final BigInteger number = new BigInteger(1, md.digest(txt.getBytes("UTF-8")));
			return StringUtils.leftPad(number.toString(16), 48, '0');
		}
		catch (final NoSuchAlgorithmException ex)
		{
			throw new CryptoException(ex);
		}
		catch (final UnsupportedEncodingException ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static String getSHA256(final String txt) throws CryptoException
	{
		try
		{
			final MessageDigest md = MessageDigest.getInstance("SHA-256");
			final BigInteger number = new BigInteger(1, md.digest(txt.getBytes("UTF-8")));
			return StringUtils.leftPad(number.toString(16), 64, '0');
		}
		catch (final NoSuchAlgorithmException ex)
		{
			throw new CryptoException(ex);
		}
		catch (final UnsupportedEncodingException ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static SealedObject sealWithAES(final Serializable object, final String passphrase) throws CryptoException
	{
		Args.notNull("object", object);

		try
		{
			return new SealedObject(object, getAESCipher(passphrase, Cipher.ENCRYPT_MODE));
		}
		catch (final Exception ex)
		{
			throw new CryptoException(ex);
		}
	}

	public static Serializable unsealWithAES(final SealedObject object, final String passphrase) throws CryptoException
	{
		Args.notNull("object", object);

		try
		{
			return (Serializable) object.getObject(getAESCipher(passphrase, Cipher.DECRYPT_MODE));
		}
		catch (final Exception ex)
		{
			throw new CryptoException(ex);
		}
	}
}
