/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import static net.sf.jstuff.core.concurrent.future.AbstractFutureTest.TaskState.*;
import static net.sf.jstuff.core.concurrent.future.FutureState.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.ref.MutableObservableRef;
import net.sf.jstuff.core.ref.MutableRef;
import net.sf.jstuff.core.ref.ObservableRef;
import net.sf.jstuff.core.ref.Ref;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
abstract class AbstractFutureTest {

   private static final int MAX_WAIT_SECS = 1;

   enum TaskState {
      NEW,
      RUNNING,
      INTERRUPTED,
      DONE
   }

   void assertFutureState(final Future<?> future, final FutureState expectedState) {
      assertThat(FutureState.of(future)).isEqualTo(expectedState);
   }

   void assertTaskState(final Ref<TaskState> actualState, final TaskState expectedState) {
      assertThat(actualState.get()).isEqualTo(expectedState);
   }

   void awaitFutureState(final @Nullable Future<?> future, final FutureState expectedState) throws InterruptedException {
      assert future != null;
      Threads.await(() -> FutureState.of(future) == expectedState, 10, MAX_WAIT_SECS, TimeUnit.SECONDS);
      assertFutureState(future, expectedState);
   }

   void awaitTaskState(final ObservableRef<TaskState> stateHolder, final TaskState expectedState) throws InterruptedException {
      stateHolder.await(expectedState, MAX_WAIT_SECS, TimeUnit.SECONDS);
      assertTaskState(stateHolder, expectedState);
   }

   void awaitTaskStateNOT(final ObservableRef<TaskState> stateHolder, final TaskState expectedState) throws InterruptedException {
      stateHolder.await(state -> state != expectedState, MAX_WAIT_SECS, TimeUnit.SECONDS);
      assertThat(stateHolder.get()).isNotEqualTo(expectedState);
   }

   Runnable createTask(final MutableRef<TaskState> state, final long waitMS) {
      return () -> {
         state.set(RUNNING);
         try {
            Thread.sleep(waitMS);
         } catch (final InterruptedException e) {
            Thread.interrupted();
            state.set(INTERRUPTED);
            return;
         }
         state.set(DONE);
      };
   }

   void testCopy(final Supplier<CompletableFuture<@Nullable String>> newFutureFactory) {
      final var future = newFutureFactory.get();
      final var copy = future.copy();
      final var minimalStage = future.minimalCompletionStage().toCompletableFuture();

      assertThat(copy).isNotEqualTo(future);
      assertThat(minimalStage).isNotEqualTo(future);

      assertFutureState(future, INCOMPLETE);
      assertFutureState(copy, INCOMPLETE);
      assertFutureState(minimalStage, INCOMPLETE);

      future.complete("foo");

      assertFutureState(future, COMPLETED);
      assertFutureState(copy, COMPLETED);
      assertFutureState(minimalStage, COMPLETED);

      assertThat(future).isCompletedWithValue("foo");
      assertThat(copy).isCompletedWithValue("foo");
      assertThat(minimalStage).isCompletedWithValue("foo");
   }

   private <U> void testCompletedFuture(final CompletableFuture<U> completedFuture, final U value) {
      assertFutureState(completedFuture, COMPLETED);
      assertThat(completedFuture).isCompletedWithValue(value);

      final var result = MutableRef.create();
      completedFuture.whenComplete((r, ex) -> {
         if (ex != null) {
            result.set(ex);
         } else {
            result.set(r);
         }
      });
      assertThat(result.get()).isEqualTo(value);

      assertThat(completedFuture.cancel(true)).isFalse();

   }

   void testCompletedFuture(final Supplier<CompletableFuture<@Nullable String>> newFutureFactory,
         final Function<@Nullable String, CompletableFuture<@Nullable String>> completedFutureFactory) {
      testCompletedFuture(completedFutureFactory.apply("value"), "value");
      testCompletedFuture(completedFutureFactory.apply(null), null);

      var completableFuture = newFutureFactory.get();
      completableFuture.complete("value");
      testCompletedFuture(completableFuture, "value");

      completableFuture = newFutureFactory.get();
      completableFuture.complete(null);
      testCompletedFuture(completableFuture, null);
   }

   void testMultipleStagesCancelDownstream(final Function<Runnable, CompletableFuture<?>> futureFactory, final boolean supportsInterrupt)
         throws InterruptedException {
      testMultipleStagesCancelDownstream(futureFactory, supportsInterrupt, false);
      testMultipleStagesCancelDownstream(futureFactory, supportsInterrupt, true);
   }

   private void testMultipleStagesCancelDownstream(final Function<Runnable, CompletableFuture<?>> futureFactory,
         final boolean supportsInterrupt, final boolean tryCancelWithInterrupt) throws InterruptedException {
      final var stage1State = MutableObservableRef.of(NEW);
      final var stage1 = futureFactory.apply(createTask(stage1State, 500));

      final var stage2State = MutableObservableRef.of(NEW);
      final var stage2 = stage1.thenRun(createTask(stage2State, 500));

      final var stage3State = MutableRef.of(NEW);
      final var stage3 = stage2.thenRun(createTask(stage2State, 500));

      awaitTaskState(stage1State, RUNNING);

      assertFutureState(stage1, INCOMPLETE);
      assertFutureState(stage2, INCOMPLETE);
      assertFutureState(stage3, INCOMPLETE);
      assertTaskState(stage1State, RUNNING);
      assertTaskState(stage2State, NEW);
      assertTaskState(stage3State, NEW);

      stage1.cancel(tryCancelWithInterrupt); // cancels this and subsequent stages

      awaitFutureState(stage1, CANCELLED);
      assertFutureState(stage2, COMPLETED_EXCEPTIONALLY); // through CancellationException
      assertFutureState(stage3, COMPLETED_EXCEPTIONALLY); // through CancellationException

      awaitTaskStateNOT(stage1State, RUNNING);
      if (tryCancelWithInterrupt && supportsInterrupt) {
         assertTaskState(stage1State, INTERRUPTED);
      } else {
         assertTaskState(stage1State, DONE); // stage1 was execute (but because it was cancelled the result is not accessible)
      }
      assertTaskState(stage2State, NEW); // stage2 was cancelled
      assertTaskState(stage3State, NEW); // stage3 was cancelled
   }

   void testMultipleStagesCancelUpstream(final Function<Runnable, CompletableFuture<?>> futureFactory, final boolean supportsUpstreamCancel,
         final boolean supportsInterrupt) throws InterruptedException {
      testMultipleStagesCancelUpstream(futureFactory, supportsUpstreamCancel, supportsInterrupt, false);
      testMultipleStagesCancelUpstream(futureFactory, supportsUpstreamCancel, supportsInterrupt, true);
   }

   void testMultipleStagesCancelUpstream(final Function<Runnable, CompletableFuture<?>> futureFactory, final boolean cancelsPrecedingStages,
         final boolean supportsInterrupt, final boolean tryCancelWithInterrupt) throws InterruptedException {
      final var stage1State = MutableObservableRef.of(NEW);
      final var stage1 = futureFactory.apply(createTask(stage1State, 500));

      final var stage2State = MutableObservableRef.of(NEW);
      final var stage2 = stage1.thenRun(createTask(stage2State, 500));

      final var stage3State = MutableRef.of(NEW);
      final var stage3 = stage2.thenRun(createTask(stage2State, 500));

      awaitTaskState(stage1State, RUNNING);

      assertFutureState(stage1, INCOMPLETE);
      assertFutureState(stage2, INCOMPLETE);
      assertFutureState(stage3, INCOMPLETE);
      assertTaskState(stage1State, RUNNING);
      assertTaskState(stage2State, NEW);
      assertTaskState(stage3State, NEW);

      stage3.cancel(tryCancelWithInterrupt);
      awaitFutureState(stage3, CANCELLED);

      if (cancelsPrecedingStages) {
         assertFutureState(stage1, CANCELLED);
         assertFutureState(stage2, CANCELLED);

         awaitTaskStateNOT(stage1State, RUNNING);

         assertTaskState(stage1State, tryCancelWithInterrupt && supportsInterrupt //
               ? INTERRUPTED // stage1 was interrupted
               : DONE); // stage1 was execute (but because it was cancelled the result is not accessible)
         assertTaskState(stage2State, NEW); // was cancelled
      } else {
         assertFutureState(stage1, INCOMPLETE);
         assertFutureState(stage2, INCOMPLETE);

         awaitTaskStateNOT(stage1State, RUNNING);
         awaitTaskState(stage2State, DONE);

         assertFutureState(stage1, COMPLETED);
         assertFutureState(stage2, COMPLETED);
         assertFutureState(stage3, CANCELLED);

         assertTaskState(stage1State, DONE); // stage1 was execute
         assertTaskState(stage2State, DONE); // stage2 was execute
      }
      assertTaskState(stage3State, NEW); // stage3 was cancelled
   }

   void testSingleStageCancel(final Function<Runnable, Future<?>> futureFactory, final boolean supportsInterrupt)
         throws InterruptedException {
      // test cancel
      {
         final var stage1State = MutableObservableRef.of(NEW);
         final var stage1 = futureFactory.apply(createTask(stage1State, 500));

         stage1State.await(RUNNING, MAX_WAIT_SECS, TimeUnit.SECONDS);

         stage1.cancel(false);

         awaitFutureState(stage1, CANCELLED);
         assertTaskState(stage1State, RUNNING);

         awaitTaskStateNOT(stage1State, RUNNING);
         assertTaskState(stage1State, DONE);
      }

      // test interrupt
      {
         final var stage1State = MutableObservableRef.of(NEW);
         final var stage1 = futureFactory.apply(createTask(stage1State, 500));

         awaitTaskState(stage1State, RUNNING);

         stage1.cancel(true); // interrupt
         awaitFutureState(stage1, CANCELLED);

         awaitTaskStateNOT(stage1State, RUNNING);
         assertTaskState(stage1State, supportsInterrupt ? INTERRUPTED : DONE);
      }

      // test interrupt after cancel
      {
         final var stage1State = MutableObservableRef.of(NEW);
         final var stage1 = futureFactory.apply(createTask(stage1State, 500));

         awaitTaskState(stage1State, RUNNING);

         stage1.cancel(false); // cancel

         awaitFutureState(stage1, CANCELLED);
         assertTaskState(stage1State, RUNNING);

         stage1.cancel(true); // ignored because already cancelled
         Threads.sleep(100);

         awaitTaskStateNOT(stage1State, RUNNING);
         assertTaskState(stage1State, DONE);
      }
   }
}
