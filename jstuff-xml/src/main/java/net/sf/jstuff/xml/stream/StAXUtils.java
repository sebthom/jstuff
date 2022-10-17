/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

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

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.primitive.IntArrayList;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StAXUtils {

   public static final class ElementInfo {
      public final String localName;

      public final @Nullable String nsPrefix;
      public final @Nullable String nsURI;
      public final @Nullable Location location;
      private @Nullable String text;
      public final SortedMap<String, String> attrs;

      private ElementInfo( //
         final String localName, //
         final @Nullable String nsPrefix, //
         final @Nullable String nsURI, //
         final @Nullable Location location, //
         final SortedMap<String, String> attrs //
      ) {
         this.localName = localName;
         this.nsPrefix = nsPrefix;
         this.nsURI = nsURI;
         this.location = location;
         this.attrs = attrs;
      }

      public @Nullable String getText() {
         return text;
      }

      @Override
      public String toString() {
         final var nsPrefix = this.nsPrefix;
         return (nsPrefix == null || nsPrefix.length() == 0 ? "" : nsPrefix + ":") + localName;
      }
   }

   private static final Logger LOG = Logger.create();

   public static @Nullable ElementInfo findElement(final XMLStreamReader reader, final String xpath) throws XMLStreamException {
      final List<ElementInfo> elems = findElements(reader, xpath, 1);
      return elems.isEmpty() ? null : elems.get(0);
   }

   public static List<ElementInfo> findElements(final XMLStreamReader reader, final String xpath) throws XMLStreamException {
      return findElements(reader, xpath, Integer.MAX_VALUE);
   }

   public static List<ElementInfo> findElements(final XMLStreamReader reader, final String xpath, final int max) throws XMLStreamException {
      final var path = new StringBuilder();

      final var pathElemSize = new IntArrayList();
      final var pathElem = new StringBuilder();
      final boolean evaluteAttributes = Strings.contains(xpath, '[');
      SortedMap<String, String> attrs = null;
      final Pattern xpathPattern = xpathToPattern(xpath);

      final var result = new ArrayList<ElementInfo>(max < 10 ? max : 10);
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
                  if (attrs == null) {
                     attrs = readAttributes(reader, attrs);
                  }
                  elem = new ElementInfo( //
                     reader.getLocalName(), //
                     reader.getPrefix(), //
                     reader.getNamespaceURI(), //
                     reader.getLocation(), //
                     attrs //
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

   public static @Nullable String getAttributeValue(final XMLStreamReader reader, final String attrLocalName) {
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         final String localName = reader.getAttributeLocalName(i);
         if (localName.equals(attrLocalName))
            return reader.getAttributeValue(i);
      }
      return null;
   }

   public static String getAttributeValue(final XMLStreamReader reader, final String attrLocalName, final String defaultValue) {
      final var val = getAttributeValue(reader, attrLocalName);
      return val == null ? defaultValue : val;
   }

   public static boolean getAttributeValueAsBoolean(final XMLStreamReader reader, final String attrLocalName, final boolean defaultValue) {
      final String val = getAttributeValue(reader, attrLocalName);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Boolean.parseBoolean(val);
   }

   private static SortedMap<String, String> readAttributes(final XMLStreamReader reader, @Nullable SortedMap<String, String> reusableMap) {
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

      final var sb = new StringBuilder("^");

      xpath = xpath.trim();
      xpath = Strings.replace(xpath, "//", ".*/");
      final var elems = Strings.split(xpath, '/');
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
