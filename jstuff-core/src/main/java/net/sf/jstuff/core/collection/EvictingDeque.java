/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.LinkedList;

import net.sf.jstuff.core.concurrent.NotThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * A non-blocking queue which automatically evicts elements from:
 * <li>the head of the queue when new elements are added to the tail of the queue while it is full
 * <li>the tail of the queue when new elements are added to the head of the queue while it is full
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@NotThreadSafe
public class EvictingDeque<E> extends DelegatingDeque<E> {

   private static final long serialVersionUID = 1L;

   private final int capacity;

   public EvictingDeque(final int capacity) {
      super(new LinkedList<>());
      Args.notNegative("capacity", capacity);
      this.capacity = capacity;

      wrappedGettable = false;
      wrappedSettable = false;
   }

   @Override
   public boolean add(final E o) {
      return offerLast(o);
   }

   @Override
   public boolean addAll(final Collection<? extends E> c) {
      if (c == null)
         return false;

      boolean modified = false;
      for (final E e : c) {
         if (offerLast(e)) {
            modified = true;
         }
      }
      return modified;
   }

   @Override
   public void addFirst(final E e) {
      offerFirst(e);
   }

   @Override
   public void addLast(final E e) {
      offerLast(e);
   }

   @Override
   public boolean offer(final E e) {
      return offerLast(e);
   }

   @Override
   public boolean offerFirst(final E e) {
      final boolean added = super.offerFirst(e);
      while (added && size() > capacity) {
         removeLast();
      }
      return added;
   }

   @Override
   public boolean offerLast(final E e) {
      final boolean added = super.offerLast(e);
      while (added && size() > capacity) {
         removeFirst();
      }
      return added;
   }

   @Override
   public void push(final E e) {
      addFirst(e);
   }

   public int remainingCapacity() {
      return capacity - size();
   }
}
