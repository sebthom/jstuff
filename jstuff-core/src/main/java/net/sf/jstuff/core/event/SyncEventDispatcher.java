/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.sf.jstuff.core.concurrent.ConstantFuture;
import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class SyncEventDispatcher<EVENT> implements EventDispatcher<EVENT> {

   private final Set<EventListener<EVENT>> eventListeners = new CopyOnWriteArraySet<EventListener<EVENT>>();

   /**
    * @return the number of listeners notified successfully
    */
   public ConstantFuture<Integer> fire(final EVENT event) {
      return ConstantFuture.of(Events.fire(event, eventListeners));
   }

   public boolean subscribe(final EventListener<EVENT> listener) {
      Args.notNull("listener", listener);
      return eventListeners.add(listener);
   }

   public boolean unsubscribe(final EventListener<EVENT> listener) {
      Args.notNull("listener", listener);
      return eventListeners.remove(listener);
   }

   public void unsubscribeAll() {
      eventListeners.clear();
   }
}
