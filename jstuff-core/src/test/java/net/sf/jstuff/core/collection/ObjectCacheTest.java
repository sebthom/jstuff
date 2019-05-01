/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObjectCacheTest extends TestCase {
   public void testObjectCache_SoftRef_NoValuesToKeep() throws InterruptedException {
      final ObjectCache<String, Object> cache = new ObjectCache<>();
      cache.put("1", new Object());
      cache.put("2", new Object());
      cache.put("3", new Object());
      cache.get("1");
      cache.get("2");
      cache.get("3");
      System.gc();
      Thread.sleep(500);
      assertTrue(cache.contains("1"));
      assertTrue(cache.contains("2"));
      assertTrue(cache.contains("3"));
   }

   public void testObjectCache_WeakRef_NoValuesToKeep() throws InterruptedException {
      final ObjectCache<String, Object> cache = new ObjectCache<>(true);
      cache.put("1", new Object());
      cache.put("2", new Object());
      cache.put("3", new Object());
      cache.get("1");
      cache.get("2");
      cache.get("3");
      System.gc();
      Thread.sleep(500);
      assertFalse(cache.contains("1"));
      assertFalse(cache.contains("2"));
      assertFalse(cache.contains("3"));
   }

   public void testObjectCache_WeakRef_Last2ValuesToKeep() throws InterruptedException {
      final ObjectCache<String, Object> cache = new ObjectCache<>(2, true);
      cache.put("1", new Object());
      cache.put("2", new Object());
      cache.put("3", new Object());
      cache.get("1");
      cache.get("2");
      cache.get("3");
      System.gc();
      Thread.sleep(500);
      assertFalse(cache.contains("1"));
      assertTrue(cache.contains("2"));
      assertTrue(cache.contains("3"));
   }
}
