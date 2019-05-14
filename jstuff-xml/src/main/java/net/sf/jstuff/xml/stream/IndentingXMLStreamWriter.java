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

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.jstuff.core.Strings;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IndentingXMLStreamWriter extends ExtendedXMLStreamWriter {

   private String indention = "  ";
   private int indentionLevel = 0;
   private boolean indentEndTag = true;

   public IndentingXMLStreamWriter() {
   }

   public IndentingXMLStreamWriter(final XMLStreamWriter wrapped) {
      super(wrapped);
   }

   public void setIndention(final String indention) {
      this.indention = indention;
   }

   @Override
   public void writeCData(final String data) throws XMLStreamException {
      writeIndention();
      super.writeCData(data);
   }

   @Override
   public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
      indentEndTag = false;
      super.writeCharacters(text, start, len);
   }

   @Override
   public void writeCharacters(final String text) throws XMLStreamException {
      indentEndTag = false;
      super.writeCharacters(text);
   }

   @Override
   public void writeComment(final String data) throws XMLStreamException {
      writeIndention();
      super.writeComment(data);
   }

   @Override
   public void writeEmptyElement(final String localName) throws XMLStreamException {
      writeIndention();
      super.writeEmptyElement(localName);
   }

   @Override
   public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
      writeIndention();
      super.writeEmptyElement(namespaceURI, localName);
   }

   @Override
   public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
      writeIndention();
      super.writeEmptyElement(prefix, localName, namespaceURI);
   }

   @Override
   public void writeEndElement() throws XMLStreamException {
      indentionLevel--;
      if (indentEndTag) {
         writeIndention();
      } else {
         indentEndTag = true;
      }
      super.writeEndElement();
   }

   private void writeIndention() throws XMLStreamException {
      wrapped.writeCharacters(Strings.NEW_LINE);
      for (int i = 0; i < indentionLevel; i++) {
         wrapped.writeCharacters(indention);
      }
   }

   @Override
   public void writeStartElement(final String localName) throws XMLStreamException {
      writeIndention();
      indentionLevel++;
      super.writeStartElement(localName);
   }

   @Override
   public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
      writeIndention();
      indentionLevel++;
      super.writeStartElement(namespaceURI, localName);
   }

   @Override
   public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
      writeIndention();
      indentionLevel++;
      super.writeStartElement(prefix, localName, namespaceURI);
   }
}
