/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.collection.CollectionUtils.*;
import static net.sf.jstuff.core.collection.Sets.newLinkedHashSet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class CollectionUtilsTest {

   void testEnsureSize() {
      final var l = new ArrayList<@Nullable String>();
      ensureSize(l, 5);
      assertThat(l).hasSize(5);
      for (final var e : l) {
         assertThat(e).isNull();
      }
   }

   @Test
   @SuppressWarnings("unchecked")
   void testIntersectListt() {
      assertThat(intersect((List[]) null)).isEmpty();
      assertThat(intersect(Collections.emptyList())).isEmpty();
      assertThat(intersect(Collections.emptyList(), null)).isEmpty();

      final List<String> list1 = newArrayList("foo", "bar", "dog");
      final List<String> list2 = newArrayList("cat", "bar", "foo");

      assertThat(intersect(list1, list2)).hasSize(2);
      assertThat(intersect(list1, list2).iterator().next()).isEqualTo("foo");
      assertThat(intersect(list1, list2)).isEqualTo(newArrayList("foo", "bar"));
   }

   @Test
   @SuppressWarnings("unchecked")
   void testIntersectSet() {
      assertThat(intersect((Set[]) null)).isEmpty();
      assertThat(intersect(Collections.emptySet())).isEmpty();
      assertThat(intersect(Collections.emptySet(), null)).isEmpty();

      final Set<String> set1 = newLinkedHashSet("foo", "bar", "dog");
      final Set<String> set2 = newLinkedHashSet("cat", "bar", "foo");

      assertThat(intersect(set1, set2)).hasSize(2);
      assertThat(intersect(set1, set2).iterator().next()).isEqualTo("foo");
      assertThat(intersect(set1, set2)).isEqualTo(newLinkedHashSet("foo", "bar"));
   }

   @Test
   void testHead() {
      final List<String> testList = newArrayList("1", "2", "3");

      assertThat(head(testList, 1)).isEqualTo(newArrayList("1"));
      assertThat(head(testList, 2)).isEqualTo(newArrayList("1", "2"));
      assertThat(head(testList, 3)).isEqualTo(newArrayList("1", "2", "3"));
      assertThat(head(testList, 10)).isEqualTo(newArrayList("1", "2", "3"));
      assertThat(head(testList, -10)).isEqualTo(newArrayList());
   }

   @Test
   void testTail() {
      final List<String> testList = newArrayList("1", "2", "3");

      assertThat(tail(testList, 1)).isEqualTo(newArrayList("3"));
      assertThat(tail(testList, 2)).isEqualTo(newArrayList("2", "3"));
      assertThat(tail(testList, 3)).isEqualTo(newArrayList("1", "2", "3"));
      assertThat(tail(testList, 10)).isEqualTo(newArrayList("1", "2", "3"));
      assertThat(tail(testList, -10)).isEqualTo(newArrayList());
   }
}
