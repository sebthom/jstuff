/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@NonNullByDefault({})
public final class ReadOnlyCompletableFuture<T> extends DelegatingCompletableFuture<T> {

   public ReadOnlyCompletableFuture(final CompletableFuture<T> wrapped) {
      super(wrapped);
   }

   @Override
   public boolean cancel(final boolean mayInterruptIfRunning) {
      return false;
   }

   @Override
   public boolean complete(final T value) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CompletableFuture<@Nullable T> completeAsync(final Supplier<? extends T> supplier) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CompletableFuture<T> completeAsync(final Supplier<? extends T> supplier, final Executor executor) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean completeExceptionally(final Throwable ex) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CompletableFuture<T> completeOnTimeout(final T value, final long timeout, final TimeUnit unit) {
      throw new UnsupportedOperationException();
   }
}
