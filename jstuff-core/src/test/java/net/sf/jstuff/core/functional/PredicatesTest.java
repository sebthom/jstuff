/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Predicates.*;
import static net.sf.jstuff.core.functional.Predicates.not;
import static org.assertj.core.api.Assertions.*;

import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import net.sf.jstuff.core.functional.Predicates.Contains;
import net.sf.jstuff.core.functional.Predicates.EndingWith;
import net.sf.jstuff.core.functional.Predicates.Property;
import net.sf.jstuff.core.functional.Predicates.StartingWith;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PredicatesTest {

   @Test
   public void testPredicateChaining() {
      {
         final Predicate<@Nullable String> a = startingWith("#").and2(endingWith("#"));
         assertThat(a.test(null)).isFalse();
         assertThat(a.test("#foo")).isFalse();
         assertThat(a.test("foo#")).isFalse();
         assertThat(a.test("#foo#")).isTrue();
      }

      {
         final Predicate<@Nullable String> a = startingWith("#").or2(endingWith("#"));
         assertThat(a.test(null)).isFalse();
         assertThat(a.test("#foo")).isTrue();
         assertThat(a.test("foo#")).isTrue();
         assertThat(a.test("#foo#")).isTrue();
      }

      {
         final Predicate<@Nullable Integer> a = notNull().and2(greaterThan(10)).and2(lessThan(20));
         assertThat(a.test(null)).isFalse();
         assertThat(a.test(2)).isFalse();
         assertThat(a.test(19)).isTrue();
         assertThat(a.test(21)).isFalse();
      }

      {
         final Predicate<@Nullable Long> a = notNull().and2(greaterThan(20L).or2(lessThan(10L)).or2(equalTo(12L)));
         assertThat(a.test(null)).isFalse();
         assertThat(a.test(21L)).isTrue();
         assertThat(a.test(19L)).isFalse();
         assertThat(a.test(11L)).isFalse();
         assertThat(a.test(12L)).isTrue();
         assertThat(a.test(9L)).isTrue();
      }
   }

   @Test
   public void testContains() {
      final Contains<@Nullable String> a = contains("oo");
      assertThat(a.test(null)).isFalse();
      assertThat(a.test("")).isFalse();
      assertThat(a.test("foo")).isTrue();

      final Predicate<@Nullable String> a2 = not(a);
      assertThat(a2.test(null)).isTrue();
      assertThat(a2.test("")).isTrue();
      assertThat(a2.test("foo")).isFalse();

      final Predicate<@Nullable String> a3 = a.ignoreCase();
      assertThat(a3.test(null)).isFalse();
      assertThat(a3.test("")).isFalse();
      assertThat(a3.test("FOO")).isTrue();
   }

   @Test
   public void testEndingWith() {
      final EndingWith<@Nullable String> a = endingWith("o#");
      assertThat(a.test(null)).isFalse();
      assertThat(a.test("#foo")).isFalse();
      assertThat(a.test("foo")).isFalse();
      assertThat(a.test("foo#")).isTrue();

      final Predicate<@Nullable String> a2 = not(a);
      assertThat(a2.test(null)).isTrue();
      assertThat(a2.test("#foo")).isTrue();
      assertThat(a2.test("foo")).isTrue();
      assertThat(a2.test("foo#")).isFalse();

      final Predicate<@Nullable String> a3 = a.ignoreCase();
      assertThat(a3.test(null)).isFalse();
      assertThat(a3.test("#FOO")).isFalse();
      assertThat(a3.test("FOO")).isFalse();
      assertThat(a3.test("FOO#")).isTrue();
   }

   @Test
   public void testEqualTo() {
      final Predicate<@Nullable String> a = equalTo("123");
      assertThat(a.test(null)).isFalse();
      assertThat(a.test("1234")).isFalse();
      assertThat(a.test("123")).isTrue();

      final Predicate<@Nullable String> a2 = not(a);
      assertThat(a2.test(null)).isTrue();
      assertThat(a2.test("1234")).isTrue();
      assertThat(a2.test("123")).isFalse();
   }

   @Test
   public void testGreaterThan() {
      final Predicate<@Nullable Integer> a = greaterThan(10);
      assertThat(a.test(null)).isFalse();
      assertThat(a.test(9)).isFalse();
      assertThat(a.test(10)).isFalse();
      assertThat(a.test(11)).isTrue();

      final Predicate<@Nullable Integer> a2 = not(a);
      assertThat(a2.test(null)).isTrue();
      assertThat(a2.test(9)).isTrue();
      assertThat(a2.test(10)).isTrue();
      assertThat(a2.test(11)).isFalse();
   }

   @Test
   public void testLessThan() {
      final Predicate<@Nullable Integer> a = lessThan(10);
      assertThat(a.test(null)).isTrue();
      assertThat(a.test(9)).isTrue();
      assertThat(a.test(10)).isFalse();
      assertThat(a.test(11)).isFalse();

      final Predicate<@Nullable Integer> a2 = not(a);
      assertThat(a2.test(null)).isFalse();
      assertThat(a2.test(9)).isFalse();
      assertThat(a2.test(10)).isTrue();
      assertThat(a2.test(11)).isTrue();
   }

   @Test
   public void testNonNull() {
      final Predicate<@Nullable Object> a = notNull();
      assertThat(a.test(null)).isFalse();
      assertThat(a.test("")).isTrue();

      final Predicate<@Nullable Object> a2 = not(a);
      assertThat(a2.test(null)).isTrue();
      assertThat(a2.test("")).isFalse();
   }

   @Test
   public void testNull() {
      final Predicate<@Nullable Object> a = isNull();
      assertThat(a.test(null)).isTrue();
      assertThat(a.test("")).isFalse();

      final Predicate<@Nullable Object> a2 = not(a);
      assertThat(a2.test(null)).isFalse();
      assertThat(a2.test("")).isTrue();
   }

   @Test
   public void testProperty() {
      class Entity {
         @SuppressWarnings("unused")
         @Nullable
         String name;

         @SuppressWarnings("unused")
         @Nullable
         Entity parent;
      }

      final Property<Entity, String> a = property("name", equalTo("foobar"));

      final var e = new Entity();

      e.name = "foobar";
      assertThat(a.test(e)).isTrue();

      e.name = "blub";
      assertThat(a.test(e)).isFalse();

      final Property<Entity, String> a2 = property("parent.name", equalTo("foobar"));

      final var parent = e.parent = new Entity();
      parent.name = "blub";
      assertThat(a2.test(e)).isFalse();

      parent.name = "foobar";
      assertThat(a2.test(e)).isTrue();

      e.parent = null;
      assertThat(a2.test(e)).isFalse();
   }

   @Test
   public void testStartingWith() {
      final StartingWith<@Nullable String> a = startingWith("#f");
      assertThat(a.test(null)).isFalse();
      assertThat(a.test("#foo")).isTrue();
      assertThat(a.test("foo")).isFalse();
      assertThat(a.test("foo#")).isFalse();

      final Predicate<@Nullable String> a2 = not(a);
      assertThat(a2.test(null)).isTrue();
      assertThat(a2.test("#foo")).isFalse();
      assertThat(a2.test("foo")).isTrue();
      assertThat(a2.test("foo#")).isTrue();

      final Predicate<@Nullable String> a3 = a.ignoreCase();
      assertThat(a3.test(null)).isFalse();
      assertThat(a3.test("#FOO")).isTrue();
      assertThat(a3.test("FOO")).isFalse();
      assertThat(a3.test("FOO#")).isFalse();
   }
}
