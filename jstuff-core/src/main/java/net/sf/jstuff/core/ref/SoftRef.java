/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.io.Serializable;
import java.lang.ref.SoftReference;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SoftRef<V> implements Ref<V>, Serializable {
   private static final long serialVersionUID = 1L;

   public static <V> SoftRef<V> of(final SoftReference<V> value) {
      return new SoftRef<>(value);
   }

   public static <V> SoftRef<V> of(final V value) {
      return new SoftRef<>(value);
   }

   private final SoftReference<V> ref;

   public SoftRef(final SoftReference<V> ref) {
      this.ref = ref;
   }

   public SoftRef(final V value) {
      this.ref = new SoftReference<>(value);
   }

   @Override
   public V get() {
      return ref.get();
   }

   public SoftReference<V> getSoftReference() {
      return ref;
   }

   @Override
   public String toString() {
      return String.valueOf(get());
   }
}
