/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.xml.stream;

import java.util.function.Consumer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingXMLEventReader extends Decorator.Default<XMLEventReader> implements AutoCloseableXMLEventReader {

   public DelegatingXMLEventReader() {
   }

   public DelegatingXMLEventReader(final XMLEventReader wrapped) {
      super(wrapped);
   }

   @Override
   public XMLEvent nextEvent() throws XMLStreamException {
      return wrapped.nextEvent();
   }

   @Override
   public boolean hasNext() {
      return wrapped.hasNext();
   }

   @Override
   public XMLEvent peek() throws XMLStreamException {
      return wrapped.peek();
   }

   @Override
   public Object next() {
      return wrapped.next();
   }

   @Override
   public String getElementText() throws XMLStreamException {
      return wrapped.getElementText();
   }

   @Override
   public void remove() {
      wrapped.remove();
   }

   @Override
   public XMLEvent nextTag() throws XMLStreamException {
      return wrapped.nextTag();
   }

   @Override
   public Object getProperty(final String name) throws IllegalArgumentException {
      return wrapped.getProperty(name);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void forEachRemaining(final Consumer action) {
      wrapped.forEachRemaining(action);
   }

   @Override
   public void close() throws XMLStreamException {
      wrapped.close();
   }
}
