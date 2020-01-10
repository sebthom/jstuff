/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.SerializationException;

import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SerializationUtils extends org.apache.commons.lang3.SerializationUtils {

   /**
    * @see XMLEncoder
    */
   @SuppressWarnings("resource")
   public static String bean2xml(final Object javaBean) throws SerializationException {
      Args.notNull("javaBean", javaBean);

      final ArrayList<Exception> exceptions = new ArrayList<>(2);
      final FastByteArrayOutputStream bos = new FastByteArrayOutputStream();
      final XMLEncoder encoder = new XMLEncoder(bos);
      encoder.setExceptionListener(ex -> exceptions.add(ex));
      encoder.writeObject(javaBean);
      encoder.close();
      if (exceptions.size() > 0)
         throw new SerializationException("An error occured during XML serialization", exceptions.get(0));
      return bos.toString();
   }

   @SuppressWarnings({"resource"})
   public static <T> T deserialize(final byte[] serializedData) throws SerializationException {
      Args.notNull("serializedData", serializedData);

      final FastByteArrayInputStream bin = new FastByteArrayInputStream(serializedData);
      return deserialize(bin);
   }

   @SuppressWarnings("unchecked")
   public static <T> T deserialize(final InputStream is) throws SerializationException {
      Args.notNull("is", is);

      try (ObjectInputStream ois = new ObjectInputStream(is)) {
         return (T) ois.readObject();
      } catch (final Exception ex) {
         throw new SerializationException("Deserialization failed", ex);
      }
   }

   /**
    * @see XMLDecoder
    */
   @SuppressWarnings("resource")
   public static <T> T xml2bean(final String xmlData) throws SerializationException {
      Args.notNull("xmlData", xmlData);

      final ArrayList<Exception> exceptions = new ArrayList<>(2);
      final FastByteArrayInputStream bis = new FastByteArrayInputStream(xmlData.getBytes());
      final XMLDecoder decoder = new XMLDecoder(bis);
      decoder.setExceptionListener(ex -> exceptions.add(ex));
      @SuppressWarnings("unchecked")
      final T javaBean = (T) decoder.readObject();
      if (exceptions.size() > 0)
         throw new SerializationException("An error occured during XML deserialization", exceptions.get(0));
      return javaBean;
   }
}
