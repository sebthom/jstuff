/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum FutureState {
   INCOMPLETE,

   /**
    * Completed normally.
    */
   COMPLETED,

   COMPLETED_EXCEPTIONALLY,

   CANCELLED;

   public static FutureState of(final Future<?> future) {
      Args.notNull("future", future);
      if (future.isCancelled())
         return FutureState.CANCELLED;

      if (future instanceof final CompletableFuture<?> cf && cf.isCompletedExceptionally())
         return FutureState.COMPLETED_EXCEPTIONALLY;

      if (future.isDone())
         return FutureState.COMPLETED;

      return FutureState.INCOMPLETE;
   }
}
