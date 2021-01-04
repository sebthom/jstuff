/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.Set;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class IdentityHashSetTest {
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
   public void testIdentityHashSet() {
      final Set<Entity> ihs = IdentityHashSet.create();
      final Set<Entity> hs = CollectionUtils.newHashSet();

      final Entity e1 = new Entity().setName("aa");
      final Entity e2 = new Entity().setName("aa");

      assertThat(e2) //
         .isEqualTo(e1) //
         .isNotSameAs(e1);

      ihs.add(e1);
      ihs.add(e2);
      assertThat(ihs.contains(e1)).isTrue();
      assertThat(ihs.contains(e2)).isTrue();

      hs.add(e1);
      hs.add(e2);

      assertThat(ihs).hasSize(2);
      assertThat(hs).hasSize(1);

      final Set<Entity> ihs2 = IdentityHashSet.create();
      ihs2.add(e1);
      ihs2.add(e2);
      assertThat(ihs2).isEqualTo(ihs);
      ihs2.remove(e2);
      assertThat(ihs.equals(ihs2)).isFalse();
   }
}
