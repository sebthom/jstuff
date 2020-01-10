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

import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakIdentityHashMapTest extends TestCase {
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

   public void testWeakIdentityHashMap() throws InterruptedException {
      final Map<Entity, Object> identityMap = WeakIdentityHashMap.create();

      Entity e1 = new Entity().setName("aa");
      Entity e2 = new Entity().setName("aa");

      assertEquals(e1, e2);
      assertNotSame(e1, e2);

      identityMap.put(e1, Boolean.TRUE);
      identityMap.put(e2, Boolean.TRUE);
      identityMap.put(null, Boolean.TRUE);

      assertEquals(3, identityMap.size());
      assertTrue(identityMap.containsKey(e1));
      assertTrue(identityMap.containsKey(e2));
      assertTrue(identityMap.containsKey(null));
      assertEquals(3, identityMap.entrySet().size());
      assertEquals(3, identityMap.keySet().size());
      assertEquals(3, identityMap.values().size());

      System.gc();
      Thread.sleep(1000);

      assertEquals(3, identityMap.size());
      assertTrue(identityMap.containsKey(e1));
      assertTrue(identityMap.containsKey(e2));
      assertTrue(identityMap.containsKey(null));
      assertEquals(3, identityMap.entrySet().size());
      assertEquals(3, identityMap.keySet().size());
      assertEquals(3, identityMap.values().size());

      final Map<Entity, Object> identityMap2 = WeakIdentityHashMap.create();

      identityMap2.put(e1, Boolean.TRUE);
      identityMap2.put(e2, Boolean.TRUE);
      identityMap2.put(null, Boolean.TRUE);

      assertEquals(identityMap, identityMap2);

      identityMap2.remove(e2);
      assertTrue(identityMap2.containsKey(e1));
      assertFalse(identityMap2.containsKey(e2));
      assertFalse(identityMap.equals(identityMap2));

      e1 = null;
      e2 = null;

      System.gc();
      Thread.sleep(1000);

      assertEquals(1, identityMap.size());
      assertEquals(1, identityMap2.size());

      identityMap.remove(null);

      assertEquals(0, identityMap.size());
   }
}
