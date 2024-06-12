/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;

import net.sf.jstuff.core.comparator.SortDirection;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapsTest {

   private static class UnsortableKey {
      public final String value;

      UnsortableKey(final String value) {
         this.value = value;
      }

      @Override
      public String toString() {
         return value;
      }
   }

   @Test
   public void testPutAllIfAbsent() {
      final var map1 = new HashMap<@Nullable String, Integer>();
      map1.put("a", 1);
      map1.put("b", 1);

      final var map2 = new HashMap<@Nullable String, Integer>();
      map2.put("b", 2);
      map2.put("c", 2);

      Maps.putAllIfAbsent(map1, map2);

      assertThat(map1).containsEntry("b", 1);
      assertThat(map1).containsKey("c");
   }

   @Test
   public void testRemap() {
      final Map<String, Integer> map = new HashMap<>();
      map.put("one", 1);
      map.put("two", 2);
      map.put("three", 3);

      assertThat(Maps.remap(map, Object::toString)).isNotNull() //
         .hasSize(3) //
         .containsEntry("1", 1) //
         .containsEntry("2", 2) //
         .containsEntry("3", 3);

      assertThat(Maps.remap(map, Object::toString, value -> "Number " + value)).isNotNull() //
         .hasSize(3) //
         .containsEntry("1", "Number 1") //
         .containsEntry("2", "Number 2") //
         .containsEntry("3", "Number 3");

      assertThat(Maps.remap(map, (key, value) -> key + "_" + value)).isNotNull() //
         .hasSize(3) //
         .containsEntry("one_1", 1) //
         .containsEntry("two_2", 2) //
         .containsEntry("three_3", 3);

      assertThat(Maps.remap(map, (key, value) -> key + "_" + value, (key, value) -> key.toUpperCase() + " " + value * 10)).isNotNull() //
         .hasSize(3) //
         .containsEntry("one_1", "ONE 10") //
         .containsEntry("two_2", "TWO 20") //
         .containsEntry("three_3", "THREE 30");
   }

   @Test
   public void testSortByValueAsc() {
      final var map = new HashMap<@Nullable String, @Nullable Integer>();
      map.put("e", 3);
      map.put("d", 2);
      map.put("c", 2);
      map.put("b", null);
      map.put("a", null);
      map.put(null, 0);

      // {a=null, b=null, null=0, c=2, d=2, e=3}
      final var sortedMap = Maps.sortByValue(map);

      assertThat(new ArrayList<>(sortedMap.values())).isNotEqualTo(new ArrayList<>(map.values()));
      assertThat(new ArrayList<>(sortedMap.values())).isEqualTo(Arrays.asList((Integer) null, (Integer) null, 0, 2, 2, 3));

      assertThat(new ArrayList<>(sortedMap.keySet())).isNotEqualTo(new ArrayList<>(map.keySet()));
      assertThat(new ArrayList<>(sortedMap.keySet())).isEqualTo(Arrays.asList("a", "b", (String) null, "c", "d", "e"));
   }

   @Test
   public void testSortByValueAsc_UnsortableKeys() {
      final UnsortableKey keyE = new UnsortableKey("e");
      final UnsortableKey keyD = new UnsortableKey("d");
      final UnsortableKey keyC = new UnsortableKey("c");
      final UnsortableKey keyB = new UnsortableKey("b");
      final UnsortableKey keyA = new UnsortableKey("a");
      final UnsortableKey keyNull = null;

      final var map = new HashMap<@Nullable UnsortableKey, @Nullable Integer>();
      map.put(keyE, 3);
      map.put(keyD, 2);
      map.put(keyC, 2);
      map.put(keyB, null);
      map.put(keyA, null);
      map.put(keyNull, 0);

      // {a=null, b=null, null=0, c=2, d=2, e=3}
      final var sortedMap = Maps.sortByValue(map);
      System.out.println(sortedMap);
      assertThat(new ArrayList<>(sortedMap.values())).isNotEqualTo(new ArrayList<>(map.values()));
      assertThat(new ArrayList<>(sortedMap.values())).isEqualTo(Arrays.asList((Integer) null, (Integer) null, 0, 2, 2, 3));

      assertThat(new ArrayList<>(sortedMap.keySet())).isNotEqualTo(new ArrayList<>(map.keySet()));
      assertThat(new ArrayList<>(sortedMap.keySet())).satisfiesAnyOf( //
         m -> assertThat(m).isEqualTo(Arrays.asList(keyA, keyB, (String) null, keyC, keyD, keyE)), //
         m -> assertThat(m).isEqualTo(Arrays.asList(keyA, keyB, (String) null, keyD, keyC, keyE)), //
         m -> assertThat(m).isEqualTo(Arrays.asList(keyB, keyA, (String) null, keyC, keyD, keyE)), //
         m -> assertThat(m).isEqualTo(Arrays.asList(keyB, keyA, (String) null, keyD, keyC, keyE)) //
      );
   }

   @Test
   public void testSortByValueDesc() {
      final var map = new HashMap<@Nullable String, @Nullable Integer>();
      map.put("e", 3);
      map.put("d", 2);
      map.put("c", 2);
      map.put("b", null);
      map.put("a", null);
      map.put(null, 0);

      // {e=3, d=2, c=2, null=0, b=null, a=null}
      final var sortedMap = Maps.sortByValue(map, SortDirection.DESC);

      assertThat(new ArrayList<>(sortedMap.values())).isNotEqualTo(new ArrayList<>(map.values()));
      assertThat(new ArrayList<>(sortedMap.values())).isEqualTo(Arrays.asList(3, 2, 2, 0, (Integer) null, (Integer) null));

      assertThat(new ArrayList<>(sortedMap.keySet())).isNotEqualTo(new ArrayList<>(map.keySet()));
      assertThat(new ArrayList<>(sortedMap.keySet())).isEqualTo(Arrays.asList("e", "d", "c", (String) null, "b", "a"));
   }
}
