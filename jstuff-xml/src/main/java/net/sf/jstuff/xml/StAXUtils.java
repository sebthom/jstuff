/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml;

import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StAXUtils {
   public static String getAttributeValue(final XMLStreamReader reader, final String attrLocalName) {
      for (int i = 0; i < reader.getAttributeCount(); i++) {
         final String localName = reader.getAttributeLocalName(i);
         if (localName.equals(attrLocalName))
            return reader.getAttributeValue(i);
      }
      return null;
   }
}
