/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe object interner. Keeps strong references to interned objects. Should only used with immutable objects.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class Interner {
   private final ConcurrentMap<Object, Object> refs = new ConcurrentHashMap<>();

   public void clear() {
      refs.clear();
   }

   @SuppressWarnings("unchecked")
   public <T> T intern(final T immutable) {
      if (immutable == null)
         return immutable;

      T interned = (T) refs.get(immutable);
      if (interned == null) {
         interned = (T) refs.putIfAbsent(immutable, immutable);
         if (interned == null)
            return immutable;
      }
      return interned;
   }

   @SuppressWarnings("unchecked")
   public <T> T remove(final T immutable) {
      return (T) refs.remove(immutable);
   }

   public int size() {
      return refs.size();
   }
}
