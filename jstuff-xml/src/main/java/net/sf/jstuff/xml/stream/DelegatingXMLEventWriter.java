/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingXMLEventWriter extends Decorator.Default<XMLEventWriter> implements AutoCloseableXMLEventWriter {

   public DelegatingXMLEventWriter() {
   }

   public DelegatingXMLEventWriter(final XMLEventWriter wrapped) {
      super(wrapped);
   }

   @Override
   public void add(final XMLEvent event) throws XMLStreamException {
      wrapped.add(event);
   }

   @Override
   public void add(final XMLEventReader reader) throws XMLStreamException {
      wrapped.add(reader);
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
   public @Nullable String getPrefix(final @Nullable String uri) throws XMLStreamException {
      return wrapped.getPrefix(uri);
   }

   @Override
   public void setDefaultNamespace(final @Nullable String uri) throws XMLStreamException {
      wrapped.setDefaultNamespace(uri);
   }

   @Override
   public void setNamespaceContext(final NamespaceContext context) throws XMLStreamException {
      wrapped.setNamespaceContext(context);
   }

   @Override
   public void setPrefix(final String prefix, final @Nullable String uri) throws XMLStreamException {
      wrapped.setPrefix(prefix, uri);
   }
}
