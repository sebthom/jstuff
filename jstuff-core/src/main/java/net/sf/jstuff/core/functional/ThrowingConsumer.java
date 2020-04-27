/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.functional;

import java.util.function.Consumer;

import net.sf.jstuff.core.exception.Exceptions;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> extends Consumer<T> {

   static <T> ThrowingConsumer<T, RuntimeException> from(final Consumer<T> consumer) {
      return value -> consumer.accept(value);
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
