/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class EvictingDequeTest {

   @Test
   void testEvictingDeque() {
      final var q = new EvictingDeque<String>(3);
      assertThat(q).isEmpty();
      assertThat(q.remainingCapacity()).isEqualTo(3);
      q.push("a");
      q.push("b");
      q.push("c");
      assertThat(q).hasSize(3);
      assertThat(q.remainingCapacity()).isZero();
      assertThat(q).containsAll(Arrays.asList("a", "b", "c"));

      q.push("d");
      assertThat(q).hasSize(3);
      assertThat(q.remainingCapacity()).isZero();
      assertThat(q).containsAll(Arrays.asList("b", "c", "d"));
      assertThat(q).doesNotContain("a");

      q.offer("e");
      assertThat(q).hasSize(3);
      assertThat(q.remainingCapacity()).isZero();
      assertThat(q).containsAll(Arrays.asList("e", "b", "c"));
      assertThat(q).doesNotContain("d");
   }
}
