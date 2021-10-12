/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MutableRef<T> implements Ref<T> {

   public static <T> MutableRef<T> of(final T value) {
      return new MutableRef<>(value);
   }

   private volatile T value;

   public MutableRef() {
   }

   public MutableRef(final T value) {
      this.value = value;
   }

   @Override
   public T get() {
      return value;
   }

   public void set(final T value) {
      this.value = value;
   }
}
