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
package net.sf.jstuff.core.io;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.CryptoUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SerializationUtils
{
	public static final class CloningFailedException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		protected CloningFailedException(final String message, final Throwable cause)
		{
			super(message, cause);
		}
	}

	public static final class ObjectSerializationException extends RuntimeException
	{
		private static final long serialVersionUID = 1L;

		public ObjectSerializationException(final String message, final Throwable cause)
		{
			super(message, cause);
		}
	}

	/**
	 * @see XMLEncoder
	 * @throws ObjectSerializationException
	 */
	public static String bean2xml(final Object javaBean) throws ObjectSerializationException
	{
		Assert.argumentNotNull("javaBean", javaBean);

		final ArrayList<Exception> exList = new ArrayList<Exception>(2);
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
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
		if (exList.size() > 0)
			throw new ObjectSerializationException("An error occured during XML serialization", exList.get(0));
		return bos.toString();
	}

	/**
	 * Returns a deep copy of an object.
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deepCopy(final T oldObj) throws CloningFailedException
	{
		Assert.argumentNotNull("oldObj", oldObj);

		ObjectOutputStream os = null;
		ObjectInputStream is = null;

		try
		{
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);

			// serialize the object
			os.writeObject(oldObj);
			os.flush();

			final ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
			is = new ObjectInputStream(bin);
			return (T) is.readObject();
		}
		catch (final Exception ex)
		{
			throw new CloningFailedException("Cloning of object " + oldObj + " failed.", ex);
		}
		finally
		{
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(is);
		}
	}

	public static Serializable deserialize(final byte[] serializedData) throws ObjectSerializationException
	{
		Assert.argumentNotNull("serializedData", serializedData);

		final ByteArrayInputStream bin = new ByteArrayInputStream(serializedData);
		return deserialize(bin);
	}

	public static Serializable deserialize(final InputStream is) throws ObjectSerializationException
	{
		Assert.argumentNotNull("is", is);

		ObjectInputStream ois = null;
		try
		{
			ois = new ObjectInputStream(is);
			return (Serializable) ois.readObject();
		}
		catch (final Exception e)
		{
			throw new ObjectSerializationException("Deserialization failed", e);
		}
		finally
		{
			IOUtils.closeQuietly(ois);
		}
	}

	/**
	 * Deserializes an object from the given DES encrypted byte array
	 */
	public static Serializable deserializeDES(final byte[] data, final String passphrase)
	{
		return deserialize(CryptoUtils.decryptWithDES(data, passphrase));
	}

	public static byte[] serialize(final Serializable obj) throws ObjectSerializationException
	{
		Assert.argumentNotNull("obj", obj);

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		serialize(obj, bos);
		return bos.toByteArray();
	}

	public static void serialize(final Serializable obj, final OutputStream os) throws ObjectSerializationException
	{
		Assert.argumentNotNull("obj", obj);
		Assert.argumentNotNull("os", os);

		ObjectOutputStream oos = null;
		try
		{
			oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
			oos.flush();
		}
		catch (final Exception e)
		{
			throw new ObjectSerializationException("Serialization of object [" + obj + "] failed.", e);
		}
		finally
		{
			IOUtils.closeQuietly(oos);
		}
	}

	/**
	 * Serializes the given object and encrypts it via DES using the given passphrase
	 */
	public static byte[] serializeDES(final Serializable obj, final String passphrase)
	{
		return CryptoUtils.encryptWithDES(serialize(obj), passphrase);
	}

	/**
	 * @see XMLDecoder
	 * @throws ObjectSerializationException
	 */
	public static Object xml2bean(final String xmlData) throws ObjectSerializationException
	{
		Assert.argumentNotNull("xmlData", xmlData);

		final ArrayList<Exception> exList = new ArrayList<Exception>(2);
		final ByteArrayInputStream bis = new ByteArrayInputStream(xmlData.getBytes());
		final XMLDecoder d = new XMLDecoder(bis);
		d.setExceptionListener(new ExceptionListener()
			{
				public void exceptionThrown(final Exception ex)
				{
					exList.add(ex);
				}
			});
		final Object javaBean = d.readObject();
		if (exList.size() > 0)
			throw new ObjectSerializationException("An error occured during XML deserialization", exList.get(0));
		return javaBean;
	}

	protected SerializationUtils()
	{
		super();
	}
}
