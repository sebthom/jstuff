/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.primitive.IntArrayList;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StAXUtils {

   public static final class ElementInfo {
      public final String localName;

      public final String nsPrefix;
      public final String nsURI;
      public final Location location;
      private String text;
      public final SortedMap<String, String> attrs;

      private ElementInfo( //
         final String localName, //
         final String nsPrefix, //
         final String nsURI, //
         final Location location, //
         final SortedMap<String, String> attrs //
      ) {
         this.localName = localName;
         this.nsPrefix = nsPrefix;
         this.nsURI = nsURI;
         this.location = location;
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

   private static final StAXFactory DEFAULT_STAX_FACTORY = new StAXFactory();

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventReader createXMLEventReader(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventReader(xmlFile);
   }

   /**
    * @param autoClose if true xmlInput.close() is invoked when XMLStreamReader.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventReader createXMLEventReader(final InputStream xmlInput, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventReader(xmlInput, autoClose);
   }

   /**
    * @param autoClose if true xmlReader.close() is invoked when XMLStreamReader.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventReader createXMLEventReader(final Reader xmlReader, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventReader(xmlReader, autoClose);
   }

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventReader createXMLEventReader(final Source xmlSource) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventReader(xmlSource);
   }

   /**
    * @param autoClose if true xmlSourcegetReader()/getInputStream().close() is invoked when XMLStreamReader.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventReader createXMLEventReader(final StreamSource xmlSource, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventReader(xmlSource, autoClose);
   }

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventWriter(xmlFile);
   }

   /**
    * @param autoClose if true os.close() is invoked when XMLEventWriter.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final OutputStream xmlOutput, final Charset encoding, final boolean autoClose)
      throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventWriter(xmlOutput, encoding, autoClose);
   }

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final Result xmlResult) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventWriter(xmlResult);
   }

   /**
    * @param autoClose if true xmlResult.[getWriter()/getOutputStream()].close() is invoked when ExtendedXMLEventWriter.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final StreamResult xmlResult, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventWriter(xmlResult, autoClose);
   }

   /**
    * @param autoClose if true xmlWriter.close() is invoked when XMLEventWriter.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static AutoCloseableXMLEventWriter createXMLEventWriter(final Writer xmlWriter, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLEventWriter(xmlWriter, autoClose);
   }

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamReader createXMLStreamReader(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamReader(xmlFile);
   }

   /**
    * @param autoClose if true xmlInput.close() is invoked when XMLStreamReader.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamReader createXMLStreamReader(final InputStream xmlInput, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamReader(xmlInput, autoClose);
   }

   /**
    * @param autoClose if true xmlReader.close() is invoked when XMLStreamReader.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamReader createXMLStreamReader(final Reader xmlReader, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamReader(xmlReader, autoClose);
   }

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamReader createXMLStreamReader(final Source xmlSource) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamReader(xmlSource);
   }

   /**
    * @param autoClose if true xmlSource.[getReader()/getInputStream()].close() is invoked when ExtendedXMLStreamReader.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamReader createXMLStreamReader(final StreamSource xmlSource, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamReader(xmlSource, autoClose);
   }

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final File xmlFile) throws FileNotFoundException, XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamWriter(xmlFile);
   }

   /**
    * @param autoClose if true os.close() is invoked when XMLStreamWriter.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final OutputStream xmlOutput, final Charset encoding, final boolean autoClose)
      throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamWriter(xmlOutput, encoding, autoClose);
   }

   /**
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final Result xmlResult) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamWriter(xmlResult);
   }

   /**
    * @param autoClose if true xmlResult.[getWriter()/getOutputStream()].close() is invoked when ExtendedXMLStreamWriter.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final StreamResult xmlResult, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamWriter(xmlResult, autoClose);
   }

   /**
    * @param autoClose if true xmlWriter.close() is invoked when XMLStreamWriter.close() is called
    * @deprecated use {@link StAXFactory}
    */
   @Deprecated
   public static ExtendedXMLStreamWriter createXMLStreamWriter(final Writer xmlWriter, final boolean autoClose) throws XMLStreamException {
      return DEFAULT_STAX_FACTORY.createXMLStreamWriter(xmlWriter, autoClose);
   }

   public static ElementInfo findElement(final XMLStreamReader reader, final String xpath) throws XMLStreamException {
      final List<ElementInfo> elems = findElements(reader, xpath, 1);
      return elems.isEmpty() ? null : elems.get(0);
   }

   public static List<ElementInfo> findElements(final XMLStreamReader reader, final String xpath) throws XMLStreamException {
      return findElements(reader, xpath, Integer.MAX_VALUE);
   }

   public static List<ElementInfo> findElements(final XMLStreamReader reader, final String xpath, final int max) throws XMLStreamException {
      final StringBuilder path = new StringBuilder();

      final IntArrayList pathElemSize = new IntArrayList();
      final StringBuilder pathElem = new StringBuilder();
      final boolean evaluteAttributes = Strings.contains(xpath, '[');
      SortedMap<String, String> attrs = null;
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
                  LOG.debug("\n--------------------------------\n" //
                     + "    PATH: " + path + "\n" //
                     + "   XPATH: " + xpath + "\n" //
                     + "   REGEX: " + xpathPattern + "\n" //
                     + "IS_MATCH: " + isMatch + "\n" //
                     + "--------------------------------");
               }
               if (isMatch) {
                  elem = new ElementInfo( //
                     reader.getLocalName(), //
                     reader.getPrefix(), //
                     reader.getNamespaceURI(), //
                     reader.getLocation(), //
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

               if (!pathElemSize.isEmpty()) {
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

   private static SortedMap<String, String> readAttributes(final XMLStreamReader reader, SortedMap<String, String> reusableMap) {
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
