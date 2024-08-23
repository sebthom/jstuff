/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FIFOMapTest {

   @Test
   public void testFIFOMap() {
      final FIFOMap<String, String> map = FIFOMap.create(3);
      map.put("1", "1");
      map.put("2", "2");
      map.put("3", "3");
      map.put("4", "4");
      assertThat(map).hasSize(3);
      assertThat(map.containsKey("2")).isTrue();
      assertThat(map.containsKey("3")).isTrue();
      assertThat(map.containsKey("4")).isTrue();

      assertThat(map.get("2")).isEqualTo("2");
      map.put("5", "5");

      assertThat(map.containsKey("3")).isTrue();
      assertThat(map.containsKey("4")).isTrue();
      assertThat(map.containsKey("5")).isTrue();
   }
}
