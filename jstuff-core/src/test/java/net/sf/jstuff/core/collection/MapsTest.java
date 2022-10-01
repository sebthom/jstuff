/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
   public void testSortByValueAsc() {
      final var map = new HashMap<String, Integer>();
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

      final var map = new HashMap<UnsortableKey, Integer>();
      map.put(keyE, 3);
      map.put(keyD, 2);
      map.put(keyC, 2);
      map.put(keyB, null);
      map.put(keyA, null);
      map.put(keyNull, 0);

      // {a=null, b=null, null=0, c=2, d=2, e=3}
      final Map<UnsortableKey, Integer> sortedMap = Maps.sortByValue(map);
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
      final var map = new HashMap<String, Integer>();
      map.put("e", 3);
      map.put("d", 2);
      map.put("c", 2);
      map.put("b", null);
      map.put("a", null);
      map.put(null, 0);

      // {e=3, d=2, c=2, null=0, b=null, a=null}
      final Map<String, Integer> sortedMap = Maps.sortByValue(map, SortDirection.DESC);

      assertThat(new ArrayList<>(sortedMap.values())).isNotEqualTo(new ArrayList<>(map.values()));
      assertThat(new ArrayList<>(sortedMap.values())).isEqualTo(Arrays.asList(3, 2, 2, 0, (Integer) null, (Integer) null));

      assertThat(new ArrayList<>(sortedMap.keySet())).isNotEqualTo(new ArrayList<>(map.keySet()));
      assertThat(new ArrayList<>(sortedMap.keySet())).isEqualTo(Arrays.asList("e", "d", "c", (String) null, "b", "a"));
   }
}
