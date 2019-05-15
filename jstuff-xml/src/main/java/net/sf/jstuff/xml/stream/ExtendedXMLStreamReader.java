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

import javax.xml.stream.XMLStreamReader;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExtendedXMLStreamReader extends DelegatingXMLStreamReader {

   public ExtendedXMLStreamReader() {
   }

   public ExtendedXMLStreamReader(final XMLStreamReader wrapped) {
      super(wrapped);
   }

   public String getAttributeValue(final String attrLocalName) {
      return getAttributeValueAsString(attrLocalName, null);
   }

   public boolean getAttributeValueAsBoolean(final String attrLocalName, final boolean defaultValue) {
      final String val = getAttributeValueAsString(attrLocalName, null);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Boolean.parseBoolean(val);
   }

   public byte getAttributeValueAsByte(final String attrLocalName, final byte defaultValue) {
      final String val = getAttributeValueAsString(attrLocalName, null);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Byte.parseByte(val);
   }

   public int getAttributeValueAsInt(final String attrLocalName, final int defaultValue) {
      final String val = getAttributeValueAsString(attrLocalName, null);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Integer.parseInt(val);
   }

   public long getAttributeValueAsLong(final String attrLocalName, final long defaultValue) {
      final String val = getAttributeValueAsString(attrLocalName, null);
      if (val == null || Strings.isBlank(val))
         return defaultValue;
      return Long.parseLong(val);
   }

   public String getAttributeValueAsString(final String attrLocalName, final String defaultValue) {
      for (int i = 0; i < getAttributeCount(); i++) {
         final String localName = getAttributeLocalName(i);
         if (localName.equals(attrLocalName))
            return getAttributeValue(i);
      }
      return defaultValue;
   }

}