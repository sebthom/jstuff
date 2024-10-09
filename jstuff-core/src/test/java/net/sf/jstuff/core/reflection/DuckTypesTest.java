/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class DuckTypesTest {

   private interface Duck {
      void walk();
   }

   static class DuckLike {
      int count = 0;

      public void walk() {
         count++;
      }
   }

   @Test
   void testDuckTyping() {
      assertThat(DuckTypes.isDuckType(new Object(), Duck.class)).isFalse();

      final var duckLike = new DuckLike();
      assertThat(DuckTypes.isDuckType(duckLike, Duck.class)).isTrue();
      assertThat(duckLike.count).isZero();
      final Duck duckTyped = DuckTypes.duckType(duckLike, Duck.class);
      duckTyped.walk();
      assertThat(duckLike.count).isEqualTo(1);
   }

   @Test
   void testDuckTypingWithAnonymousInnerClass() {
      final var count = new AtomicInteger();
      final var duckLike = new Object() {
         @SuppressWarnings("unused")
         public void walk() {
            count.incrementAndGet();
         }
      };
      assertThat(count.get()).isZero();
      final Duck duckTyped = DuckTypes.duckType(duckLike, Duck.class);
      duckTyped.walk();
      assertThat(count.get()).isEqualTo(1);
   }
}
