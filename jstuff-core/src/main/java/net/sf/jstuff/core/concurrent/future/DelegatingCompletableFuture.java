/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@NonNullByDefault({})
public class DelegatingCompletableFuture<T> extends CompletableFuture<T> {

   private CompletableFuture<T> wrapped;

   public DelegatingCompletableFuture(final CompletableFuture<T> wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public CompletableFuture<Void> acceptEither(final CompletionStage<? extends T> other, final Consumer<? super T> action) {
      return wrapped.acceptEither(other, action);
   }

   @Override
   public CompletableFuture<Void> acceptEitherAsync(final CompletionStage<? extends T> other, final Consumer<? super T> action) {
      return wrapped.acceptEitherAsync(other, action);
   }

   @Override
   public CompletableFuture<Void> acceptEitherAsync(final CompletionStage<? extends T> other, final Consumer<? super T> action,
         final Executor executor) {
      return wrapped.acceptEitherAsync(other, action, executor);
   }

   @Override
   public <U> CompletableFuture<U> applyToEither(final CompletionStage<? extends T> other, final Function<? super T, U> fn) {
      return wrapped.applyToEither(other, fn);
   }

   @Override
   public <U> CompletableFuture<U> applyToEitherAsync(final CompletionStage<? extends T> other, final Function<? super T, U> fn) {
      return wrapped.applyToEitherAsync(other, fn);
   }

   @Override
   public <U> CompletableFuture<U> applyToEitherAsync(final CompletionStage<? extends T> other, final Function<? super T, U> fn,
         final Executor executor) {
      return wrapped.applyToEitherAsync(other, fn, executor);
   }

   @Override
   public boolean cancel(final boolean mayInterruptIfRunning) {
      return wrapped.cancel(mayInterruptIfRunning);
   }

   @Override
   public boolean complete(final T value) {
      return wrapped.complete(value);
   }

   @Override
   public CompletableFuture<@Nullable T> completeAsync(final Supplier<? extends T> supplier) {
      return wrapped.completeAsync(supplier);
   }

   @Override
   public CompletableFuture<T> completeAsync(final Supplier<? extends T> supplier, final Executor executor) {
      return wrapped.completeAsync(supplier, executor);
   }

   @Override
   public boolean completeExceptionally(final Throwable ex) {
      return wrapped.completeExceptionally(ex);
   }

   @Override
   public CompletableFuture<T> completeOnTimeout(final T value, final long timeout, final TimeUnit unit) {
      return wrapped.completeOnTimeout(value, timeout, unit);
   }

   @Override
   public CompletableFuture<T> copy() {
      return wrapped.copy();
   }

   @Override
   public Executor defaultExecutor() {
      return wrapped.defaultExecutor();
   }

   @Override
   public boolean equals(@Nullable final Object obj) {
      return wrapped.equals(obj);
   }

   @Override
   public CompletableFuture<T> exceptionally(final Function<Throwable, ? extends T> fn) {
      return wrapped.exceptionally(fn);
   }

   @Override
   public T get() throws InterruptedException, ExecutionException {
      return wrapped.get();
   }

   @Override
   public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      return wrapped.get(timeout, unit);
   }

   @Override
   public T getNow(final T valueIfAbsent) {
      return wrapped.getNow(valueIfAbsent);
   }

   @Override
   public int getNumberOfDependents() {
      return wrapped.getNumberOfDependents();
   }

   @Override
   public <U> CompletableFuture<U> handle(final BiFunction<? super T, @Nullable Throwable, ? extends U> fn) {
      return wrapped.handle(fn);
   }

   @Override
   public <U> CompletableFuture<U> handleAsync(final BiFunction<? super T, @Nullable Throwable, ? extends U> fn) {
      return wrapped.handleAsync(fn);
   }

   @Override
   public <U> CompletableFuture<U> handleAsync(final BiFunction<? super T, @Nullable Throwable, ? extends U> fn, final Executor executor) {
      return wrapped.handleAsync(fn, executor);
   }

   @Override
   public int hashCode() {
      return wrapped.hashCode();
   }

   @Override
   public boolean isCancelled() {
      return wrapped.isCancelled();
   }

   @Override
   public boolean isCompletedExceptionally() {
      return wrapped.isCompletedExceptionally();
   }

   @Override
   public boolean isDone() {
      return wrapped.isDone();
   }

   @Override
   public T join() {
      return wrapped.join();
   }

   @Override
   public CompletionStage<T> minimalCompletionStage() {
      return wrapped.minimalCompletionStage();
   }

   @Override
   public <U> CompletableFuture<U> newIncompleteFuture() {
      return wrapped.newIncompleteFuture();
   }

   @Override
   public void obtrudeException(final Throwable ex) {
      wrapped.obtrudeException(ex);
   }

   @Override
   public void obtrudeValue(final T value) {
      wrapped.obtrudeValue(value);
   }

   @Override
   public CompletableFuture<T> orTimeout(final long timeout, final TimeUnit unit) {
      return wrapped.orTimeout(timeout, unit);
   }

   @Override
   public CompletableFuture<Void> runAfterBoth(final CompletionStage<?> other, final Runnable action) {
      return wrapped.runAfterBoth(other, action);
   }

   @Override
   public CompletableFuture<Void> runAfterBothAsync(final CompletionStage<?> other, final Runnable action) {
      return wrapped.runAfterBothAsync(other, action);
   }

   @Override
   public CompletableFuture<Void> runAfterBothAsync(final CompletionStage<?> other, final Runnable action, final Executor executor) {
      return wrapped.runAfterBothAsync(other, action, executor);
   }

   @Override
   public CompletableFuture<Void> runAfterEither(final CompletionStage<?> other, final Runnable action) {
      return wrapped.runAfterEither(other, action);
   }

   @Override
   public CompletableFuture<Void> runAfterEitherAsync(final CompletionStage<?> other, final Runnable action) {
      return wrapped.runAfterEitherAsync(other, action);
   }

   @Override
   public CompletableFuture<Void> runAfterEitherAsync(final CompletionStage<?> other, final Runnable action, final Executor executor) {
      return wrapped.runAfterEitherAsync(other, action, executor);
   }

   @Override
   public CompletableFuture<Void> thenAccept(final Consumer<? super T> action) {
      return wrapped.thenAccept(action);
   }

   @Override
   public CompletableFuture<Void> thenAcceptAsync(final Consumer<? super T> action) {
      return wrapped.thenAcceptAsync(action);
   }

   @Override
   public CompletableFuture<Void> thenAcceptAsync(final Consumer<? super T> action, final Executor executor) {
      return wrapped.thenAcceptAsync(action, executor);
   }

   @Override
   public <U> CompletableFuture<Void> thenAcceptBoth(final CompletionStage<? extends U> other,
         final BiConsumer<? super T, ? super U> action) {
      return wrapped.thenAcceptBoth(other, action);
   }

   @Override
   public <U> CompletableFuture<Void> thenAcceptBothAsync(final CompletionStage<? extends U> other,
         final BiConsumer<? super T, ? super U> action) {
      return wrapped.thenAcceptBothAsync(other, action);
   }

   @Override
   public <U> CompletableFuture<Void> thenAcceptBothAsync(final CompletionStage<? extends U> other,
         final BiConsumer<? super T, ? super U> action, final Executor executor) {
      return wrapped.thenAcceptBothAsync(other, action, executor);
   }

   @Override
   public <U> CompletableFuture<U> thenApply(final Function<? super T, ? extends U> fn) {
      return wrapped.thenApply(fn);
   }

   @Override
   public <U> CompletableFuture<U> thenApplyAsync(final Function<? super T, ? extends U> fn) {
      return wrapped.thenApplyAsync(fn);
   }

   @Override
   public <U> CompletableFuture<U> thenApplyAsync(final Function<? super T, ? extends U> fn, final Executor executor) {
      return wrapped.thenApplyAsync(fn, executor);
   }

   @Override
   public <U, V> @NonNull CompletableFuture<V> thenCombine(@NonNull final CompletionStage<? extends U> other,
         @NonNull final BiFunction<? super T, ? super U, ? extends V> fn) {
      return wrapped.thenCombine(other, fn);
   }

   @Override
   public <U, V> @NonNull CompletableFuture<V> thenCombineAsync(@NonNull final CompletionStage<? extends U> other,
         @NonNull final BiFunction<? super T, ? super U, ? extends V> fn) {
      return wrapped.thenCombineAsync(other, fn);
   }

   @Override
   public <U, V> @NonNull CompletableFuture<V> thenCombineAsync(@NonNull final CompletionStage<? extends U> other,
         @NonNull final BiFunction<? super T, ? super U, ? extends V> fn, @NonNull final Executor executor) {
      return wrapped.thenCombineAsync(other, fn, executor);
   }

   @Override
   public <U> CompletableFuture<U> thenCompose(final Function<? super T, ? extends CompletionStage<U>> fn) {
      return wrapped.thenCompose(fn);
   }

   @Override
   public <U> CompletableFuture<U> thenComposeAsync(final Function<? super T, ? extends CompletionStage<U>> fn) {
      return wrapped.thenComposeAsync(fn);
   }

   @Override
   public <U> CompletableFuture<U> thenComposeAsync(final Function<? super T, ? extends CompletionStage<U>> fn, final Executor executor) {
      return wrapped.thenComposeAsync(fn, executor);
   }

   @Override
   public CompletableFuture<Void> thenRun(final Runnable action) {
      return wrapped.thenRun(action);
   }

   @Override
   public CompletableFuture<Void> thenRunAsync(final Runnable action) {
      return wrapped.thenRunAsync(action);
   }

   @Override
   public CompletableFuture<Void> thenRunAsync(final Runnable action, final Executor executor) {
      return wrapped.thenRunAsync(action, executor);
   }

   @Override
   public CompletableFuture<T> toCompletableFuture() {
      return this;
   }

   @Override
   public String toString() {
      return wrapped.toString();
   }

   @Override
   public CompletableFuture<T> whenComplete(final BiConsumer<? super T, ? super @Nullable Throwable> action) {
      return wrapped.whenComplete(action);
   }

   @Override
   public CompletableFuture<T> whenCompleteAsync(final BiConsumer<? super T, ? super @Nullable Throwable> action) {
      return wrapped.whenCompleteAsync(action);
   }

   @Override
   public CompletableFuture<T> whenCompleteAsync(final BiConsumer<? super T, ? super @Nullable Throwable> action, final Executor executor) {
      return wrapped.whenCompleteAsync(action, executor);
   }
}
