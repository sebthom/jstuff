/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;

import org.junit.Test;

import net.sf.jstuff.core.reflection.DuckTypes;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SafeAwaitTest {

   @Test
   public void testAwait_condition_timeout() throws InterruptedException {
      final Condition condition = DuckTypes.duckType(new Object() {
         final long startAt = System.currentTimeMillis();

         @SuppressWarnings("unused")
         public boolean await(final long timeout, final TimeUnit unit) {
            Threads.sleep(unit.toMillis(timeout));
            return System.currentTimeMillis() - startAt > 500;
         }
      }, Condition.class);

      assertThat(SafeAwait.await(condition, 0)).isFalse();
      assertThat(SafeAwait.await(condition, 100)).isFalse();
      assertThat(SafeAwait.await(condition, 1_000)).isTrue();
   }

   @Test
   public void testAwait_waitObject_noTimeout() throws InterruptedException {
      final var objectWithState = new AtomicBoolean(false);

      CompletableFuture.runAsync(() -> {
         try {
            TimeUnit.MILLISECONDS.sleep(500);
            synchronized (objectWithState) {
               objectWithState.set(true);
               objectWithState.notifyAll();
            }
         } catch (final InterruptedException ex) {
            Threads.handleInterruptedException(ex);
         }
      });

      assertThat(objectWithState.get()).isFalse();
      SafeAwait.await(objectWithState::get, objectWithState);
      assertThat(objectWithState.get()).isTrue();
   }

   @Test
   public void testAwait_waitObject_timeout() throws InterruptedException {
      final var objectWithState = new AtomicBoolean(false);

      CompletableFuture.runAsync(() -> {
         try {
            TimeUnit.MILLISECONDS.sleep(500);
            synchronized (objectWithState) {
               objectWithState.set(true);
               objectWithState.notifyAll();
            }
         } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      });

      assertThat(objectWithState.get()).isFalse();
      assertThat(SafeAwait.await(objectWithState::get, objectWithState, 0)).isFalse();
      assertThat(SafeAwait.await(objectWithState::get, objectWithState, 100)).isFalse();
      assertThat(SafeAwait.await(objectWithState::get, objectWithState, 1_000)).isTrue();
      assertThat(objectWithState.get()).isTrue();
   }
}
