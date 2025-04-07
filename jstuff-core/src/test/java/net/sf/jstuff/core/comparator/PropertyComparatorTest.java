/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.TreeSet;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class PropertyComparatorTest {
   private static final class Entity {
      @Nullable
      String name;

      @SuppressWarnings("unused")
      @Nullable
      Entity child;

      private Entity(final @Nullable String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return Objects.toString(name);
      }
   }

   @Test
   void testPropertyComparator() {
      final var e1 = new Entity("444");
      final var e2 = new Entity("222");
      final var e3 = new Entity("333");
      final var e4 = new Entity(null);
      {
         final var set = new TreeSet<>(new PropertyComparator<>("name"));
         set.add(e1);
         set.add(e2);
         set.add(e3);
         set.add(e4);

         final var orderedEntities = set.toArray(new Entity[4]);
         assertThat(orderedEntities[0]).isEqualTo(e2);
         assertThat(orderedEntities[1]).isEqualTo(e3);
         assertThat(orderedEntities[2]).isEqualTo(e1);
         assertThat(orderedEntities[3]).isEqualTo(e4);
      }
      e1.child = new Entity("AAA");
      e2.child = new Entity("CCC");
      e3.child = new Entity(null);
      e4.child = new Entity("BBB");
      {
         final var set = new TreeSet<>(new PropertyComparator<>("child.name"));
         set.add(e1);
         set.add(e2);
         set.add(e3);
         set.add(e4);

         final var orderedEntities = set.toArray(new Entity[4]);
         assertThat(orderedEntities[0]).isEqualTo(e1);
         assertThat(orderedEntities[1]).isEqualTo(e4);
         assertThat(orderedEntities[2]).isEqualTo(e2);
         assertThat(orderedEntities[3]).isEqualTo(e3);
      }
   }
}
