/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import java.util.Objects;

import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashMapTest {
   private static class Entity {
      private String name;

      public Entity setName(final String name) {
         this.name = name;
         return this;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (name == null ? 0 : name.hashCode());
         return result;
      }

      @Override
      public boolean equals(final Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         final Entity other = (Entity) obj;
         if (!Objects.equals(name, other.name))
            return false;
         return true;
      }
   }

   @Test
   public void testWeakIdentityHashMap() {
      final Map<Entity, Object> identityMap = WeakIdentityHashMap.create();

      Entity e1 = new Entity().setName("aa");
      Entity e2 = new Entity().setName("aa");

      assertThat(e2).isEqualTo(e1).isNotSameAs(e1);

      identityMap.put(e1, Boolean.TRUE);
      identityMap.put(e2, Boolean.TRUE);
      identityMap.put(null, Boolean.TRUE);

      assertThat(identityMap) //
         .hasSize(3).containsKey(e1) //
         .containsKey(e2) //
         .containsKey(null);
      assertThat(identityMap.entrySet()).hasSize(3);
      assertThat(identityMap.keySet()).hasSize(3);
      assertThat(identityMap.values()).hasSize(3);

      System.gc();
      Threads.sleep(1000);

      assertThat(identityMap) //
         .hasSize(3) //
         .containsKey(e1) //
         .containsKey(e2) //
         .containsKey(null);
      assertThat(identityMap.entrySet()).hasSize(3);
      assertThat(identityMap.keySet()).hasSize(3);
      assertThat(identityMap.values()).hasSize(3);

      final Map<Entity, Object> identityMap2 = WeakIdentityHashMap.create();

      identityMap2.put(e1, Boolean.TRUE);
      identityMap2.put(e2, Boolean.TRUE);
      identityMap2.put(null, Boolean.TRUE);

      assertThat(identityMap2).isEqualTo(identityMap);

      identityMap2.remove(e2);
      assertThat(identityMap2) //
         .containsKey(e1) //
         .doesNotContainKey(e2);
      assertThat(identityMap).isNotEqualTo(identityMap2);

      e1 = null;
      e2 = null;

      System.gc();
      Threads.sleep(1000);

      assertThat(identityMap).hasSize(1);
      assertThat(identityMap2).hasSize(1);

      identityMap.remove(null);

      assertThat(identityMap).isEmpty();
   }
}
