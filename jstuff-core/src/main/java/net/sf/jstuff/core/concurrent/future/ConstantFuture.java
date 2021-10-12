/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ConstantFuture<T> implements Future<T> {

   public static <T> ConstantFuture<T> of(final T value) {
      return new ConstantFuture<>(value);
   }

   private final T value;

   public ConstantFuture(final T value) {
      this.value = value;
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
   public T get() {
      return value;
   }

   @Override
   public T get(final long timeout, final TimeUnit unit) {
      return get();
   }
}
