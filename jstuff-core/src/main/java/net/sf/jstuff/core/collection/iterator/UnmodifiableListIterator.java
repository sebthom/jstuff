/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.iterator;

import java.util.ListIterator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnmodifiableListIterator<T> extends UnmodifiableIterator<T> implements ListIterator<T> {
   private static final long serialVersionUID = 1L;

   private final ListIterator<T> delegate;

   public UnmodifiableListIterator(final ListIterator<T> delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   @Override
   public void add(final T o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean hasPrevious() {
      return delegate.hasPrevious();
   }

   @Override
   public int nextIndex() {
      return delegate.nextIndex();
   }

   @Override
   public T previous() {
      return delegate.previous();
   }

   @Override
   public int previousIndex() {
      return delegate.previousIndex();
   }

   @Override
   public void set(final T o) {
      throw new UnsupportedOperationException();
   }
}
