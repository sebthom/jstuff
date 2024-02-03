/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import net.sf.jstuff.core.exception.Exceptions;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface ThrowingRunnable<X extends Throwable> extends Runnable {

   static ThrowingRunnable<RuntimeException> from(final Runnable runnable) {
      return runnable::run;
   }

   @Override
   default void run() {
      try {
         runOrThrow();
      } catch (final Throwable t) { // CHECKSTYLE:IGNORE IllegalCatch
         throw Exceptions.wrapAsRuntimeException(t);
      }
   }

   void runOrThrow() throws X;
}
