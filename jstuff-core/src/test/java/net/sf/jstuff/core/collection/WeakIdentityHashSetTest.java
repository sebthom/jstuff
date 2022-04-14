/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.*;

import java.util.Objects;
import java.util.Set;

import org.junit.Test;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashSetTest extends TestCase {
   private static class Entity {
      private String name;

      public Entity setName(final String name) {
         this.name = name;
         return this;
      }

      @Override
      public int hashCode() {
         return Objects.hash(name);
      }

      @Override
      public boolean equals(final Object obj) {
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
   public void testWeakIdentityHashSet() {
      final WeakIdentityHashSet<Entity> identitySet = WeakIdentityHashSet.create();

      Entity e1 = new Entity().setName("aa");
      Entity e2 = new Entity().setName("aa");

      assertThat(e2).isEqualTo(e1).isNotSameAs(e1);

      identitySet.add(e1);
      identitySet.add(e2);

      assertThat(identitySet).hasSize(2).contains(e1).contains(e2);
      assertThat(identitySet.toArray()).hasSize(2);

      System.gc();
      Threads.sleep(1000);

      assertThat(identitySet) //
         .hasSize(2) //
         .contains(e1) //
         .contains(e2);

      final Set<Entity> identitySet2 = WeakIdentityHashSet.create();
      identitySet2.add(e1);
      identitySet2.add(e2);

      assertThat(identitySet2).isEqualTo(identitySet);

      identitySet2.remove(e2);
      assertThat(identitySet2) //
         .hasSize(1) //
         .contains(e1);
      assertThat(identitySet2.contains(e2)).isFalse();
      assertThat(identitySet).isNotEqualTo(identitySet2);

      e1 = null;
      e2 = null;

      System.gc();
      Threads.sleep(1000);

      assertThat(identitySet).isEmpty();
      assertThat(identitySet2).isEmpty();
   }
}
