/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jdt.annotation.Nullable;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.SchemaOutputResolver;
import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.io.stream.FastByteArrayOutputStream;
import net.sf.jstuff.core.validation.Args;

/**
 * Interesting reading: Unofficial JAXB Guide http://jaxb.java.net/guide/
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class JAXBUtils {
   @SuppressWarnings("resource")
   public static String toXML(final Object obj) throws XMLException {
      Args.notNull("obj", obj);

      final var baos = new FastByteArrayOutputStream();
      toXML(obj, baos);
      return baos.toString();
   }

   @SuppressWarnings({"rawtypes", "resource", "unchecked"})
   public static void toXML(final Object obj, final OutputStream out) throws XMLException {
      Args.notNull("obj", obj);
      Args.notNull("out", out);

      try {
         final JAXBContext ctx = JAXBContext.newInstance(obj.getClass());
         final Marshaller m = ctx.createMarshaller();
         m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
         m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
         if (ctx.createJAXBIntrospector().getElementName(obj) == null) {
            m.marshal(new JAXBElement(new QName(Strings.lowerCaseFirstChar(obj.getClass().getSimpleName())), obj.getClass(), obj), out);
         } else {
            m.marshal(obj, out);
         }
      } catch (final JAXBException ex) {
         throw new XMLException(ex);
      }
   }

   @SuppressWarnings("resource")
   public static String toXSD(final Class<?>... xmlRootClasses) throws XMLException {
      Args.notNull("xmlRootClasses", xmlRootClasses);

      final var out = new FastByteArrayOutputStream();
      try {
         toXSD(out, xmlRootClasses);
      } catch (final IOException ex) {
         // never happens
      }
      return out.toString();
   }

   @SuppressWarnings("resource")
   public static void toXSD(final OutputStream out, final Class<?>... xmlRootClasses) throws XMLException, IOException {
      Args.notNull("xmlRootClasses", xmlRootClasses);
      Args.notNull("out", out);

      try {
         final var result = new StreamResult(out);
         JAXBContext.newInstance(xmlRootClasses).generateSchema(new SchemaOutputResolver() {
            @Override
            public @Nullable Result createOutput(final String namespaceURI, final String suggestedFileName) throws IOException {
               result.setSystemId(new File(suggestedFileName));
               return result;
            }
         });
      } catch (final JAXBException ex) {
         throw new XMLException(ex);
      }
   }
}
