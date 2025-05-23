/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class IdentityHashSetTest {
   private static final class Entity {
      private @Nullable String name;

      Entity setName(final String name) {
         this.name = name;
         return this;
      }

      @Override
      public int hashCode() {
         return Objects.hash(name);
      }

      @Override
      public boolean equals(final @Nullable Object obj) {
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
   void testIdentityHashSet() {
      final var ihs = new IdentityHashSet<Entity>();
      final var hs = new HashSet<Entity>();

      final var e1 = new Entity().setName("aa");
      final var e2 = new Entity().setName("aa");

      assertThat(e2) //
         .isEqualTo(e1) //
         .isNotSameAs(e1);

      ihs.add(e1);
      ihs.add(e2);
      assertThat(ihs).contains(e1);
      assertThat(ihs).contains(e2);

      hs.add(e1);
      hs.add(e2);

      assertThat(ihs).hasSize(2);
      assertThat(hs).hasSize(1);

      final var ihs2 = new IdentityHashSet<Entity>();
      ihs2.add(e1);
      ihs2.add(e2);
      assertThat(ihs2).isEqualTo(ihs);
      ihs2.remove(e2);
      assertThat(ihs).isNotEqualTo(ihs2);
   }
}
