/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.concurrent;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ConstantFuture<T> implements Future<T> {

   public static <T> ConstantFuture<T> of(final T value) {
      return new ConstantFuture<T>(value);
   }

   private final T value;

   public ConstantFuture(final T value) {
      this.value = value;
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

   public T get() {
      return value;
   }

   public T get(final long timeout, final TimeUnit unit) {
      return get();
   }
}
