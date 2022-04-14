/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.function.Consumer;

import net.sf.jstuff.core.exception.Exceptions;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> extends Consumer<T> {

   static <T> ThrowingConsumer<T, RuntimeException> from(final Consumer<T> consumer) {
      return consumer::accept;
   }

   @Override
   default void accept(final T elem) {
      try {
         acceptOrThrow(elem);
      } catch (final Throwable t) { // CHECKSTYLE:IGNORE IllegalCatch
         throw Exceptions.wrapAsRuntimeException(t);
      }
   }

   void acceptOrThrow(T elem) throws E;
}
