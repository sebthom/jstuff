/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExtendedXMLStreamWriter extends DelegatingXMLStreamWriter {

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
