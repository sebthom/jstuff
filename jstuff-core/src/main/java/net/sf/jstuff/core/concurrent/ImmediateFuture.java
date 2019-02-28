/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ImmediateFuture<T> implements Future<T> {

   private final T value;
   private final ExecutionException ex;

   public ImmediateFuture(final T value) {
      this.value = value;
      ex = null;
   }

   public ImmediateFuture(final Throwable ex) {
      Args.notNull("ex", ex);
      this.value = null;
      this.ex = new ExecutionException(ex);
   }

   public boolean cancel(final boolean mayInterruptIfRunning) {
      return false;
   }

   public boolean isCancelled() {
      return false;
   }

   public boolean isDone() {
      return true;
   }

   public T get() throws ExecutionException {
      if (ex == null)
         return value;
      throw ex;
   }

   public T get(final long timeout, final TimeUnit unit) throws ExecutionException {
      return get();
   }

}
