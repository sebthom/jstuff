/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface MutableRef<V> extends Ref<V> {

   class Default<V> implements MutableRef<V> {

      private volatile V value;

      public Default() {
      }

      public Default(final V value) {
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
   }

   static <V> MutableRef<V> of(final V initialValue) {
      return new Default<>(initialValue);
   }

   void set(V value);
}
