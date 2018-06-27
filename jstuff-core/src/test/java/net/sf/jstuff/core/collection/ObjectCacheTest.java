/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.collection;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObjectCacheTest extends TestCase {
   public void testObjectCache_SoftRef_NoValuesToKeep() throws InterruptedException {
      final ObjectCache<String, Object> cache = new ObjectCache<String, Object>();
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
      final ObjectCache<String, Object> cache = new ObjectCache<String, Object>(true);
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
      final ObjectCache<String, Object> cache = new ObjectCache<String, Object>(2, true);
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
