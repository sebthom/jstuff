/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.core.io;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import net.sf.jstuff.core.crypto.Crypto;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.SerializationException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SerializationUtils extends org.apache.commons.lang3.SerializationUtils
{
	/**
	 * @see XMLEncoder
	 * @throws SerializationException
	 */
	@SuppressWarnings("resource")
	public static String bean2xml(final Object javaBean) throws SerializationException
	{
		Args.notNull("javaBean", javaBean);

		final ArrayList<Exception> exList = new ArrayList<Exception>(2);
		final FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
		final XMLEncoder e = new XMLEncoder(bos);
		e.setExceptionListener(new ExceptionListener()
			{
				public void exceptionThrown(final Exception ex)
				{
					exList.add(ex);
				}
			});
		e.writeObject(javaBean);
		e.close();
		if (exList.size() > 0) throw new SerializationException("An error occured during XML serialization", exList.get(0));
		return bos.toString();
	}

	@SuppressWarnings("resource")
	public static Serializable deserialize(final byte[] serializedData) throws SerializationException
	{
		Args.notNull("serializedData", serializedData);

		final FastByteArrayInputStream bin = new FastByteArrayInputStream(serializedData);
		return deserialize(bin);
	}

	public static Serializable deserialize(final InputStream is) throws SerializationException
	{
		Args.notNull("is", is);

		ObjectInputStream ois = null;
		try
		{
			ois = new ObjectInputStream(is);
			return (Serializable) ois.readObject();
		}
		catch (final Exception e)
		{
			throw new SerializationException("Deserialization failed", e);
		}
		finally
		{
			IOUtils.closeQuietly(ois);
		}
	}

	/**
	 * Deserializes an object from the given AES encrypted byte array
	 */
	public static Serializable deserializeAES(final byte[] data, final String passphrase)
	{
		return deserialize(Crypto.decryptWithAES(data, passphrase));
	}

	/**
	 * Serializes the given object and encrypts it via AES using the given passphrase
	 */
	public static byte[] serializeAES(final Serializable obj, final String passphrase)
	{
		return Crypto.encryptWithAES(serialize(obj), passphrase);
	}

	/**
	 * @see XMLDecoder
	 * @throws SerializationException
	 */
	@SuppressWarnings("resource")
	public static Object xml2bean(final String xmlData) throws SerializationException
	{
		Args.notNull("xmlData", xmlData);

		final ArrayList<Exception> exList = new ArrayList<Exception>(2);
		final FastByteArrayInputStream bis = new FastByteArrayInputStream(xmlData.getBytes());
		final XMLDecoder d = new XMLDecoder(bis);
		d.setExceptionListener(new ExceptionListener()
			{
				public void exceptionThrown(final Exception ex)
				{
					exList.add(ex);
				}
			});
		final Object javaBean = d.readObject();
		if (exList.size() > 0) throw new SerializationException("An error occured during XML deserialization", exList.get(0));
		return javaBean;
	}
}
