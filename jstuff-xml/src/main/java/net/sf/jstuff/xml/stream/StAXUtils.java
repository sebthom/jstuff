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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

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

   private static final ThreadLocal<XMLInputFactory> XML_INPUT_FACTORY = new ThreadLocal<XMLInputFactory>() {
      @Override
      protected XMLInputFactory initialValue() {
         final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
         return xmlInputFactory;
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

   @SuppressWarnings("resource")
   public static AutoCloseableXMLEventReader createXMLEventReader(final InputStream xmlInput) throws XMLStreamException {
      Args.notNull("xmlInput", xmlInput);

      final XMLEventReader reader = XML_INPUT_FACTORY.get().createXMLEventReader(xmlInput instanceof BufferedInputStream ? xmlInput
         : new BufferedInputStream(xmlInput));
      return new DelegatingXMLEventReader(reader) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(xmlInput);
         }
      };
   }

   public static AutoCloseableXMLEventReader createXMLEventReader(final Reader xmlReader) throws XMLStreamException {
      Args.notNull("xmlReader", xmlReader);
      return new DelegatingXMLEventReader(XML_INPUT_FACTORY.get().createXMLEventReader(xmlReader)) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(xmlReader);
         }
      };
   }

   public static AutoCloseableXMLEventReader createXMLEventReader(final Source xmlSource) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);
      return new DelegatingXMLEventReader(XML_INPUT_FACTORY.get().createXMLEventReader(xmlSource)) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
         }
      };
   }

   @SuppressWarnings("resource")
   public static AutoCloseableXMLStreamReader createXMLStreamReader(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      Args.notNull("xmlFile", xmlFile);
      Args.isFileReadable("xmlFile", xmlFile);
      final InputStream is = new BufferedInputStream(new FileInputStream(xmlFile));
      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(is);
      return new DelegatingXMLStreamReader(reader) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(is);
         }
      };
   }

   @SuppressWarnings("resource")
   public static AutoCloseableXMLStreamReader createXMLStreamReader(final InputStream xmlInput) throws XMLStreamException {
      Args.notNull("xmlInput", xmlInput);

      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(xmlInput instanceof BufferedInputStream ? xmlInput
         : new BufferedInputStream(xmlInput));
      return new DelegatingXMLStreamReader(reader) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(xmlInput);
         }
      };
   }

   public static AutoCloseableXMLStreamReader createXMLStreamReader(final Reader xmlReader) throws XMLStreamException {
      Args.notNull("xmlReader", xmlReader);
      return new DelegatingXMLStreamReader(XML_INPUT_FACTORY.get().createXMLStreamReader(xmlReader)) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
            IOUtils.closeQuietly(xmlReader);
         }
      };
   }

   public static AutoCloseableXMLStreamReader createXMLStreamReader(final Source xmlSource) throws XMLStreamException {
      Args.notNull("xmlSource", xmlSource);

      final XMLStreamReader reader = XML_INPUT_FACTORY.get().createXMLStreamReader(xmlSource);
      return new DelegatingXMLStreamReader(reader) {
         @Override
         public void close() throws XMLStreamException {
            super.close();
         }
      };
   }

   public static ElementInfo findElement(final File xmlFile, final String xpath) throws XMLStreamException, FileNotFoundException {
      try (AutoCloseableXMLStreamReader reader = createXMLStreamReader(xmlFile)) {
         return findElement(reader, xpath);
      }
   }

   public static ElementInfo findElement(final InputStream xmlInput, final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = createXMLStreamReader(xmlInput)) {
         return findElement(reader, xpath);
      }
   }

   public static ElementInfo findElement(final Source xmlSource, final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = createXMLStreamReader(xmlSource)) {
         return findElement(reader, xpath);
      }
   }

   private static ElementInfo findElement(final XMLStreamReader reader, final String xpath) throws XMLStreamException {
      final List<ElementInfo> elems = findElements(reader, xpath, 1);
      return elems.size() == 0 ? null : elems.get(0);
   }

   public static List<ElementInfo> findElements(final File xmlFile, final String xpath) throws XMLStreamException, FileNotFoundException {
      try (AutoCloseableXMLStreamReader reader = createXMLStreamReader(xmlFile)) {
         return findElements(reader, xpath, Integer.MAX_VALUE);
      }
   }

   public static List<ElementInfo> findElements(final InputStream xmlInput, final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = createXMLStreamReader(xmlInput)) {
         return findElements(reader, xpath, Integer.MAX_VALUE);
      }
   }

   public static List<ElementInfo> findElements(final Source xmlSource, final String xpath) throws XMLStreamException {
      try (AutoCloseableXMLStreamReader reader = createXMLStreamReader(xmlSource)) {
         return findElements(reader, xpath, Integer.MAX_VALUE);
      }
   }

   private static List<ElementInfo> findElements(final XMLStreamReader reader, final String xpath, final int max) throws XMLStreamException {
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
                        pathElem.append("@").append(entry.getKey()).append("='").append(entry.getValue()).append('\'');
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

      sb.append("$");
      return Pattern.compile(sb.toString());
   }
}