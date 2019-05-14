/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

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

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.IntArrayList;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StAXUtils {

   public static final class ElementInfo {
      public final String localName;

      public final String nsPrefix;
      public final String nsURI;
      private String text;
      public final TreeMap<String, String> attrs;

      private ElementInfo(//
         final String localName, //
         final String nsPrefix, //
         final String nsURI, //
         final TreeMap<String, String> attrs //
      ) {
         this.localName = localName;
         this.nsPrefix = nsPrefix;
         this.nsURI = nsURI;
         this.attrs = attrs;
      }

      public String getText() {
         return text;
      }

      @Override
      public String toString() {
         return (nsPrefix == null || nsPrefix.length() == 0 ? "" : nsPrefix + ":") + localName;
      }
   }

   private static final Logger LOG = Logger.create();

   private static final ThreadLocal<XMLOutputFactory> XML_OUTPUT_FACTORY = new ThreadLocal<XMLOutputFactory>() {
      @Override
      protected XMLOutputFactory initialValue() {
         return XMLOutputFactory.newInstance();
      }
   };

   private static final ThreadLocal<XMLInputFactory> XML_INPUT_FACTORY = new ThreadLocal<XMLInputFactory>() {
      @Override
      protected XMLInputFactory initialValue() {
         return XMLInputFactory.newInstance();
      }
   };

   @SuppressWarnings("resource")
   public static AutoCloseableXMLEventReader createXMLEventReader(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileReadable("xmlFile", xmlFile);

      final InputStream is = new BufferedInputStream(new FileInputStream(xmlFile));
      final XMLEventReader reader = XML_INPUT_FACTORY.get().createXMLEventReader(is);
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
   public static AutoCloseableXMLEventReader createXMLEventReader(final InputStream xmlInput, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlInput", xmlInput);

      final InputStream is = xmlInput instanceof BufferedInputStream ? xmlInput : new BufferedInputStream(xmlInput);
      final XMLEventReader reader = XML_INPUT_FACTORY.get().createXMLEventReader(is);
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
   public static AutoCloseableXMLEventReader createXMLEventReader(final Reader xmlReader, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlReader", xmlReader);

      final XMLEventReader reader = XML_INPUT_FACTORY.get().createXMLEventReader(xmlReader);
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

   public static AutoCloseableXMLEventReader createXMLEventReader(final Source xmlSource) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLEventReader reader = XML_INPUT_FACTORY.get().createXMLEventReader(xmlSource);
      return new DelegatingXMLEventReader(reader);
   }

   /**
    * @param autoClose if true xmlSourcegetReader()/getInputStream().close() is invoked when XMLStreamReader.close() is called
    */
   public static AutoCloseableXMLEventReader createXMLEventReader(final StreamSource xmlSource, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLEventReader reader = XML_INPUT_FACTORY.get().createXMLEventReader(xmlSource);
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
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileWriteable("xmlFile", xmlFile);

      final OutputStream is = new BufferedOutputStream(new FileOutputStream(xmlFile));
      final XMLEventWriter writer = XML_OUTPUT_FACTORY.get().createXMLEventWriter(is);
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
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final OutputStream xmlOutput, final Charset encoding, final boolean autoClose)
      throws XMLStreamException {
      Args.notNull("xmlOutput", xmlOutput);
      Args.notNull("encoding", encoding);

      final OutputStream os = xmlOutput instanceof BufferedOutputStream ? xmlOutput : new BufferedOutputStream(xmlOutput);
      final XMLEventWriter reader = XML_OUTPUT_FACTORY.get().createXMLEventWriter(os, encoding.name());
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

   public static AutoCloseableXMLEventWriter createXMLEventWriter(final Result xmlResult) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLEventWriter writer = XML_OUTPUT_FACTORY.get().createXMLEventWriter(xmlResult);
      return new DelegatingXMLEventWriter(writer);
   }

   /**
    * @param autoClose if true xmlResult.[getWriter()/getOutputStream()].close() is invoked when ExtendedXMLEventWriter.close() is called
    */
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final StreamResult xmlResult, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLEventWriter writer = XML_OUTPUT_FACTORY.get().createXMLEventWriter(xmlResult);
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
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final Writer xmlWriter, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlWriter", xmlWriter);

      final XMLEventWriter reader = XML_OUTPUT_FACTORY.get().createXMLEventWriter(xmlWriter);
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

   @SuppressWarnings("resource")
   public static ExtendedXMLStreamReader createXMLStreamReader(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileReadable("xmlFile", xmlFile);

      final InputStream is = new BufferedInputStream(new FileInputStream(xmlFile));
      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(is);
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
   public static ExtendedXMLStreamReader createXMLStreamReader(final InputStream xmlInput, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlInput", xmlInput);

      final InputStream is = xmlInput instanceof BufferedInputStream ? xmlInput : new BufferedInputStream(xmlInput);
      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(is);
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
   public static ExtendedXMLStreamReader createXMLStreamReader(final Reader xmlReader, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlReader", xmlReader);

      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(xmlReader);
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

   public static ExtendedXMLStreamReader createXMLStreamReader(final Source xmlSource) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(xmlSource);
      return new ExtendedXMLStreamReader(reader);
   }

   /**
    * @param autoClose if true xmlSource.[getReader()/getInputStream()].close() is invoked when ExtendedXMLStreamReader.close() is called
    */
   public static ExtendedXMLStreamReader createXMLStreamReader(final StreamSource xmlSource, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(xmlSource);
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
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileWriteable("xmlFile", xmlFile);

      final OutputStream is = new BufferedOutputStream(new FileOutputStream(xmlFile));
      final XMLStreamWriter writer = XML_OUTPUT_FACTORY.get().createXMLStreamWriter(is);
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
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final OutputStream xmlOutput, final Charset encoding, final boolean autoClose)
      throws XMLStreamException {
      Args.notNull("xmlOutput", xmlOutput);
      Args.notNull("encoding", encoding);

      final OutputStream os = xmlOutput instanceof BufferedOutputStream ? xmlOutput : new BufferedOutputStream(xmlOutput);
      final XMLStreamWriter reader = XML_OUTPUT_FACTORY.get().createXMLStreamWriter(os, encoding.name());
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

   public static ExtendedXMLStreamWriter createXMLStreamWriter(final Result xmlResult) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLStreamWriter writer = XML_OUTPUT_FACTORY.get().createXMLStreamWriter(xmlResult);
      return new ExtendedXMLStreamWriter(writer);
   }

   /**
    * @param autoClose if true xmlResult.[getWriter()/getOutputStream()].close() is invoked when ExtendedXMLStreamWriter.close() is called
    */
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final StreamResult xmlResult, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlResult", xmlResult);

      final XMLStreamWriter writer = XML_OUTPUT_FACTORY.get().createXMLStreamWriter(xmlResult);
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
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final Writer xmlWriter, final boolean autoClose) throws XMLStreamException {
      Args.notNull("xmlWriter", xmlWriter);

      final XMLStreamWriter writer = XML_OUTPUT_FACTORY.get().createXMLStreamWriter(xmlWriter);
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

   public static ElementInfo findElement(final XMLStreamReader reader, final String xpath) throws XMLStreamException {
      final List<ElementInfo> elems = findElements(reader, xpath, 1);
      return elems.size() == 0 ? null : elems.get(0);
   }

   public static List<ElementInfo> findElements(final XMLStreamReader reader, final String xpath) throws XMLStreamException {
      return findElements(reader, xpath, Integer.MAX_VALUE);
   }

   public static List<ElementInfo> findElements(final XMLStreamReader reader, final String xpath, final int max) throws XMLStreamException {
      final StringBuilder path = new StringBuilder();

      final IntArrayList pathElemSize = new IntArrayList();
      final StringBuilder pathElem = new StringBuilder();
      final boolean evaluteAttributes = Strings.contains(xpath, '[');
      TreeMap<String, String> attrs = null;
      final Pattern xpathPattern = xpathToPattern(xpath);

      final List<ElementInfo> result = new ArrayList<>(max < 10 ? max : 10);
      ElementInfo elem = null;
      while (reader.hasNext()) {
         reader.next();
         switch (reader.getEventType()) {
            case XMLStreamReader.START_ELEMENT: {
               pathElem.setLength(0);
               pathElem.append('/');
               pathElem.append(reader.getLocalName());
               if (evaluteAttributes) {
                  attrs = readAttributes(reader, attrs);
                  if (attrs.size() > 0) {
                     pathElem.append('[');
                     boolean isFirstAttr = true;
                     for (final Map.Entry<String, String> entry : attrs.entrySet()) {
                        if (isFirstAttr) {
                           isFirstAttr = false;
                        } else {
                           pathElem.append(',');
                        }
                        pathElem.append('@').append(entry.getKey()).append("='").append(entry.getValue()).append('\'');
                     }
                     pathElem.append(']');
                  }
               }

               pathElemSize.add(pathElem.length());
               path.append(pathElem);

               final boolean isMatch = xpathPattern.matcher(path).matches();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("\n--------------------------------\n" + //
                     "    PATH: " + path + "\n" + //
                     "   XPATH: " + xpath + "\n" + //
                     "   REGEX: " + xpathPattern + "\n" + //
                     "IS_MATCH: " + isMatch + "\n" + //
                     "--------------------------------");
               }
               if (isMatch) {
                  elem = new ElementInfo( //
                     reader.getLocalName(), //
                     reader.getPrefix(), //
                     reader.getNamespaceURI(), //
                     evaluteAttributes ? attrs : readAttributes(reader, attrs) //
                  );
                  attrs = null;
               }
               break;
            }

            case XMLStreamReader.CHARACTERS: {
               if (elem != null) {
                  elem.text = elem.text == null ? reader.getText() : elem.text + reader.getText();
               }
               break;
            }

            case XMLStreamReader.END_ELEMENT: {
               if (elem != null) {
                  result.add(elem);
                  elem = null;
               }

               if (result.size() == max)
                  return result;

               if (pathElemSize.size() > 0) {
                  final int size = pathElemSize.removeLast(); //CHECKSTYLE:IGNORE MoveVariableInsideIfCheck
                  if (path.length() > 0) {
                     path.setLength(path.length() - size);
                  }
               }
               break;
            }
         }
      }
      return result;
   }

   public static String getAttributeValue(final XMLStreamReader reader, final String attrLocalName) {
      return getAttributeValue(reader, attrLocalName, null);
   }

   public static String getAttributeValue(final XMLStreamReader reader, final String attrLocalName, final String defaultValue) {
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         final String localName = reader.getAttributeLocalName(i);
         if (localName.equals(attrLocalName))
            return reader.getAttributeValue(i);
      }
      return defaultValue;
   }

   public static boolean getAttributeValueAsBoolean(final XMLStreamReader reader, final String attrLocalName, final boolean defaultValue) {
      final String val = getAttributeValue(reader, attrLocalName);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Boolean.parseBoolean(val);
   }

   private static TreeMap<String, String> readAttributes(final XMLStreamReader reader, TreeMap<String, String> reusableMap) {
      if (reusableMap == null) {
         reusableMap = new TreeMap<>();
      } else {
         reusableMap.clear();
      }
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         reusableMap.put(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
      }
      return reusableMap;
   }

   private static Pattern xpathToPattern(String xpath) {
      final boolean evaluteAttributes = Strings.contains(xpath, '[');

      final StringBuilder sb = new StringBuilder("^");

      xpath = xpath.trim();
      xpath = Strings.replace(xpath, "//", ".*/");
      final String[] elems = Strings.split(xpath, '/');
      for (int i = 0; i < elems.length; i++) {
         final String elem = elems[i];
         if (i == 0) {
            if (elem.length() == 0) {
               sb.append('^');
            } else {
               sb.append(".*");
            }
         }
         sb.append("\\/");
         if (evaluteAttributes) {
            if (Strings.contains(elem, '[')) {
               sb.append(Strings.substringBefore(elem, "["));
               sb.append("\\[.*");
               final String[] attrValues = Strings.splitByWholeSeparator(Strings.substringBetween(elem, "[", "]"), " and ");
               Arrays.sort(attrValues);
               for (final String attrValue : attrValues) {
                  sb.append(Strings.replace(attrValue.trim(), "\"", "'")).append(".*");
               }
               sb.append("\\]");

            } else {
               sb.append(elem).append("(\\[.*\\])?");
            }
         } else {
            sb.append(elem);
         }
      }

      sb.append('$');
      return Pattern.compile(sb.toString());
   }
}
