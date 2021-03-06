/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationException;

import net.sf.jstuff.core.io.stream.FastByteArrayInputStream;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SerializationUtils extends org.apache.commons.lang3.SerializationUtils {

   /**
    * @see XMLEncoder
    */
   public static String bean2xml(final Object javaBean) throws SerializationException {
      Args.notNull("javaBean", javaBean);

      final List<Exception> exceptions = new ArrayList<>(2);
      try (FastByteArrayOutputStream bos = new FastByteArrayOutputStream()) {
         try (XMLEncoder encoder = new XMLEncoder(bos)) {
            encoder.setExceptionListener(exceptions::add);
            encoder.writeObject(javaBean);
         }
         if (exceptions.isEmpty())
            return bos.toString();
         throw new SerializationException("An error occured during XML serialization", exceptions.get(0));
      }
   }

   @SuppressWarnings({"resource"})
   public static <T> T deserialize(final byte[] serializedData) throws SerializationException {
      Args.notNull("serializedData", serializedData);

      final FastByteArrayInputStream bin = new FastByteArrayInputStream(serializedData);
      return deserialize(bin);
   }

   @SuppressWarnings({"resource", "unchecked"})
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
   public static <T> T xml2bean(final String xmlData) throws SerializationException {
      Args.notNull("xmlData", xmlData);

      try (FastByteArrayInputStream bis = new FastByteArrayInputStream(xmlData.getBytes(Charset.defaultCharset()));
           XMLDecoder decoder = new XMLDecoder(bis) //
      ) {
         final List<Exception> exceptions = new ArrayList<>(2);
         decoder.setExceptionListener(exceptions::add);
         @SuppressWarnings("unchecked")
         final T javaBean = (T) decoder.readObject();
         if (exceptions.isEmpty())
            return javaBean;
         throw new SerializationException("An error occured during XML deserialization", exceptions.get(0));
      }
   }
}
