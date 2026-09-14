/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.io.Serializable;
import java.lang.ref.WeakReference;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakRef<V> implements Ref<@Nullable V>, Serializable {
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
      ref = new WeakReference<>(value);
   }

   @Override
   public @Nullable V get() {
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
