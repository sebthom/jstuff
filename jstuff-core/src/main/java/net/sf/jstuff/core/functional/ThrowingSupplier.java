/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.function.Supplier;

import net.sf.jstuff.core.exception.Exceptions;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface ThrowingSupplier<I, X extends Throwable> extends Supplier<I> {

   static <T> ThrowingSupplier<T, RuntimeException> from(final Supplier<T> supplier) {
      return supplier::get;
   }

   @Override
   default I get() {
      try {
         return getOrThrow();
      } catch (final Throwable t) { // CHECKSTYLE:IGNORE IllegalCatch
         throw Exceptions.wrapAsRuntimeException(t);
      }
   }

   I getOrThrow() throws X;
}
