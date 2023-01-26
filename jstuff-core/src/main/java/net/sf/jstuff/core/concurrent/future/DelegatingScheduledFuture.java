/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.future;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingScheduledFuture<V> extends DelegatingFuture<V> implements ScheduledFuture<V> {

   public DelegatingScheduledFuture(final ScheduledFuture<V> wrapped) {
      super(wrapped);
   }

   @Override
   public int compareTo(final Delayed o) {
      return ((ScheduledFuture<V>) wrapped).compareTo(o);
   }

   @Override
   public long getDelay(final TimeUnit unit) {
      return ((ScheduledFuture<V>) wrapped).getDelay(unit);
   }

   @Override
   public ScheduledFuture<V> getWrapped() {
      return (ScheduledFuture<V>) wrapped;
   }
}
