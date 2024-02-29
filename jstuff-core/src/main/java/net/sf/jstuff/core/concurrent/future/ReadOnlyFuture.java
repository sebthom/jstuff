/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.Future;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReadOnlyFuture<V> extends DelegatingFuture<V> {

   public ReadOnlyFuture(final Future<V> wrapped) {
      super(wrapped);
   }

   @Override
   public boolean cancel(final boolean mayInterruptIfRunning) {
      return false;
   }
}
