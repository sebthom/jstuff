/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TreeSet;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyComparatorTest {
   private static final class Entity {
      String name;

      @SuppressWarnings("unused")
      Entity child;

      private Entity(final String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return name;
      }
   }

   @Test
   public void testPropertyComparator() {
      final Entity e1 = new Entity("444");
      final Entity e2 = new Entity("222");
      final Entity e3 = new Entity("333");
      final Entity e4 = new Entity(null);
      {
         final TreeSet<Entity> set = new TreeSet<>(new PropertyComparator<Entity>("name"));
         set.add(e1);
         set.add(e2);
         set.add(e3);
         set.add(e4);

         final Entity[] orderedEntities = set.toArray(new Entity[4]);
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
         final TreeSet<Entity> set = new TreeSet<>(new PropertyComparator<Entity>("child.name"));
         set.add(e1);
         set.add(e2);
         set.add(e3);
         set.add(e4);

         final Entity[] orderedEntities = set.toArray(new Entity[4]);
         assertThat(orderedEntities[0]).isEqualTo(e1);
         assertThat(orderedEntities[1]).isEqualTo(e4);
         assertThat(orderedEntities[2]).isEqualTo(e2);
         assertThat(orderedEntities[3]).isEqualTo(e3);
      }
   }
}
