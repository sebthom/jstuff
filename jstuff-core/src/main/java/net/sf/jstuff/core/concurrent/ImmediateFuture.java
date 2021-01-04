/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ImmediateFuture<T> implements Future<T> {

   private final T value;
   private final ExecutionException ex;

   public ImmediateFuture(final T value) {
      this.value = value;
      ex = null;
   }

   public ImmediateFuture(final Throwable ex) {
      Args.notNull("ex", ex);
      this.value = null;
      this.ex = new ExecutionException(ex);
   }

   @Override
   public boolean cancel(final boolean mayInterruptIfRunning) {
      return false;
   }

   @Override
   public boolean isCancelled() {
      return false;
   }

   @Override
   public boolean isDone() {
      return true;
   }

   @Override
   public T get() throws ExecutionException {
      if (ex == null)
         return value;
      throw ex;
   }

   @Override
   public T get(final long timeout, final TimeUnit unit) throws ExecutionException {
      return get();
   }

}
