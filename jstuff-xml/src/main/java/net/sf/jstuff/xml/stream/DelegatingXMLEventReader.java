/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.xml.stream;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.util.function.Consumer;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingXMLEventReader extends Decorator.Default<XMLEventReader> implements AutoCloseableXMLEventReader {

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
   public @Nullable XMLEvent peek() throws XMLStreamException {
      return wrapped.peek();
   }

   @Override
   public Object next() {
      return asNonNullUnsafe(wrapped.next());
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

   @Override
   public void forEachRemaining(@NonNullByDefault({}) @NonNull final Consumer<? super Object> action) {
      AutoCloseableXMLEventReader.super.forEachRemaining(action);
   }

   @Override
   public void close() throws XMLStreamException {
      wrapped.close();
   }
}
