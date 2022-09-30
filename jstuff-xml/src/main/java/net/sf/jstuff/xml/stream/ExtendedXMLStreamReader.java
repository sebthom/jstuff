/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import javax.xml.stream.XMLStreamReader;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExtendedXMLStreamReader extends DelegatingXMLStreamReader {

   public ExtendedXMLStreamReader() {
   }

   public ExtendedXMLStreamReader(final XMLStreamReader wrapped) {
      super(wrapped);
   }

   public String getAttributeValue(final String attrLocalName) {
      for (int i = 0; i < getAttributeCount(); i++) {
         final String localName = getAttributeLocalName(i);
         if (localName.equals(attrLocalName))
            return getAttributeValue(i);
      }
      return null;
   }

   public boolean getAttributeValueAsBoolean(final String attrLocalName, final boolean defaultValue) {
      final String val = getAttributeValue(attrLocalName);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Boolean.parseBoolean(val);
   }

   public byte getAttributeValueAsByte(final String attrLocalName, final byte defaultValue) {
      final String val = getAttributeValue(attrLocalName);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Byte.parseByte(val);
   }

   public int getAttributeValueAsInt(final String attrLocalName, final int defaultValue) {
      final String val = getAttributeValue(attrLocalName);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Integer.parseInt(val);
   }

   public long getAttributeValueAsLong(final String attrLocalName, final long defaultValue) {
      final String val = getAttributeValue(attrLocalName);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Long.parseLong(val);
   }

   public String getAttributeValueOrElse(final String attrLocalName, final String defaultValue) {
      for (int i = 0; i < getAttributeCount(); i++) {
         final String localName = getAttributeLocalName(i);
         if (localName.equals(attrLocalName))
            return getAttributeValue(i);
      }
      return defaultValue;
   }
}
