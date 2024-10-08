/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {
   public static <K, V> LRUMap<K, V> create(final int maxCapacity) {
      return new LRUMap<>(maxCapacity);
   }

   private static final long serialVersionUID = 1L;

   private final int maxCapacity;

   /**
    * @param maxCapacity the maximum capacity of the cache. When maxCapacity is exceeded the oldest entry in the cache is removed.
    */
   public LRUMap(final int maxCapacity) {
      super(8, 1.0f, true);
      Args.min("maxCapacity", maxCapacity, 1);
      this.maxCapacity = maxCapacity;
   }

   public int getMaxCapacity() {
      return maxCapacity;
   }

   @Override
   protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
      return size() > maxCapacity;
   }
}
