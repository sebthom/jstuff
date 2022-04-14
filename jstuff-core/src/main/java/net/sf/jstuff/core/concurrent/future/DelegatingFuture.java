/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingFuture<V> extends Decorator.Default<Future<V>> implements Future<V> {

   public DelegatingFuture(final Future<V> wrapped) {
      super(wrapped);
   }

   @Override
   public boolean cancel(final boolean mayInterruptIfRunning) {
      return wrapped.cancel(mayInterruptIfRunning);
   }

   @Override
   public V get() throws InterruptedException, ExecutionException {
      return wrapped.get();
   }

   @Override
   public V get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return wrapped.get(timeout, unit);
   }

   @Override
   public boolean isCancelled() {
      return wrapped.isCancelled();
   }

   @Override
   public boolean isDone() {
      return wrapped.isDone();
   }
}
