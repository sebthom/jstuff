/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.concurrent.future.ConstantFuture;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class SyncEventDispatcher<EVENT> implements EventDispatcher<EVENT> {

   private final Set<EventListener<EVENT>> eventListeners = new CopyOnWriteArraySet<>();

   /**
    * @return the number of listeners notified successfully
    */
   @Override
   public ConstantFuture<Integer> fire(final EVENT event) {
      return ConstantFuture.of(Events.fire(event, eventListeners));
   }

   @Override
   public boolean subscribe(final EventListener<EVENT> listener) {
      Args.notNull("listener", listener);
      return eventListeners.add(listener);
   }

   @Override
   public boolean unsubscribe(final EventListener<EVENT> listener) {
      Args.notNull("listener", listener);
      return eventListeners.remove(listener);
   }

   @Override
   public void unsubscribeAll() {
      eventListeners.clear();
   }
}
