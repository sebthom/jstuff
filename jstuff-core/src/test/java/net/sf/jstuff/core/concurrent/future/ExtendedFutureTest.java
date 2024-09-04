/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import static net.sf.jstuff.core.concurrent.future.AbstractFutureTest.TaskState.INTERRUPTED;
import static net.sf.jstuff.core.concurrent.future.FutureState.CANCELLED;
import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import net.sf.jstuff.core.ref.MutableObservableRef;
import net.sf.jstuff.core.ref.MutableRef;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ExtendedFutureTest extends AbstractFutureTest {

   @Test
   public void testCompletedFuture() {
      testCompletedFuture(ExtendedFuture::new, ExtendedFuture::completedFuture);
      testCompletedFuture(ExtendedFuture::new, value -> ExtendedFuture.from(CompletableFuture.completedFuture(value), true));
   }

   @Test
   public void testCopy() {
      testCopy(ExtendedFuture::new);
   }

   @Test
   public void testMultipleStagesCancelDownstream() throws InterruptedException {
      testMultipleStagesCancelDownstream(ExtendedFuture::runAsync, true);
      testMultipleStagesCancelDownstream(runnable -> ExtendedFuture.from(CompletableFuture.completedFuture(null), true).thenRunAsync(
         runnable), true);
   }

   @Test
   public void testMultipleStagesCancelUpstream() throws InterruptedException {
      final var parent = new ExtendedFuture<>(false);
      final var parentCancellable = parent.asCancellableByDependents(true);
      final var parentUncancellable = parentCancellable.asCancellableByDependents(false);
      assertThat(parent.isCancellableByDependents()).isFalse();
      assertThat(parentCancellable.isCancellableByDependents()).isTrue();
      assertThat(parentUncancellable.isCancellableByDependents()).isFalse();

      var dependent = parent.thenRun(() -> { /**/ });
      dependent.cancel(true);
      awaitFutureState(dependent, CANCELLED);
      awaitFutureState(parent, FutureState.INCOMPLETE);

      dependent = parentUncancellable.thenRun(() -> { /**/ });
      dependent.cancel(true);
      awaitFutureState(dependent, CANCELLED);
      awaitFutureState(parent, FutureState.INCOMPLETE);
      awaitFutureState(parentUncancellable, FutureState.INCOMPLETE);

      dependent = parentCancellable.thenRun(() -> { /**/ });
      dependent.cancel(true);
      awaitFutureState(dependent, CANCELLED);
      awaitFutureState(parentCancellable, CANCELLED);
      awaitFutureState(parent, CANCELLED);

      testMultipleStagesCancelUpstream(r -> ExtendedFuture.runAsync(r).asCancellableByDependents(true), true, true);
      testMultipleStagesCancelUpstream(runnable -> ExtendedFuture.from(CompletableFuture.completedFuture(null), true).thenRunAsync(
         runnable), true, true);

      testMultipleStagesCancelUpstream(r -> ExtendedFuture.runAsync(r).asCancellableByDependents(false), false, true);
      testMultipleStagesCancelUpstream(runnable -> ExtendedFuture.from(CompletableFuture.completedFuture(null), false).thenRunAsync(
         runnable), false, true);
   }

   @Test
   public void testSingleStageCancel() throws InterruptedException {
      testSingleStageCancel(ExtendedFuture::runAsync, true);
      testSingleStageCancel(runnable -> ExtendedFuture.from(CompletableFuture.completedFuture(null), true).thenRunAsync(runnable), true);
   }

   @Test
   public void testNestedStageCancel() throws InterruptedException {
      final var stage1NestedState = MutableObservableRef.of(TaskState.NEW);
      final MutableRef<@Nullable ExtendedFuture<?>> stage1Nested = MutableRef.create();
      final var stage1 = ExtendedFuture //
         .completedFuture(null) //
         .thenCompose(result -> {
            final var nestedStage = ExtendedFuture //
               .runAsync(createTask(stage1NestedState, 1000)) //
               .asCancellableByDependents(true);
            stage1Nested.set(nestedStage);
            return nestedStage;
         });

      stage1NestedState.await(state -> state != TaskState.NEW, 300, TimeUnit.MILLISECONDS);

      stage1.cancel(true);

      awaitFutureState(stage1, CANCELLED);
      awaitFutureState(stage1Nested.get(), CANCELLED);
      awaitTaskState(stage1NestedState, INTERRUPTED);
   }

   @Test
   @SuppressWarnings("null")
   public void testAsReadOnly_withThrowOnMutationAttempt() {
      final var originalFuture = new ExtendedFuture<String>();
      final var readOnlyFuture = originalFuture.asReadOnly(true); // throwOnMutationAttempt = false

      assertThatThrownBy(() -> readOnlyFuture.complete("Hello")) //
         .isInstanceOf(UnsupportedOperationException.class) //
         .hasMessageContaining("is read-only");

      assertThatThrownBy(() -> readOnlyFuture.cancel(true)) //
         .isInstanceOf(UnsupportedOperationException.class) //
         .hasMessageContaining("is read-only");

      assertThatThrownBy(() -> readOnlyFuture.completeExceptionally(new RuntimeException("Test Exception"))) //
         .isInstanceOf(UnsupportedOperationException.class) //
         .hasMessageContaining("is read-only");

      // ensure read-only future can still perform non-mutating operations like get() or join()
      originalFuture.complete("Hello");
      assertThat(readOnlyFuture.join()).isEqualTo("Hello");
   }

   @Test
   public void testAsReadOnly_withSilentMutation() {
      final var originalFuture = new ExtendedFuture<String>();
      final var readOnlyFuture = originalFuture.asReadOnly(false); // throwOnMutationAttempt = false

      assertThat(readOnlyFuture.complete("Hello")).isFalse();
      assertThat(readOnlyFuture.cancel(true)).isFalse();
      assertThat(readOnlyFuture.completeExceptionally(new RuntimeException("Test Exception"))).isFalse();

      // ensure read-only future can still perform non-mutating operations like get() or join()
      originalFuture.complete("World");
      assertThat(readOnlyFuture.join()).isEqualTo("World");
   }

   @Test
   public void testWithDefaultExecutor() {
      final var defaultExecutor1 = Executors.newSingleThreadExecutor();
      final var defaultExecutor2 = Executors.newSingleThreadExecutor();
      final var originalFuture = new ExtendedFuture<String>(defaultExecutor1);
      final var sameFuture = originalFuture.withDefaultExecutor(defaultExecutor1);
      final var newFuture = originalFuture.withDefaultExecutor(defaultExecutor2);

      assertThat(sameFuture).isSameAs(originalFuture);
      assertThat(newFuture).isNotSameAs(originalFuture);
      assertThat(newFuture.defaultExecutor()).isSameAs(defaultExecutor2); // Assuming a getter

      defaultExecutor1.shutdown();
      defaultExecutor2.shutdown();
   }
}
