/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TreeMapExt<K, V> extends TreeMap<K, V> implements MapExt<K, V> {

   private static final long serialVersionUID = 1L;

   public TreeMapExt() {
   }

   public TreeMapExt(final Comparator<? super K> comparator) {
      super(comparator);
   }

   public TreeMapExt(final Map<? extends K, ? extends V> m) {
      super(m);
   }

   public TreeMapExt(final SortedMap<K, ? extends V> m) {
      super(m);
   }
}
