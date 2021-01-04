/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DelegatingList<V> extends DelegatingCollection<V> implements List<V> {

   private static final long serialVersionUID = 1L;

   private final List<V> delegate;

   protected DelegatingList(final List<V> delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   @Override
   public void add(final int index, final V element) {
      delegate.add(index, element);
   }

   @Override
   public boolean addAll(final int index, final Collection<? extends V> c) {
      return delegate.addAll(index, c);
   }

   @Override
   public V get(final int index) {
      return delegate.get(index);
   }

   @Override
   public int indexOf(final Object o) {
      return delegate.indexOf(o);
   }

   @Override
   public int lastIndexOf(final Object o) {
      return delegate.lastIndexOf(o);
   }

   @Override
   public ListIterator<V> listIterator() {
      return delegate.listIterator();
   }

   @Override
   public ListIterator<V> listIterator(final int index) {
      return delegate.listIterator(index);
   }

   @Override
   public V remove(final int index) {
      return delegate.remove(index);
   }

   @Override
   public V set(final int index, final V element) {
      return delegate.set(index, element);
   }

   @Override
   public List<V> subList(final int fromIndex, final int toIndex) {
      return delegate.subList(fromIndex, toIndex);
   }

   @Override
   public String toString() {
      return super.toString() + delegate;
   }
}
