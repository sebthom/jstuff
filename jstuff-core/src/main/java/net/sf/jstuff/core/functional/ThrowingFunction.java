/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.function.Function;

import net.sf.jstuff.core.exception.Exceptions;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface ThrowingFunction<I, O, X extends Throwable> extends Function<I, O> {

   @Override
   default O apply(final I elem) {
      try {
         return applyOrThrow(elem);
      } catch (final Throwable t) { // CHECKSTYLE:IGNORE IllegalCatch
         throw Exceptions.wrapAsRuntimeException(t);
      }
   }

   O applyOrThrow(I input) throws X;
}
