/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml.stream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * Thread-safe XMLInputFactory/XMLOutputFactory factory.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StAXFactory {

   private final ThreadLocal<XMLOutputFactory> xmlOutputFactory = new ThreadLocal<XMLOutputFactory>() {
      @Override
      protected XMLOutputFactory initialValue() {
         return createXMLOutputFactory();
      }
   };

   private final ThreadLocal<XMLInputFactory> xmlInputFactory = new ThreadLocal<XMLInputFactory>() {
      @Override
      protected XMLInputFactory initialValue() {
         return createXMLInputFactory();
      }
   };

   @SuppressWarnings("resource")
   public AutoCloseableXMLEventReader createXMLEventReader(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileReadable("xmlFile", xmlFile);

      final InputStream is = new BufferedInputStream(new FileInputStream(xmlFile));
      final XMLEventReader reader = xmlInputFactory.get().createXMLEventReader(is);
      return new DelegatingXMLEventReader(reader) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(is);
         }
      };
   }

   /**
    * @param autoClose if true xmlInput.close() is invoked when XMLStreamReader.close() is called
    */
   @SuppressWarnings("resource")
   public AutoCloseableXMLEventReader createXMLEventReader(final InputStream xmlInput, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlInput", xmlInput);

      final InputStream is = xmlInput instanceof BufferedInputStream ? xmlInput : new BufferedInputStream(xmlInput);
      final XMLEventReader reader = xmlInputFactory.get().createXMLEventReader(is);
      if (autoClose)
         return new DelegatingXMLEventReader(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(is);
            }
         };
      return new DelegatingXMLEventReader(reader);
   }

   /**
    * @param autoClose if true xmlReader.close() is invoked when XMLStreamReader.close() is called
    */
   public AutoCloseableXMLEventReader createXMLEventReader(final Reader xmlReader, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlReader", xmlReader);

      final XMLEventReader reader = xmlInputFactory.get().createXMLEventReader(xmlReader);
      if (autoClose)
         return new DelegatingXMLEventReader(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(xmlReader);
            }
         };
      return new DelegatingXMLEventReader(reader);
   }

   public AutoCloseableXMLEventReader createXMLEventReader(final Source xmlSource) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLEventReader reader = xmlInputFactory.get().createXMLEventReader(xmlSource);
      return new DelegatingXMLEventReader(reader);
   }

   /**
    * @param autoClose if true xmlSourcegetReader()/getInputStream().close() is invoked when XMLStreamReader.close() is called
    */
   public AutoCloseableXMLEventReader createXMLEventReader(final StreamSource xmlSource, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLEventReader reader = xmlInputFactory.get().createXMLEventReader(xmlSource);
      if (autoClose)
         return new DelegatingXMLEventReader(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(xmlSource.getReader());
               IOUtils.closeQuietly(xmlSource.getInputStream());
            }
         };
      return new DelegatingXMLEventReader(reader);
   }

   @SuppressWarnings("resource")
   public AutoCloseableXMLEventWriter createXMLEventWriter(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileWriteable("xmlFile", xmlFile);

      final OutputStream is = new BufferedOutputStream(new FileOutputStream(xmlFile));
      final XMLEventWriter writer = xmlOutputFactory.get().createXMLEventWriter(is);
      return new DelegatingXMLEventWriter(writer) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(is);
         }
      };
   }

   /**
    * @param autoClose if true os.close() is invoked when XMLEventWriter.close() is called
    */
   @SuppressWarnings("resource")
   public AutoCloseableXMLEventWriter createXMLEventWriter(final OutputStream xmlOutput, final Charset encoding, final boolean autoClose)
      throws XMLStreamException {
      Args.notNull("xmlOutput", xmlOutput);
      Args.notNull("encoding", encoding);

      final OutputStream os = xmlOutput instanceof BufferedOutputStream ? xmlOutput : new BufferedOutputStream(xmlOutput);
      final XMLEventWriter reader = xmlOutputFactory.get().createXMLEventWriter(os, encoding.name());
      if (autoClose)
         return new DelegatingXMLEventWriter(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();

               IOUtils.closeQuietly(os);
            }
         };
      return new DelegatingXMLEventWriter(reader);
   }

   public AutoCloseableXMLEventWriter createXMLEventWriter(final Result xmlResult) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLEventWriter writer = xmlOutputFactory.get().createXMLEventWriter(xmlResult);
      return new DelegatingXMLEventWriter(writer);
   }

   /**
    * @param autoClose if true xmlResult.[getWriter()/getOutputStream()].close() is invoked when ExtendedXMLEventWriter.close() is called
    */
   public AutoCloseableXMLEventWriter createXMLEventWriter(final StreamResult xmlResult, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLEventWriter writer = xmlOutputFactory.get().createXMLEventWriter(xmlResult);
      if (autoClose)
         return new DelegatingXMLEventWriter(writer) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(xmlResult.getWriter());
               IOUtils.closeQuietly(xmlResult.getOutputStream());
            }
         };
      return new DelegatingXMLEventWriter(writer);
   }

   /**
    * @param autoClose if true xmlWriter.close() is invoked when XMLEventWriter.close() is called
    */
   public AutoCloseableXMLEventWriter createXMLEventWriter(final Writer xmlWriter, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlWriter", xmlWriter);

      final XMLEventWriter reader = xmlOutputFactory.get().createXMLEventWriter(xmlWriter);
      if (autoClose)
         return new DelegatingXMLEventWriter(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();

               IOUtils.closeQuietly(xmlWriter);
            }
         };
      return new DelegatingXMLEventWriter(reader);
   }

   protected XMLInputFactory createXMLInputFactory() {
      return XMLInputFactory.newInstance();
   }

   protected XMLOutputFactory createXMLOutputFactory() {
      return XMLOutputFactory.newInstance();
   }

   @SuppressWarnings("resource")
   public ExtendedXMLStreamReader createXMLStreamReader(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileReadable("xmlFile", xmlFile);

      final InputStream is = new BufferedInputStream(new FileInputStream(xmlFile));
      final XMLStreamReader reader = xmlInputFactory.get().createXMLStreamReader(is);
      return new ExtendedXMLStreamReader(reader) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(is);
         }
      };
   }

   /**
    * @param autoClose if true xmlInput.close() is invoked when XMLStreamReader.close() is called
    */
   @SuppressWarnings("resource")
   public ExtendedXMLStreamReader createXMLStreamReader(final InputStream xmlInput, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlInput", xmlInput);

      final InputStream is = xmlInput instanceof BufferedInputStream ? xmlInput : new BufferedInputStream(xmlInput);
      final XMLStreamReader reader = xmlInputFactory.get().createXMLStreamReader(is);
      if (autoClose)
         return new ExtendedXMLStreamReader(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();

               IOUtils.closeQuietly(is);
            }
         };
      return new ExtendedXMLStreamReader(reader);
   }

   /**
    * @param autoClose if true xmlReader.close() is invoked when XMLStreamReader.close() is called
    */
   public ExtendedXMLStreamReader createXMLStreamReader(final Reader xmlReader, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlReader", xmlReader);

      final XMLStreamReader reader = xmlInputFactory.get().createXMLStreamReader(xmlReader);
      if (autoClose)
         return new ExtendedXMLStreamReader(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(xmlReader);
            }
         };
      return new ExtendedXMLStreamReader(reader);
   }

   public ExtendedXMLStreamReader createXMLStreamReader(final Source xmlSource) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLStreamReader reader = xmlInputFactory.get().createXMLStreamReader(xmlSource);
      return new ExtendedXMLStreamReader(reader);
   }

   /**
    * @param autoClose if true xmlSource.[getReader()/getInputStream()].close() is invoked when ExtendedXMLStreamReader.close() is called
    */
   public ExtendedXMLStreamReader createXMLStreamReader(final StreamSource xmlSource, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLStreamReader reader = xmlInputFactory.get().createXMLStreamReader(xmlSource);
      if (autoClose)
         return new ExtendedXMLStreamReader(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(xmlSource.getReader());
               IOUtils.closeQuietly(xmlSource.getInputStream());
            }
         };
      return new ExtendedXMLStreamReader(reader);
   }

   @SuppressWarnings("resource")
   public ExtendedXMLStreamWriter createXMLStreamWriter(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileWriteable("xmlFile", xmlFile);

      final OutputStream is = new BufferedOutputStream(new FileOutputStream(xmlFile));
      final XMLStreamWriter writer = xmlOutputFactory.get().createXMLStreamWriter(is);
      return new ExtendedXMLStreamWriter(writer) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(is);
         }
      };
   }

   /**
    * @param autoClose if true os.close() is invoked when XMLStreamWriter.close() is called
    */
   @SuppressWarnings("resource")
   public ExtendedXMLStreamWriter createXMLStreamWriter(final OutputStream xmlOutput, final Charset encoding, final boolean autoClose)
      throws XMLStreamException {
      Args.notNull("xmlOutput", xmlOutput);
      Args.notNull("encoding", encoding);

      final OutputStream os = xmlOutput instanceof BufferedOutputStream ? xmlOutput : new BufferedOutputStream(xmlOutput);
      final XMLStreamWriter reader = xmlOutputFactory.get().createXMLStreamWriter(os, encoding.name());
      if (autoClose)
         return new ExtendedXMLStreamWriter(reader) {
            @Override
            public void close() throws XMLStreamException {
               super.close();

               IOUtils.closeQuietly(os);
            }
         };
      return new ExtendedXMLStreamWriter(reader);
   }

   public ExtendedXMLStreamWriter createXMLStreamWriter(final Result xmlResult) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLStreamWriter writer = xmlOutputFactory.get().createXMLStreamWriter(xmlResult);
      return new ExtendedXMLStreamWriter(writer);
   }

   /**
    * @param autoClose if true xmlResult.[getWriter()/getOutputStream()].close() is invoked when ExtendedXMLStreamWriter.close() is called
    */
   public ExtendedXMLStreamWriter createXMLStreamWriter(final StreamResult xmlResult, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLStreamWriter writer = xmlOutputFactory.get().createXMLStreamWriter(xmlResult);
      if (autoClose)
         return new ExtendedXMLStreamWriter(writer) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(xmlResult.getWriter());
               IOUtils.closeQuietly(xmlResult.getOutputStream());
            }
         };
      return new ExtendedXMLStreamWriter(writer);
   }

   /**
    * @param autoClose if true xmlWriter.close() is invoked when XMLStreamWriter.close() is called
    */
   public ExtendedXMLStreamWriter createXMLStreamWriter(final Writer xmlWriter, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlWriter", xmlWriter);

      final XMLStreamWriter writer = xmlOutputFactory.get().createXMLStreamWriter(xmlWriter);
      if (autoClose)
         return new ExtendedXMLStreamWriter(writer) {
            @Override
            public void close() throws XMLStreamException {
               super.close();
               IOUtils.closeQuietly(xmlWriter);
            }
         };
      return new ExtendedXMLStreamWriter(writer);
   }
}
