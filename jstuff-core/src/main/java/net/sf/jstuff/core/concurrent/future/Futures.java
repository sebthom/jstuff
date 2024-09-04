/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.Loops;
import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Futures {

   private static final Logger LOG = Logger.create();

   public static void cancel(final @Nullable Future<?> futureToCancel) {
      if (futureToCancel != null && !futureToCancel.isDone()) {
         futureToCancel.cancel(true);
      }
   }

   public static void cancelAll(final @Nullable Collection<? extends Future<?>> futuresToCancel) {
      if (futuresToCancel == null)
         return;
      for (final Future<?> futureToCancel : futuresToCancel) {
         cancel(futureToCancel);
      }
   }

   public static void cancelAll(final Future<?> @Nullable... futuresToCancel) {
      if (futuresToCancel == null)
         return;
      for (final Future<?> futureToCancel : futuresToCancel) {
         cancel(futureToCancel);
      }
   }

   /**
    * Combines multiple {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link Stream} with all elements from all collections.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link Stream} of all elements.
    */
   public static <T> CompletableFuture<Stream<T>> concat(final CompletableFuture<? extends Collection<T>> future1,
         final Collection<? extends CompletableFuture<? extends Collection<T>>> moreFutures) {
      CompletableFuture<Stream<T>> result = future1.thenApply(Collection::stream);

      for (final var futureN : moreFutures) {
         result = result.thenCombine(futureN, (combined, collN) -> Stream.concat(combined, collN.stream()));
      }
      forwardCancellation(result, future1);
      forwardCancellation(result, moreFutures);
      return result;
   }

   /**
    * Combines multiple {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link Stream} with all elements from all collections.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link Stream} of all elements.
    */
   @SafeVarargs
   @SuppressWarnings("null")
   public static <T> CompletableFuture<Stream<T>> concat(final CompletableFuture<? extends Collection<T>> future1,
         final CompletableFuture<? extends Collection<T>>... moreFutures) {
      CompletableFuture<Stream<T>> result = future1.thenApply(Collection::stream);

      for (final var futureN : moreFutures) {
         result = result.thenCombine(futureN, (combined, collN) -> Stream.concat(combined, collN.stream()));
      }
      forwardCancellation(result, future1);
      forwardCancellation(result, moreFutures);
      return result;
   }

   /**
    * Propagates the cancellation of a {@link CompletableFuture} to other {@link Future}s.
    * <p>
    * If the specified {@code from} future is cancelled, all futures in the provided {@code to} collection will be cancelled too.
    *
    * @param from the {@link CompletableFuture} whose cancellation should be propagated
    * @param to the collection of {@link Future} instances that should be cancelled if {@code from} is cancelled
    */
   public static void forwardCancellation(final CompletableFuture<?> from, final Collection<? extends Future<?>> to) {
      from.whenComplete((result, ex) -> {
         if (ex instanceof CancellationException) {
            to.forEach(f -> f.cancel(true));
         }
      });
   }

   /**
    * Propagates the cancellation of a {@link CompletableFuture} to other {@link Future}.
    * <p>
    * If the specified {@code from} future is cancelled, all futures in the provided {@code to} array will be cancelled too.
    *
    * @param from the {@link CompletableFuture} whose cancellation should be propagated
    * @param to the {@link Future} instance that should be cancelled if {@code from} is cancelled
    */
   public static void forwardCancellation(final CompletableFuture<?> from, final Future<?> to) {
      from.whenComplete((result, ex) -> {
         if (ex instanceof CancellationException) {
            to.cancel(true);
         }
      });
   }

   /**
    * Propagates the cancellation of a {@link CompletableFuture} to other {@link Future}s.
    * <p>
    * If the specified {@code from} future is cancelled, all futures in the provided {@code to} array will be cancelled too.
    *
    * @param from the {@link CompletableFuture} whose cancellation should be propagated
    * @param to the array of {@link Future} instances that should be cancelled if {@code from} is cancelled
    */
   public static void forwardCancellation(final CompletableFuture<?> from, final Future<?>... to) {
      from.whenComplete((result, ex) -> {
         if (ex instanceof CancellationException) {
            Loops.forEach(to, f -> f.cancel(true));
         }
      });
   }

   /**
    * Attempts to retrieve the result of the given {@link Future} within the specified timeout.
    *
    * @return the result of the future if completed normally, otherwise {@code fallback}
    */
   public static <T> T get(final Future<T> future, final long timeout, final TimeUnit unit, final T fallback) {
      try {
         return future.get(timeout, unit);
      } catch (final TimeoutException ex) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Could not get result within " + timeout + " " + unit.toString().toLowerCase() + "(s)", ex);
         }
      } catch (final InterruptedException ex) {
         Threads.handleInterruptedException(ex);
      } catch (final Exception ex) {
         LOG.debug(ex);
      }
      return fallback;
   }

   /**
    * Returns a list of results from all normally completed futures in the given collection.
    * <p>
    * Futures that are incomplete, canceled, or completed exceptionally are ignored.
    *
    * @return a list of results from all normally completed futures; an empty list if no futures are completed
    */
   public static <T> List<T> getNow(final Collection<? extends Future<? extends T>> futures) {
      if (futures.isEmpty())
         return Collections.emptyList();
      final List<T> result = new ArrayList<>();
      for (final var future : futures) {
         if (future.isDone()) {
            try {
               result.add(future.get(0, TimeUnit.SECONDS));
            } catch (final InterruptedException ex) {
               Threads.handleInterruptedException(ex);
            } catch (final Exception ex) {
               LOG.debug(ex);
            }
         }
      }
      return result;
   }

   /**
    * Returns the result of the given {@link Future} if it is already completed, or the specified
    * {@code fallback} if the future is incomplete, cancelled or completed exceptionally.
    *
    * @return the result of the future if completed normally, otherwise {@code fallback}
    */
   public static <T> T getNow(final CompletableFuture<T> future, final T fallback) {
      if (future.isDone()) {
         try {
            return future.getNow(fallback);
         } catch (final Exception ex) {
            LOG.debug(ex);
         }
      }
      return fallback;
   }

   /**
    * Returns the result of the given {@link Future} if it is already completed, or the specified
    * {@code fallback} if the future is incomplete or completed exceptionally.
    *
    * @return the result of the future if completed, otherwise {@code fallback}
    */
   public static <T> T getNow(final Future<T> future, final T fallback) {
      if (future.isDone()) {
         try {
            return future.get(0, TimeUnit.SECONDS);
         } catch (final InterruptedException ex) {
            Threads.handleInterruptedException(ex);
         } catch (final Exception ex) {
            LOG.debug(ex);
         }
      }
      return fallback;
   }

   /**
    * Returns a list of results from all successfully completed futures in the given collection.
    * <p>
    * If at least one future was canceled or completed exceptionally, this method will throw the corresponding exception.
    * Futures that are incomplete are ignored.
    *
    * @return a list of results from all completed futures; an empty list if no futures are completed
    *
    * @throws CancellationException if any future was canceled
    * @throws ExecutionException if any future completed exceptionally
    */
   public static <T> List<T> getNowOrThrow(final Collection<? extends Future<? extends T>> futures) throws ExecutionException {
      if (futures.isEmpty())
         return Collections.emptyList();
      final List<T> result = new ArrayList<>();
      for (final var future : futures) {
         if (future.isDone()) {
            try {
               result.add(future.get());
            } catch (final InterruptedException ex) {
               Threads.handleInterruptedException(ex);
            } catch (final CancellationException | ExecutionException ex) {
               throw ex;
            }
         }
      }
      return result;
   }

   /**
    * Combines two {@link CompletableFuture}s into a single {@link CompletableFuture}
    * that returns a {@link List} with results from both futures.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link List}.
    */
   public static <T> CompletableFuture<List<T>> join(final CompletableFuture<? extends T> future1,
         final Collection<? extends CompletableFuture<? extends T>> moreFutures) {

      CompletableFuture<List<T>> combinedFuture = future1.thenApply(v -> {
         final var list = new ArrayList<T>(moreFutures.size() + 1);
         list.add(v);
         return list;
      });

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, collN) -> {
            combined.add(collN);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }

   /**
    * Combines two {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link List} with all elements from both collections.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link List}.
    */
   public static <T> CompletableFuture<List<T>> join(final CompletableFuture<? extends T> future1,
         final CompletableFuture<? extends T> future2) {
      @SuppressWarnings("null")
      final CompletableFuture<List<T>> combinedFuture = future1.thenCombine(future2, List::of);
      forwardCancellation(combinedFuture, future1, future2);
      return combinedFuture;
   }

   /**
    * Combines multiple {@link CompletableFuture}s into a single {@link CompletableFuture}
    * that returns a {@link List} with results from all futures.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link List}.
    */
   @SafeVarargs
   @SuppressWarnings("null")
   public static <T> CompletableFuture<List<T>> join(final CompletableFuture<? extends T> future1,
         final CompletableFuture<? extends T>... moreFutures) {

      CompletableFuture<List<T>> combinedFuture = future1.thenApply(v -> {
         final var list = new ArrayList<T>(moreFutures.length + 1);
         list.add(v);
         return list;
      });

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, collN) -> {
            combined.add(collN);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }

   /**
    * Combines multiple {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link List} with all elements from all collections.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link List}.
    */
   public static <T> CompletableFuture<List<T>> joinFlat(final CompletableFuture<? extends Collection<T>> future1,
         final Collection<? extends CompletableFuture<? extends Collection<T>>> moreFutures) {
      CompletableFuture<List<T>> combinedFuture = future1.thenApply(ArrayList::new);

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, collN) -> {
            combined.addAll(collN);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }

   /**
    * Combines two {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link List} with all elements from both collections.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link List}.
    */
   public static <T> CompletableFuture<List<T>> joinFlat(final CompletableFuture<? extends Collection<T>> future1,
         final CompletableFuture<? extends Collection<T>> future2) {
      final CompletableFuture<List<T>> combinedFuture = future1.thenCombine(future2, (coll1, coll2) -> {
         final List<T> combined = new ArrayList<>(coll1);
         combined.addAll(coll2);
         return combined;
      });
      forwardCancellation(combinedFuture, future1, future2);
      return combinedFuture;
   }

   /**
    * Combines multiple {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link List} with all elements from all collections.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link List}.
    */
   @SafeVarargs
   @SuppressWarnings("null")
   public static <T> CompletableFuture<List<T>> joinFlat(final CompletableFuture<? extends Collection<T>> future1,
         final CompletableFuture<? extends Collection<T>>... moreFutures) {
      CompletableFuture<List<T>> combinedFuture = future1.thenApply(ArrayList::new);

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, collN) -> {
            combined.addAll(collN);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }

   /**
    * Combines multiple {@link CompletableFuture}s into a single {@link CompletableFuture}
    * that returns a {@link Set} with unique results from all futures.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link Set}.
    */
   public static <T> CompletableFuture<Set<T>> merge(final CompletableFuture<? extends T> future1,
         final Collection<? extends CompletableFuture<? extends T>> moreFutures) {

      CompletableFuture<Set<T>> combinedFuture = future1.thenApply(v -> {
         final var set = new HashSet<T>(moreFutures.size() + 1);
         set.add(v);
         return set;
      });

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, v) -> {
            combined.add(v);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }

   /**
    * Combines multiple {@link CompletableFuture}s into a single {@link CompletableFuture}
    * that returns a {@link Set} with unique results from all futures.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link Set}.
    */
   @SafeVarargs
   @SuppressWarnings("null")
   public static <T> CompletableFuture<Set<T>> merge(final CompletableFuture<? extends T> future1,
         final CompletableFuture<? extends T>... moreFutures) {

      CompletableFuture<Set<T>> combinedFuture = future1.thenApply(v -> {
         final var set = new HashSet<T>(moreFutures.length + 1);
         set.add(v);
         return set;
      });

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, v) -> {
            combined.add(v);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }

   /**
    * Combines multiple {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link Set} with all unique elements from all collections.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link Set}.
    */
   public static <T> CompletableFuture<Set<T>> mergeFlat(final CompletableFuture<? extends Collection<T>> future1,
         final Collection<? extends CompletableFuture<? extends Collection<T>>> moreFutures) {
      CompletableFuture<Set<T>> combinedFuture = future1.thenApply(HashSet::new);

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, collN) -> {
            combined.addAll(collN);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }

   /**
    * Combines multiple {@link CompletableFuture}s containing {@link Collection}s into a single {@link CompletableFuture}
    * that returns a {@link Set} with all unique elements from all collections.
    * <p>
    * Cancellation of the result future is propagated to the input futures.
    *
    * @return A {@link CompletableFuture} that completes with a combined {@link Set}.
    */
   @SafeVarargs
   @SuppressWarnings("null")
   public static <T> CompletableFuture<Set<T>> mergeFlat(final CompletableFuture<? extends Collection<T>> future1,
         final CompletableFuture<? extends Collection<T>>... moreFutures) {
      CompletableFuture<Set<T>> combinedFuture = future1.thenApply(HashSet::new);

      for (final var futureN : moreFutures) {
         combinedFuture = combinedFuture.thenCombine(futureN, (combined, collN) -> {
            combined.addAll(collN);
            return combined;
         });
      }
      forwardCancellation(combinedFuture, future1);
      forwardCancellation(combinedFuture, moreFutures);
      return combinedFuture;
   }
}
