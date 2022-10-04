/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.iterator;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeIterator<V> extends Composite.Default<Iterator<? extends V>> implements Iterator<V> {

   private static final long serialVersionUID = 1L;

   private Iterator<? extends V> lastItemIterator = Iterators.empty();
   private Iterator<? extends V> nextItemIterator = Iterators.empty();

   public CompositeIterator() {
   }

   public CompositeIterator(final Collection<? extends @Nullable Iterator<? extends V>> initialIterators) {
      super(false, initialIterators);
      if (!components.isEmpty()) {
         nextItemIterator = CollectionUtils.remove(components, 0);
      }
   }

   @SuppressWarnings("null")
   @SafeVarargs
   public CompositeIterator(final @NonNullByDefault({}) Iterator<? extends V>... initialIterators) {
      super(initialIterators);
      if (!components.isEmpty()) {
         nextItemIterator = CollectionUtils.remove(components, 0);
      }
   }

   @Override
   public boolean hasNext() {
      prepareNextItemIterator();
      return nextItemIterator.hasNext();
   }

   @Override
   public V next() {
      prepareNextItemIterator();
      final V item = nextItemIterator.next();
      lastItemIterator = nextItemIterator;
      return item;
   }

   protected void prepareNextItemIterator() {
      if (nextItemIterator.hasNext())
         return;
      if (!components.isEmpty()) {
         nextItemIterator = CollectionUtils.remove(components, 0);
         prepareNextItemIterator();
      }
   }

   @Override
   public void remove() {
      lastItemIterator.remove();
   }
}
