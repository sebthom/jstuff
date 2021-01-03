/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection.iterator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class EmptyIterator<T> implements Iterator<T>, Serializable {

   private static final long serialVersionUID = 1L;

   static final EmptyIterator<?> INSTANCE = new EmptyIterator<>();

   @SuppressWarnings("unchecked")
   public static <T> EmptyIterator<T> get() {
      return (EmptyIterator<T>) INSTANCE;
   }

   private EmptyIterator() {
   }

   @Override
   public boolean hasNext() {
      return false;
   }

   @Override
   public T next() {
      throw new NoSuchElementException();
   }

   @SuppressWarnings("unused")
   private Object readResolve() throws ObjectStreamException {
      return INSTANCE;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }
}
