/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Deque;
import java.util.Iterator;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DelegatingDeque<V> extends DelegatingQueue<V> implements Deque<V> {

   private static final long serialVersionUID = 1L;

   private final Deque<V> delegate;

   protected DelegatingDeque(final Deque<V> delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   @Override
   public void addFirst(final V e) {
      delegate.addFirst(e);
   }

   @Override
   public void addLast(final V e) {
      delegate.addLast(e);
   }

   @Override
   public boolean offerFirst(final V e) {
      return delegate.offerFirst(e);
   }

   @Override
   public boolean offerLast(final V e) {
      return delegate.offerLast(e);
   }

   @Override
   public V removeFirst() {
      return delegate.removeFirst();
   }

   @Override
   public V removeLast() {
      return delegate.removeLast();
   }

   @Override
   public @Nullable V pollFirst() {
      return delegate.pollFirst();
   }

   @Override
   public @Nullable V pollLast() {
      return delegate.pollLast();
   }

   @Override
   public V getFirst() {
      return delegate.getFirst();
   }

   @Override
   public V getLast() {
      return delegate.getLast();
   }

   @Override
   public @Nullable V peekFirst() {
      return delegate.peekFirst();
   }

   @Override
   public @Nullable V peekLast() {
      return delegate.peekLast();
   }

   @Override
   public boolean removeFirstOccurrence(final @Nullable Object o) {
      return delegate.removeFirstOccurrence(o);
   }

   @Override
   public boolean removeLastOccurrence(final @Nullable Object o) {
      return delegate.removeLastOccurrence(o);
   }

   @Override
   public void push(final V e) {
      delegate.push(e);
   }

   @Override
   public V pop() {
      return delegate.pop();
   }

   @Override
   public Iterator<V> descendingIterator() {
      return delegate.descendingIterator();
   }
}
