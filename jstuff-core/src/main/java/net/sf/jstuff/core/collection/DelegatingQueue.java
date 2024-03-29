/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Queue;

import org.eclipse.jdt.annotation.Nullable;

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

   @Nullable
   @Override
   public V peek() {
      return delegate.peek();
   }

   @Nullable
   @Override
   public V poll() {
      return delegate.poll();
   }

   @Override
   public V remove() {
      return delegate.remove();
   }
}
