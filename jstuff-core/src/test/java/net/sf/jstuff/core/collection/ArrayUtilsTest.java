/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class ArrayUtilsTest {

   @Test
   void testAddAll() {
      assertThat(ArrayUtils.addAll(new String[] {"a", "b"}, List.of("c", "d"))).isEqualTo(new String[] {"a", "b", "c", "d"});
   }

   @Test
   void testFilter() {
      final String[] filtered = ArrayUtils.filter(obj -> "foo".equals(obj) || "bar".equals(obj), "dog", "foo", "bar", "cat");

      assertThat(filtered).hasSize(2);
      assertThat(filtered[0]).isEqualTo("foo");
      assertThat(filtered[1]).isEqualTo("bar");
   }

   @Test
   void testIntersect() {
      assertThat(ArrayUtils.intersect((Object[]) null)).isEmpty();
      assertThat(ArrayUtils.intersect(ArrayUtils.EMPTY_OBJECT_ARRAY)).isEmpty();
      assertThat(ArrayUtils.intersect(ArrayUtils.EMPTY_OBJECT_ARRAY, null)).isEmpty();

      final String[] arr1 = {"foo", "bar", "dog"};
      final String[] arr2 = {"cat", "bar", "foo"};

      assertThat(ArrayUtils.intersect(arr1, arr2)).hasSize(2);
      assertThat(ArrayUtils.intersect(arr1, arr2)[0]).isEqualTo("foo");
      assertThat(ArrayUtils.intersect(arr1, arr2)[1]).isEqualTo("bar");
   }

   @Test
   void testToByteArray() {
      final String str = RandomStringUtils.random(250);
      assertThat(ArrayUtils.toByteArray(str.toCharArray(), StandardCharsets.UTF_8)).isEqualTo(str.getBytes(StandardCharsets.UTF_8));
   }
}
