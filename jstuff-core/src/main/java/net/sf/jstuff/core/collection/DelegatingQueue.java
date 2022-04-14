/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Queue;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DelegatingQueue<V> extends DelegatingCollection<V> implements Queue<V> {

   private static final long serialVersionUID = 1L;

   private final Queue<V> delegate;

   protected DelegatingQueue(final Queue<V> delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   @Override
   public V element() {
      return delegate.element();
   }

   @Override
   public boolean offer(final V e) {
      return delegate.offer(e);
   }

   @Override
   public V peek() {
      return delegate.peek();
   }

   @Override
   public V poll() {
      return delegate.poll();
   }

   @Override
   public V remove() {
      return delegate.remove();
   }
}
