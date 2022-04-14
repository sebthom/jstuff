/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayUtilsTest {

   @Test
   public void testFilter() {
      final String[] filtered = ArrayUtils.filter(obj -> "foo".equals(obj) || "bar".equals(obj), "dog", "foo", "bar", "cat");

      assertThat(filtered).hasSize(2);
      assertThat(filtered[0]).isEqualTo("foo");
      assertThat(filtered[1]).isEqualTo("bar");
   }

   @Test
   public void testIntersect() {
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
   public void testToByteArray() {
      final String str = RandomStringUtils.random(250);
      assertThat(ArrayUtils.toByteArray(str.toCharArray(), StandardCharsets.UTF_8)).isEqualTo(str.getBytes(StandardCharsets.UTF_8));
   }
}
