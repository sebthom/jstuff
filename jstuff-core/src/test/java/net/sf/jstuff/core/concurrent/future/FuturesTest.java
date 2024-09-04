/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FuturesTest {

   @Test
   public void testCancel() {
      Futures.cancel((Future<?>) null);
      Futures.cancelAll((Future<?>[]) null);
      Futures.cancelAll((Collection<? extends Future<?>>) null);

      final var future1 = new CompletableFuture<>();
      final var future2 = new CompletableFuture<>();
      final var future3 = new CompletableFuture<>();
      final var future4 = new CompletableFuture<>();
      final var future5 = new CompletableFuture<>();

      Futures.cancel(future1);
      Futures.cancelAll(future2, future3, null);
      Futures.cancelAll(List.of(future4, future5));

      assertThat(future1).isCancelled();
      assertThat(future2).isCancelled();
      assertThat(future3).isCancelled();
      assertThat(future4).isCancelled();
      assertThat(future5).isCancelled();
   }

   @Test
   @SuppressWarnings("null")
   public void testConcat() throws InterruptedException, ExecutionException {
      final var fut1 = CompletableFuture.supplyAsync(() -> List.of("a", "b"));
      final var fut2 = CompletableFuture.supplyAsync(() -> new TreeSet<>(Set.of("c", "d")));
      final var fut3 = CompletableFuture.supplyAsync(() -> List.of("e", "f"));
      final var fut4 = CompletableFuture.supplyAsync(() -> new TreeSet<>(Set.of("g", "h")));

      assertEquals(List.of("a", "b"), Futures.concat(fut1).get().toList());
      assertEquals(List.of("a", "b", "c", "d"), Futures.concat(fut1, fut2).get().toList());
      assertEquals(List.of("a", "b", "c", "d", "e", "f"), Futures.concat(fut1, fut2, fut3).get().toList());
      assertEquals(List.of("a", "b", "c", "d", "e", "f", "g", "h"), Futures.concat(fut1, fut2, fut3, fut4).get().toList());
      assertEquals(List.of("a", "b", "c", "d", "e", "f", "g", "h"), Futures.concat(fut1, List.of(fut2, fut3, fut4)).get().toList());
   }

   @Test
   public void testForwardCancellation1() {
      final var sourceFuture = new CompletableFuture<>();
      final var futures = new ArrayList<CompletableFuture<?>>();
      for (int i = 0; i < 3; i++) {
         futures.add(new CompletableFuture<>());
      }
      Futures.forwardCancellation(sourceFuture, futures);

      sourceFuture.cancel(true);
      for (final var future : futures) {
         assertThat(future).isCancelled();
      }
   }

   @Test
   public void testForwardCancellation2() {
      final var sourceFuture = new CompletableFuture<>();
      final var futures = new ArrayList<CompletableFuture<?>>();
      for (int i = 0; i < 3; i++) {
         futures.add(new CompletableFuture<>());
      }
      Futures.forwardCancellation(sourceFuture, futures.toArray(CompletableFuture[]::new));

      sourceFuture.cancel(true);
      for (final var future : futures) {
         assertThat(future).isCancelled();
      }
   }

   @Test
   public void testGetNowCollection() {
      final var future1 = CompletableFuture.completedFuture("First");
      final var future2 = CompletableFuture.completedFuture("Second");

      // Test case with an empty collection
      {
         final Collection<Future<String>> futures = Collections.emptyList();
         final var result = Futures.getNow(futures);
         assertThat(result).isEmpty();
      }
      // Test case with some futures completed
      {
         final var futures = List.of(future1, future2);
         final var result = Futures.getNow(futures);
         assertThat(result).containsExactly("First", "Second");
      }
      // Test case with a future that completes exceptionally
      {
         final var futureCompletedExceptionally = new CompletableFuture<String>();
         futureCompletedExceptionally.completeExceptionally(new RuntimeException());
         final var futures = List.of(future1, futureCompletedExceptionally);
         final var result = Futures.getNow(futures);
         assertThat(result).containsExactly("First");
      }
   }

   @Test
   public void testGetNowFuture() {
      // Test case for future completed successfully
      {
         final var future = CompletableFuture.completedFuture("Success");
         final var result = Futures.getNow(future, "Default");
         assertThat(result).isEqualTo("Success");
      }
      // Test case for future not completed
      {
         final var future = new CompletableFuture<>();
         final var result = Futures.getNow(future, "Default");
         assertThat(result).isEqualTo("Default");
      }

      // Test case for future completed exceptionally
      {
         final var futureCompletedExceptionally = new CompletableFuture<String>();
         futureCompletedExceptionally.completeExceptionally(new RuntimeException());
         var result = Futures.getNow(futureCompletedExceptionally, "Default");
         assertThat(result).isEqualTo("Default");

         result = Futures.get(futureCompletedExceptionally, 1, TimeUnit.SECONDS, "Default");
         assertThat(result).isEqualTo("Default");
      }
   }

   @Test
   public void testGetNowOrThrow() throws Exception {
      final var future1 = CompletableFuture.completedFuture("First");
      final var future2 = CompletableFuture.completedFuture("Second");

      // Test case with all futures completed successfully
      {
         final var futures = List.of(future1, future2);
         final var result = Futures.getNowOrThrow(futures);
         assertThat(result).containsExactly("First", "Second");
      }

      // Test case with a future that completes exceptionally
      {
         final var futureException = new CompletableFuture<String>();
         futureException.completeExceptionally(new RuntimeException());
         final var futures = List.of(future1, futureException);
         final var thrown = catchThrowable(() -> Futures.getNowOrThrow(futures));
         assertThat(thrown).isInstanceOf(ExecutionException.class);
      }

      // Test case with a future that is canceled
      {
         final var futureCanceled = new CompletableFuture<String>();
         futureCanceled.cancel(true);
         final var futures = List.of(future1, futureCanceled);
         final var thrown = catchThrowable(() -> Futures.getNowOrThrow(futures));
         assertThat(thrown).isInstanceOf(CancellationException.class);
      }
   }

   @Test
   @SuppressWarnings("null")
   public void testJoin() throws InterruptedException, ExecutionException {
      final var fut1 = CompletableFuture.supplyAsync(() -> List.of("a", "b"));
      final var fut2 = CompletableFuture.supplyAsync(() -> new TreeSet<>(Set.of("c", "d")));
      final var fut3 = CompletableFuture.supplyAsync(() -> List.of("e", "f"));
      final var fut4 = CompletableFuture.supplyAsync(() -> List.of("g", "h"));

      assertEquals(List.of(fut1.get()), Futures.join(fut1).get());
      assertEquals(List.of(fut1.get(), fut2.get()), Futures.join(fut1, fut2).get());
      assertEquals(List.of(fut1.get(), fut2.get(), fut3.get()), Futures.join(fut1, fut2, fut3).get());
      assertEquals(List.of(fut1.get(), fut2.get(), fut3.get(), fut4.get()), Futures.join(fut1, fut2, fut3, fut4).get());
      assertEquals(List.of(fut1.get(), fut2.get(), fut3.get(), fut4.get()), Futures.join(fut1, List.of(fut2, fut3, fut4)).get());
   }

   @Test
   @SuppressWarnings("null")
   public void testJoinFlat() throws InterruptedException, ExecutionException {
      final var fut1 = CompletableFuture.supplyAsync(() -> List.of("a", "b"));
      final var fut2 = CompletableFuture.supplyAsync(() -> new TreeSet<>(Set.of("c", "d")));
      final var fut3 = CompletableFuture.supplyAsync(() -> List.of("e", "f"));
      final var fut4 = CompletableFuture.supplyAsync(() -> new TreeSet<>(Set.of("g", "h")));

      assertEquals(List.of("a", "b"), Futures.joinFlat(fut1).get());
      assertEquals(List.of("a", "b", "c", "d"), Futures.joinFlat(fut1, fut2).get());
      assertEquals(List.of("a", "b", "c", "d", "e", "f"), Futures.joinFlat(fut1, fut2, fut3).get());
      assertEquals(List.of("a", "b", "c", "d", "e", "f", "g", "h"), Futures.joinFlat(fut1, fut2, fut3, fut4).get());
      assertEquals(List.of("a", "b", "c", "d", "e", "f", "g", "h"), Futures.joinFlat(fut1, List.of(fut2, fut3, fut4)).get());
   }

   @Test
   public void testJoinFlatWithCancel() throws InterruptedException {
      final var fut1 = CompletableFuture.supplyAsync(() -> List.of("a", "b"));
      final var fut2 = CompletableFuture.supplyAsync(() -> {
         try {
            Thread.sleep(2000);
         } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
         }
         return List.of("c", "d");
      });
      final var fut3 = CompletableFuture.supplyAsync(() -> {
         try {
            Thread.sleep(2000);
         } catch (final InterruptedException ex) {
            throw new RuntimeException(ex);
         }
         return List.of("e", "f");
      });

      Thread.sleep(500);
      Futures.joinFlat(fut1, fut2, fut3).cancel(true);
      assertTrue(fut1.isDone());
      assertTrue(fut2.isCancelled());
      assertTrue(fut3.isCancelled());
   }

   @Test
   public void testMerge() throws InterruptedException, ExecutionException {
      final var fut1 = CompletableFuture.supplyAsync(() -> List.of("a", "b"));
      final var fut2 = CompletableFuture.supplyAsync(() -> Set.of("a", "c"));
      final var fut3 = CompletableFuture.supplyAsync(() -> List.of("a", "b"));
      final var fut4 = CompletableFuture.supplyAsync(() -> Set.of("a", "e"));

      assertEquals(Set.of(fut1.get()), Futures.merge(fut1).get());
      assertEquals(Set.of(fut1.get(), fut2.get()), Futures.merge(fut1, fut2).get());
      assertEquals(Set.of(fut1.get()), Futures.merge(fut1, fut3).get());
      assertEquals(Set.of(fut1.get(), fut2.get()), Futures.merge(fut1, fut2, fut3).get());
      assertEquals(Set.of(fut1.get(), fut2.get(), fut4.get()), Futures.merge(fut1, fut2, fut3, fut4).get());
      assertEquals(Set.of(fut1.get(), fut2.get(), fut4.get()), Futures.merge(fut1, List.of(fut2, fut3, fut4)).get());
   }

   @Test
   public void testMergeFlat() throws InterruptedException, ExecutionException {
      final var fut1 = CompletableFuture.supplyAsync(() -> List.of("a", "b"));
      final var fut2 = CompletableFuture.supplyAsync(() -> Set.of("a", "c"));
      final var fut3 = CompletableFuture.supplyAsync(() -> List.of("a", "d"));
      final var fut4 = CompletableFuture.supplyAsync(() -> Set.of("a", "e"));

      assertEquals(Set.of("a", "b"), Futures.mergeFlat(fut1).get());
      assertEquals(Set.of("a", "b", "c"), Futures.mergeFlat(fut1, fut2).get());
      assertEquals(Set.of("a", "b", "c", "d"), Futures.mergeFlat(fut1, fut2, fut3).get());
      assertEquals(Set.of("a", "b", "c", "d", "e"), Futures.mergeFlat(fut1, fut2, fut3, fut4).get());
      assertEquals(Set.of("a", "b", "c", "d", "e"), Futures.mergeFlat(fut1, List.of(fut2, fut3, fut4)).get());
   }
}
