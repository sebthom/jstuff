/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.queue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortedUniqueIntQueueTest {

   @Test
   public void testoffer() {
      final var q = new SortedUniqueIntQueue(0);
      assertThat(q.isEmpty()).isTrue();
      assertThat(q.isNotEmpty()).isFalse();
      assertThat(q.toString()).isEqualTo("[]");
      assertThat(q.size()).isZero();
      assertThat(q.items.length).isZero();
      assertThat(q.headIndex).isEqualTo(-1);

      assertThat(q.headIndex).isEqualTo(-1);
      assertThat(q.peek(-1)).isEqualTo(-1);
      assertThat(q.isFirstElement(5)).isFalse();
      assertThat(q.contains(5)).isFalse();

      assertThat(q.offer(5)).isTrue();
      assertThat(q.offer(5)).isFalse();
      assertThat(q.isEmpty()).isFalse();
      assertThat(q.isNotEmpty()).isTrue();
      assertThat(q.toString()).isEqualTo("[5]");
      assertThat(q.size()).isEqualTo(1);
      assertThat(q.items.length).isEqualTo(1);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(5);
      assertThat(q.isFirstElement(5)).isTrue();
      assertThat(q.contains(5)).isTrue();

      assertThat(q.offer(7)).isTrue();
      assertThat(q.toString()).isEqualTo("[5,7]");
      assertThat(q.size()).isEqualTo(2);
      assertThat(q.items.length).isEqualTo(2);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(5);
      assertThat(q.isFirstElement(5)).isTrue();
      assertThat(q.contains(5)).isTrue();
      assertThat(q.contains(7)).isTrue();

      assertThat(q.offer(3)).isTrue();
      assertThat(q.toString()).isEqualTo("[3,5,7]");
      assertThat(q.size()).isEqualTo(3);
      assertThat(q.items.length).isEqualTo(3);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(3);
      assertThat(q.isFirstElement(5)).isFalse();
      assertThat(q.contains(3)).isTrue();
      assertThat(q.contains(5)).isTrue();
      assertThat(q.contains(7)).isTrue();

      assertThat(q.offer(6)).isTrue();
      assertThat(q.toString()).isEqualTo("[3,5,6,7]");
      assertThat(q.size()).isEqualTo(4);
      assertThat(q.items.length).isEqualTo(4);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(3);

      assertThat(q.offer(4)).isTrue();
      assertThat(q.toString()).isEqualTo("[3,4,5,6,7]");
      assertThat(q.size()).isEqualTo(5);
      assertThat(q.items.length).isEqualTo(6);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(3);
   }

   @Test
   public void testofferToHead() {
      // tests insert at head on index 0 if underlying array has no capacity left
      var q = new SortedUniqueIntQueue(1);
      q.offer(4);
      assertThat(q.toString()).isEqualTo("[4]");
      assertThat(q.size()).isEqualTo(1);
      assertThat(q.items.length).isEqualTo(1);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(4);

      q.offer(2);
      assertThat(q.toString()).isEqualTo("[2,4]");
      assertThat(q.size()).isEqualTo(2);
      assertThat(q.items.length).isEqualTo(2);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(2);

      // tests insert at head on index 0 if underlying array still has capacity
      q = new SortedUniqueIntQueue(4);
      q.offer(4);
      q.offer(2);
      assertThat(q.toString()).isEqualTo("[2,4]");
      assertThat(q.size()).isEqualTo(2);
      assertThat(q.items.length).isEqualTo(4);
      assertThat(q.headIndex).isZero();
      assertThat(q.peek(-1)).isEqualTo(2);
   }

   @Test
   public void testTakeFromNonEmptyQueue() throws InterruptedException {
      final var q = new SortedUniqueIntQueue(4);
      q.offer(3);
      q.offer(4);
      q.offer(5);
      q.offer(6);
      assertThat(q.toString()).isEqualTo("[3,4,5,6]");
      assertThat(q.headIndex).isZero();
      assertThat(q.size()).isEqualTo(4);

      assertThat(q.take()).isEqualTo(3);
      assertThat(q.toString()).isEqualTo("[4,5,6]");
      assertThat(q.headIndex).isEqualTo(1);
      assertThat(q.size()).isEqualTo(3);

      assertThat(q.take()).isEqualTo(4);
      assertThat(q.toString()).isEqualTo("[5,6]");
      assertThat(q.headIndex).isEqualTo(2);
      assertThat(q.size()).isEqualTo(2);
      assertThat(q.items.length).isEqualTo(4);

      q.offer(2);
      assertThat(q.toString()).isEqualTo("[2,5,6]");
      assertThat(q.headIndex).isEqualTo(1);
      assertThat(q.size()).isEqualTo(3);
      assertThat(q.items.length).isEqualTo(4);

      q.offer(1);
      assertThat(q.toString()).isEqualTo("[1,2,5,6]");
      assertThat(q.headIndex).isZero();
      assertThat(q.size()).isEqualTo(4);
      assertThat(q.items.length).isEqualTo(4);

      // empty the queue
      assertThat(q.take()).isEqualTo(1);
      assertThat(q.headIndex).isEqualTo(1);
      assertThat(q.take()).isEqualTo(2);
      assertThat(q.headIndex).isEqualTo(2);
      assertThat(q.take()).isEqualTo(5);
      assertThat(q.headIndex).isEqualTo(3);
      assertThat(q.take()).isEqualTo(6);
      assertThat(q.size()).isZero();
      assertThat(q.headIndex).isEqualTo(-1);
   }

   @Test
   public void testTakeFromEmptyQueue() throws InterruptedException {
      final var q = new SortedUniqueIntQueue(-1);
      assertThat(q.items.length).isZero();

      new Timer().schedule(new TimerTask() {
         @Override
         public void run() {
            q.offer(5);
         }
      }, 500L);

      assertThat(q.take()).isEqualTo(5);
   }
}
