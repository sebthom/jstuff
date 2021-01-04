/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingXMLStreamWriter extends Decorator.Default<XMLStreamWriter> implements AutoCloseableXMLStreamWriter {

   public DelegatingXMLStreamWriter() {
   }

   public DelegatingXMLStreamWriter(final XMLStreamWriter wrapped) {
      super(wrapped);
   }

   @Override
   public void close() throws XMLStreamException {
      wrapped.close();
   }

   @Override
   public void flush() throws XMLStreamException {
      wrapped.flush();
   }

   @Override
   public NamespaceContext getNamespaceContext() {
      return wrapped.getNamespaceContext();
   }

   @Override
   public String getPrefix(final String uri) throws XMLStreamException {
      return wrapped.getPrefix(uri);
   }

   @Override
   public Object getProperty(final String name) throws IllegalArgumentException {
      return wrapped.getProperty(name);
   }

   @Override
   public void setDefaultNamespace(final String uri) throws XMLStreamException {
      wrapped.setDefaultNamespace(uri);
   }

   @Override
   public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
      wrapped.setNamespaceContext(context);
   }

   @Override
   public void setPrefix(final String prefix, final String uri) throws XMLStreamException {
      wrapped.setPrefix(prefix, uri);
   }

   @Override
   public void writeAttribute(final String localName, final String value) throws XMLStreamException {
      wrapped.writeAttribute(localName, value);
   }

   @Override
   public void writeAttribute(final String namespaceURI, final String localName, final String value) throws XMLStreamException {
      wrapped.writeAttribute(namespaceURI, localName, value);
   }

   @Override
   public void writeAttribute(final String prefix, final String namespaceURI, final String localName, final String value) throws XMLStreamException {
      wrapped.writeAttribute(prefix, namespaceURI, localName, value);
   }

   @Override
   public void writeCData(final String data) throws XMLStreamException {
      wrapped.writeCData(data);
   }

   @Override
   public void writeCharacters(final char[] text, final int start, final int len) throws XMLStreamException {
      wrapped.writeCharacters(text, start, len);
   }

   @Override
   public void writeCharacters(final String text) throws XMLStreamException {
      wrapped.writeCharacters(text);
   }

   @Override
   public void writeComment(final String data) throws XMLStreamException {
      wrapped.writeComment(data);
   }

   @Override
   public void writeDefaultNamespace(final String namespaceURI) throws XMLStreamException {
      wrapped.writeDefaultNamespace(namespaceURI);
   }

   @Override
   public void writeDTD(final String dtd) throws XMLStreamException {
      wrapped.writeDTD(dtd);
   }

   @Override
   public void writeEmptyElement(final String localName) throws XMLStreamException {
      wrapped.writeEmptyElement(localName);
   }

   @Override
   public void writeEmptyElement(final String namespaceURI, final String localName) throws XMLStreamException {
      wrapped.writeEmptyElement(namespaceURI, localName);
   }

   @Override
   public void writeEmptyElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
      wrapped.writeEmptyElement(prefix, localName, namespaceURI);
   }

   @Override
   public void writeEndDocument() throws XMLStreamException {
      wrapped.writeEndDocument();
   }

   @Override
   public void writeEndElement() throws XMLStreamException {
      wrapped.writeEndElement();
   }

   @Override
   public void writeEntityRef(final String name) throws XMLStreamException {
      wrapped.writeEntityRef(name);
   }

   @Override
   public void writeNamespace(final String prefix, final String namespaceURI) throws XMLStreamException {
      wrapped.writeNamespace(prefix, namespaceURI);
   }

   @Override
   public void writeProcessingInstruction(final String target) throws XMLStreamException {
      wrapped.writeProcessingInstruction(target);
   }

   @Override
   public void writeProcessingInstruction(final String target, final String data) throws XMLStreamException {
      wrapped.writeProcessingInstruction(target, data);
   }

   @Override
   public void writeStartDocument() throws XMLStreamException {
      wrapped.writeStartDocument();
   }

   @Override
   public void writeStartDocument(final String version) throws XMLStreamException {
      wrapped.writeStartDocument(version);
   }

   @Override
   public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
      wrapped.writeStartDocument(encoding, version);
   }

   @Override
   public void writeStartElement(final String localName) throws XMLStreamException {
      wrapped.writeStartElement(localName);
   }

   @Override
   public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
      wrapped.writeStartElement(namespaceURI, localName);
   }

   @Override
   public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
      wrapped.writeStartElement(prefix, localName, namespaceURI);
   }
}
