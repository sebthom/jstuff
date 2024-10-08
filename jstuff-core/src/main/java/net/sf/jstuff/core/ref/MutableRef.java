/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface MutableRef<V> extends Ref<V> {

   class Default<V> implements MutableRef<V> {

      protected volatile V value;

      @SuppressWarnings("null")
      protected Default() {
      }

      protected Default(final V value) {
         this.value = value;
      }

      @Override
      public V get() {
         return value;
      }

      @Override
      public void set(final V value) {
         this.value = value;
      }

      @Override
      public String toString() {
         return String.valueOf(get());
      }
   }

   static <@Nullable V> MutableRef<V> create() {
      return new Default<>();
   }

   static <@NonNull V> MutableRef<V> of(final V initialValue) {
      return new Default<>(initialValue);
   }

   static <@Nullable V extends @Nullable Object> MutableRef<V> ofNullable(final V initialValue) {
      return new Default<>(initialValue);
   }

   void set(V value);
}
