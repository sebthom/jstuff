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

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SingleObjectIterator<T> implements Iterator<T>, Serializable {

   private static final long serialVersionUID = 1L;

   private T item;
   private boolean hasNext = true;

   public SingleObjectIterator(final T item) {
      this.item = item;
   }

   @Override
   public boolean hasNext() {
      return hasNext;
   }

   @Override
   public T next() {
      if (hasNext) {
         hasNext = false;
         final T tmp = item;
         // help the gc
         item = null;
         return tmp;
      }
      throw new NoSuchElementException();
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }
}
