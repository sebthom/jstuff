/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.util.function.Supplier;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Ref<V> {

   V get();

   default boolean isNotNull() {
      return get() != null;
   }

   default boolean isNull() {
      return get() == null;
   }

   default V orElse(final V other) {
      final V value = get();
      return value != null ? value : other;
   }

   default V orElseGet(final Supplier<? extends V> other) {
      final V value = get();
      return value != null ? value : other.get();
   }

   default <T extends Throwable> V orElseThrow(final Supplier<? extends T> exceptionSupplier) throws T {
      final V value = get();
      if (value != null)
         return value;
      throw exceptionSupplier.get();
   }
}
