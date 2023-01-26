/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.io.Serializable;
import java.lang.ref.WeakReference;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakRef<V> implements Ref<V>, Serializable {
   private static final long serialVersionUID = 1L;

   public static <V> WeakRef<V> of(final WeakReference<V> value) {
      return new WeakRef<>(value);
   }

   public static <V> WeakRef<V> of(final V value) {
      return new WeakRef<>(value);
   }

   private final WeakReference<V> ref;

   public WeakRef(final WeakReference<V> ref) {
      this.ref = ref;
   }

   public WeakRef(final V value) {
      this.ref = new WeakReference<>(value);
   }

   @Override
   public V get() {
      return ref.get();
   }

   public WeakReference<V> getWeakReference() {
      return ref;
   }

   @Override
   public String toString() {
      return String.valueOf(get());
   }
}
