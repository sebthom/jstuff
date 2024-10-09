/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class WeakIdentityHashMapTest {
   private static final class Entity {
      @Nullable
      private String name;

      Entity setName(final String name) {
         this.name = name;
         return this;
      }

      @Override
      public int hashCode() {
         return Objects.hash(name);
      }

      @Override
      public boolean equals(@Nullable final Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         final Entity other = (Entity) obj;
         if (!Objects.equals(name, other.name))
            return false;
         return true;
      }
   }

   @Test
   void testWeakIdentityHashMap() {
      final var identityMap = new WeakIdentityHashMap<@Nullable Entity, Object>();

      @Nullable
      Entity e1 = new Entity().setName("aa");
      @Nullable
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

      final var identityMap2 = new WeakIdentityHashMap<@Nullable Entity, Object>();

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
