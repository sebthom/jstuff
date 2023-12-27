/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class HashMapExt<K, V> extends HashMap<K, V> implements MapExt<K, V> {

   private static final long serialVersionUID = 1L;

   public HashMapExt() {
   }

   public HashMapExt(final int initialCapacity, final float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public HashMapExt(final int initialCapacity) {
      super(initialCapacity);
   }

   public HashMapExt(final Map<? extends K, ? extends V> initialValues) {
      super(initialValues);
   }
}
