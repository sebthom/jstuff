/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MutableRef<V> implements Ref<V> {

   public static <V> MutableRef<V> of(final V initialValue) {
      return new MutableRef<>(initialValue);
   }

   private volatile V value;

   public MutableRef() {
   }

   public MutableRef(final V value) {
      this.value = value;
   }

   @Override
   public V get() {
      return value;
   }

   public void set(final V value) {
      this.value = value;
   }
}
