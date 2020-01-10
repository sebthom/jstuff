/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.Set;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IdentityHashSetTest extends TestCase {
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
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         return true;
      }
   }

   public void testIdentityHashSet() {
      final Set<Entity> ihs = IdentityHashSet.create();
      final Set<Entity> hs = CollectionUtils.newHashSet();

      final Entity e1 = new Entity().setName("aa");
      final Entity e2 = new Entity().setName("aa");

      assertEquals(e1, e2);
      assertNotSame(e1, e2);

      ihs.add(e1);
      ihs.add(e2);
      assertTrue(ihs.contains(e1));
      assertTrue(ihs.contains(e2));

      hs.add(e1);
      hs.add(e2);

      assertEquals(2, ihs.size());
      assertEquals(1, hs.size());

      final Set<Entity> ihs2 = IdentityHashSet.create();
      ihs2.add(e1);
      ihs2.add(e2);
      assertEquals(ihs, ihs2);
      ihs2.remove(e2);
      assertFalse(ihs.equals(ihs2));
   }
}
