/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FIFOMap<K, V> extends LinkedHashMap<K, V> {

   private static final long serialVersionUID = 1L;

   public static <K, V> FIFOMap<K, V> create(final int maxSize) {
      return new FIFOMap<>(maxSize);
   }

   private final int maxSize;

   /**
    * @param maxSize the maximum size of the cache. When maxSize is exceeded the oldest entry in the cache is removed.
    */
   public FIFOMap(final int maxSize) {
      super(maxSize, 0.75f, false);
      this.maxSize = maxSize;
   }

   public int getMaxSize() {
      return maxSize;
   }

   @Override
   protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
      return size() > maxSize;
   }
}
