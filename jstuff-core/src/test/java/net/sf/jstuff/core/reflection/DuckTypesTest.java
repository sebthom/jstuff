/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DuckTypesTest {

   private interface Duck {
      void walk();
   }

   public static class DuckLike {
      public int count = 0;

      public void walk() {
         count++;
      }
   }

   @Test
   public void testDuckTyping() {
      assertThat(DuckTypes.isDuckType(new Object(), Duck.class)).isFalse();

      final DuckLike duckLike = new DuckLike();
      assertThat(DuckTypes.isDuckType(duckLike, Duck.class)).isTrue();
      assertThat(duckLike.count).isZero();
      final Duck duckTyped = DuckTypes.duckType(duckLike, Duck.class);
      duckTyped.walk();
      assertThat(duckLike.count).isEqualTo(1);
   }

   @Test
   public void testDuckTypingWithAnonymousInnerClass() {
      final AtomicInteger count = new AtomicInteger();
      final Object duckLike = new Object() {
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
