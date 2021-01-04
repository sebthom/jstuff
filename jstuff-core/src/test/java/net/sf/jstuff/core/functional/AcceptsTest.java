/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Accepts.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import net.sf.jstuff.core.functional.Accepts.Contains;
import net.sf.jstuff.core.functional.Accepts.EndingWith;
import net.sf.jstuff.core.functional.Accepts.Property;
import net.sf.jstuff.core.functional.Accepts.StartingWith;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AcceptsTest {

   @Test
   public void testAcceptChaining() {
      {
         final Accept<String> a = startingWith("#").and(endingWith("#"));
         assertThat(a.accept(null)).isFalse();
         assertThat(a.accept("#foo")).isFalse();
         assertThat(a.accept("foo#")).isFalse();
         assertThat(a.accept("#foo#")).isTrue();
      }

      {
         final Accept<String> a = startingWith("#").or(endingWith("#"));
         assertThat(a.accept(null)).isFalse();
         assertThat(a.accept("#foo")).isTrue();
         assertThat(a.accept("foo#")).isTrue();
         assertThat(a.accept("#foo#")).isTrue();
      }

      {
         final Accept<Integer> a = notNull().and(greaterThan(10)).and(lessThan(20));
         assertThat(a.accept(null)).isFalse();
         assertThat(a.accept(2)).isFalse();
         assertThat(a.accept(19)).isTrue();
         assertThat(a.accept(21)).isFalse();
      }

      {
         final Accept<Long> a = notNull(). //
            and( //
               greaterThan(20L).or(lessThan(10L)).or(equalTo(12L)) //
            );
         assertThat(a.accept(null)).isFalse();
         assertThat(a.accept(21L)).isTrue();
         assertThat(a.accept(19L)).isFalse();
         assertThat(a.accept(11L)).isFalse();
         assertThat(a.accept(12L)).isTrue();
         assertThat(a.accept(9L)).isTrue();
      }
   }

   @Test
   public void testContains() {
      final Contains<String> a = contains("oo");
      assertThat(a.accept(null)).isFalse();
      assertThat(a.accept("")).isFalse();
      assertThat(a.accept("foo")).isTrue();

      final Accept<String> a2 = not(a);
      assertThat(a2.accept(null)).isTrue();
      assertThat(a2.accept("")).isTrue();
      assertThat(a2.accept("foo")).isFalse();

      final Accept<String> a3 = a.ignoreCase();
      assertThat(a3.accept(null)).isFalse();
      assertThat(a3.accept("")).isFalse();
      assertThat(a3.accept("FOO")).isTrue();
   }

   @Test
   public void testEndingWith() {
      final EndingWith<String> a = endingWith("o#");
      assertThat(a.accept(null)).isFalse();
      assertThat(a.accept("#foo")).isFalse();
      assertThat(a.accept("foo")).isFalse();
      assertThat(a.accept("foo#")).isTrue();

      final Accept<String> a2 = not(a);
      assertThat(a2.accept(null)).isTrue();
      assertThat(a2.accept("#foo")).isTrue();
      assertThat(a2.accept("foo")).isTrue();
      assertThat(a2.accept("foo#")).isFalse();

      final Accept<String> a3 = a.ignoreCase();
      assertThat(a3.accept(null)).isFalse();
      assertThat(a3.accept("#FOO")).isFalse();
      assertThat(a3.accept("FOO")).isFalse();
      assertThat(a3.accept("FOO#")).isTrue();
   }

   @Test
   public void testEqualTo() {
      final Accept<String> a = equalTo("123");
      assertThat(a.accept(null)).isFalse();
      assertThat(a.accept("1234")).isFalse();
      assertThat(a.accept("123")).isTrue();

      final Accept<String> a2 = not(a);
      assertThat(a2.accept(null)).isTrue();
      assertThat(a2.accept("1234")).isTrue();
      assertThat(a2.accept("123")).isFalse();
   }

   @Test
   public void testGreaterThan() {
      final Accept<Integer> a = greaterThan(10);
      assertThat(a.accept(null)).isFalse();
      assertThat(a.accept(9)).isFalse();
      assertThat(a.accept(10)).isFalse();
      assertThat(a.accept(11)).isTrue();

      final Accept<Integer> a2 = not(a);
      assertThat(a2.accept(null)).isTrue();
      assertThat(a2.accept(9)).isTrue();
      assertThat(a2.accept(10)).isTrue();
      assertThat(a2.accept(11)).isFalse();
   }

   @Test
   public void testLessThan() {
      final Accept<Integer> a = lessThan(10);
      assertThat(a.accept(null)).isFalse();
      assertThat(a.accept(9)).isTrue();
      assertThat(a.accept(10)).isFalse();
      assertThat(a.accept(11)).isFalse();

      final Accept<Integer> a2 = not(a);
      assertThat(a2.accept(null)).isTrue();
      assertThat(a2.accept(9)).isFalse();
      assertThat(a2.accept(10)).isTrue();
      assertThat(a2.accept(11)).isTrue();
   }

   @Test
   public void testNonNull() {
      final Accept<Object> a = notNull();
      assertThat(a.accept(null)).isFalse();
      assertThat(a.accept("")).isTrue();

      final Accept<Object> a2 = not(a);
      assertThat(a2.accept(null)).isTrue();
      assertThat(a2.accept("")).isFalse();
   }

   @Test
   public void testNull() {
      final Accept<Object> a = isNull();
      assertThat(a.accept(null)).isTrue();
      assertThat(a.accept("")).isFalse();

      final Accept<Object> a2 = not(a);
      assertThat(a2.accept(null)).isFalse();
      assertThat(a2.accept("")).isTrue();
   }

   @Test
   public void testProperty() {
      class Entity {
         @SuppressWarnings("unused")
         String name;
         Entity parent;
      }

      final Property<Entity, String> a = property("name", equalTo("foobar"));

      final Entity e = new Entity();

      e.name = "foobar";
      assertThat(a.accept(e)).isTrue();

      e.name = "blub";
      assertThat(a.accept(e)).isFalse();

      final Property<Entity, String> a2 = property("parent.name", equalTo("foobar"));

      e.parent = new Entity();

      e.parent.name = "blub";
      assertThat(a2.accept(e)).isFalse();

      e.parent.name = "foobar";
      assertThat(a2.accept(e)).isTrue();

      e.parent = null;
      assertThat(a2.accept(e)).isFalse();
   }

   @Test
   public void testStartingWith() {
      final StartingWith<String> a = startingWith("#f");
      assertThat(a.accept(null)).isFalse();
      assertThat(a.accept("#foo")).isTrue();
      assertThat(a.accept("foo")).isFalse();
      assertThat(a.accept("foo#")).isFalse();

      final Accept<String> a2 = not(a);
      assertThat(a2.accept(null)).isTrue();
      assertThat(a2.accept("#foo")).isFalse();
      assertThat(a2.accept("foo")).isTrue();
      assertThat(a2.accept("foo#")).isTrue();

      final Accept<String> a3 = a.ignoreCase();
      assertThat(a3.accept(null)).isFalse();
      assertThat(a3.accept("#FOO")).isTrue();
      assertThat(a3.accept("FOO")).isFalse();
      assertThat(a3.accept("FOO#")).isFalse();
   }
}
