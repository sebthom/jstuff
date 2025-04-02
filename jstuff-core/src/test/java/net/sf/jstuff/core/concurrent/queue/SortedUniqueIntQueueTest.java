/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.queue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class SortedUniqueIntQueueTest {

   @Test
   void testoffer() {
      final var q = new SortedUniqueIntQueue(0);
      assertThat(q.isEmpty()).isTrue();
      assertThat(q.isNotEmpty()).isFalse();
      assertThat(q).hasToString("[]");
      assertThat(q.size()).isZero();
      assertThat(q.items).isEmpty();
      assertThat(q.headIndex).isEqualTo(-1);

      assertThat(q.headIndex).isEqualTo(-1);
      assertThat(q.peek(-1)).isEqualTo(-1);
      assertThat(q.isFirstElement(5)).isFalse();
      assertThat(q.contains(5)).isFalse();

      assertThat(q.offer(5)).isTrue();
      assertThat(q.offer(5)).isFalse();
      assertThat(q.isEmpty()).isFalse();
      assertThat(q.isNotEmpty()).isTrue();
      assertThat(q).hasToString("[5]");
      assertThat(q.size()).isEqualTo(1);
      assertThat(q.items).hasSize(1);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(5);
      assertThat(q.isFirstElement(5)).isTrue();
      assertThat(q.contains(5)).isTrue();

      assertThat(q.offer(7)).isTrue();
      assertThat(q).hasToString("[5,7]");
      assertThat(q.size()).isEqualTo(2);
      assertThat(q.items).hasSize(2);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(5);
      assertThat(q.isFirstElement(5)).isTrue();
      assertThat(q.contains(5)).isTrue();
      assertThat(q.contains(7)).isTrue();

      assertThat(q.offer(3)).isTrue();
      assertThat(q).hasToString("[3,5,7]");
      assertThat(q.size()).isEqualTo(3);
      assertThat(q.items).hasSize(3);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(3);
      assertThat(q.isFirstElement(5)).isFalse();
      assertThat(q.contains(3)).isTrue();
      assertThat(q.contains(5)).isTrue();
      assertThat(q.contains(7)).isTrue();

      assertThat(q.offer(6)).isTrue();
      assertThat(q).hasToString("[3,5,6,7]");
      assertThat(q.size()).isEqualTo(4);
      assertThat(q.items).hasSize(4);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(3);

      assertThat(q.offer(4)).isTrue();
      assertThat(q).hasToString("[3,4,5,6,7]");
      assertThat(q.size()).isEqualTo(5);
      assertThat(q.items).hasSize(6);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(3);
   }

   @Test
   void testofferToHead() {
      // tests insert at head on index 0 if underlying array has no capacity left
      var q = new SortedUniqueIntQueue(1);

      q.offer(4);
      assertThat(q).hasToString("[4]");
      assertThat(q.size()).isEqualTo(1);
      assertThat(q.items).hasSize(1);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(4);

      q.offer(2);
      assertThat(q).hasToString("[2,4]");
      assertThat(q.size()).isEqualTo(2);
      assertThat(q.items).hasSize(2);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(2);

      // tests insert at head on index 0 if underlying array still has capacity
      q = new SortedUniqueIntQueue(4);
      q.offer(4);
      q.offer(2);
      assertThat(q).hasToString("[2,4]");
      assertThat(q.size()).isEqualTo(2);
      assertThat(q.items).hasSize(4);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(2);
   }

   @Test
   void testTakeFromNonEmptyQueue() throws InterruptedException {
      final var q = new SortedUniqueIntQueue(4);
      for (int i = 0; i < 2; i++) {
         q.offer(3);
         q.offer(4);
         q.offer(5);
         q.offer(6);
         assertThat(q).hasToString("[3,4,5,6]");
         assertThat(q.headIndex).isZero();
         assertThat(q.size()).isEqualTo(4);

         assertThat(q.take()).isEqualTo(3);
         assertThat(q).hasToString("[4,5,6]");
         assertThat(q.headIndex).isEqualTo(1);
         assertThat(q.size()).isEqualTo(3);

         assertThat(q.take()).isEqualTo(4);
         assertThat(q).hasToString("[5,6]");
         assertThat(q.headIndex).isEqualTo(2);
         assertThat(q.size()).isEqualTo(2);
         assertThat(q.items).hasSize(4);

         q.offer(2);
         assertThat(q).hasToString("[2,5,6]");
         assertThat(q.headIndex).isEqualTo(1);
         assertThat(q.size()).isEqualTo(3);
         assertThat(q.items).hasSize(4);

         q.offer(1);
         assertThat(q).hasToString("[1,2,5,6]");
         assertThat(q.headIndex).isZero();
         assertThat(q.size()).isEqualTo(4);
         assertThat(q.items).hasSize(4);

         // empty the queue
         assertThat(q.take()).isEqualTo(1);
         assertThat(q.headIndex).isEqualTo(1);
         assertThat(q.take()).isEqualTo(2);
         assertThat(q.headIndex).isEqualTo(2);
         assertThat(q.take()).isEqualTo(5);
         assertThat(q.headIndex).isEqualTo(3);
         assertThat(q.take()).isEqualTo(6);
         assertThat(q.size()).isZero();
      }
   }

   @Test
   void testTakeFromEmptyQueue() throws InterruptedException {
      final var q = new SortedUniqueIntQueue(-1);
      assertThat(q.items).isEmpty();

      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            q.offer(5);
         }
      }, 500);

      assertThat(q.take()).isEqualTo(5);
   }

   @Test
   void testTakeDoesNotBlockOffer() throws InterruptedException {
      final var queue = new SortedUniqueIntQueue(10);

      final var pollResult = new AtomicReference<@Nullable Integer>();
      final var pollStarts = new CountDownLatch(1);
      final var pollThread = new Thread(() -> {
         try {
            pollStarts.countDown();
            pollResult.set(queue.take());
         } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      });
      pollThread.start();
      pollStarts.await();
      Thread.sleep(100);

      final long offerStart = System.nanoTime();
      final boolean offered = queue.offer(42);
      final long offerDurationMS = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - offerStart);
      assertThat(offered).isTrue();
      assertThat(offerDurationMS).isLessThan(50);

      pollThread.join();

      assertThat(pollResult.get()).isEqualTo(42);
   }
}
