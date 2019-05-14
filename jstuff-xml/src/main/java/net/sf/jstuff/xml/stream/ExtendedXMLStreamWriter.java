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

import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExtendedXMLStreamWriter extends DelegatingXMLStreamWriter {

   public ExtendedXMLStreamWriter() {
   }

   public ExtendedXMLStreamWriter(final XMLStreamWriter wrapped) {
      super(wrapped);
   }

   public void writeAttribute(final String localName, final boolean value) throws XMLStreamException {
      wrapped.writeAttribute(localName, Boolean.toString(value));
   }

   public void writeAttribute(final String localName, final byte value) throws XMLStreamException {
      wrapped.writeAttribute(localName, Byte.toString(value));
   }

   public void writeAttribute(final String localName, final int value) throws XMLStreamException {
      wrapped.writeAttribute(localName, Integer.toString(value));
   }

   public void writeAttribute(final String localName, final long value) throws XMLStreamException {
      wrapped.writeAttribute(localName, Long.toString(value));
   }

   public void writeAttribute(final String localName, final Object value) throws XMLStreamException {
      wrapped.writeAttribute(localName, Objects.toString(value));
   }
}
