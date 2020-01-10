/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
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

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnmodifiableIterator<T> implements Iterator<T>, Serializable {

   private static final long serialVersionUID = 1L;

   private final Iterator<T> delegate;

   public UnmodifiableIterator(final Iterator<T> delegate) {
      Args.notNull("delegate", delegate);
      this.delegate = delegate;
   }

   @Override
   public boolean hasNext() {
      return delegate.hasNext();
   }

   @Override
   public T next() {
      return delegate.next();
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }
}
