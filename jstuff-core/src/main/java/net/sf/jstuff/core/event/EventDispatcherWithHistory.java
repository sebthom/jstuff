/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.event;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Future;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class EventDispatcherWithHistory<EVENT> implements EventDispatcher<EVENT> {
   private List<EVENT> eventHistory;
   private final EventDispatcher<EVENT> wrapped;

   public EventDispatcherWithHistory(final EventDispatcher<EVENT> wrapped) {
      this.wrapped = wrapped;
      initEventHistory();
   }

   protected void addEventToHistory(final EVENT event) {
      eventHistory.add(event);
   }

   public void clearHistory() {
      eventHistory.clear();
   }

   @Override
   public Future<Integer> fire(final EVENT event) {
      addEventToHistory(event);

      return wrapped.fire(event);
   }

   protected Iterable<EVENT> getEventHistory() {
      return eventHistory;
   }

   protected void initEventHistory() {
      // http://stackoverflow.com/questions/34556989/vector-vs-synchronizedlist-performance
      eventHistory = new Vector<EVENT>();
   }

   /**
    * If the listener was not subscribed already, all recorded events will be send to the given listeners.
    */
   public boolean subscribeAndReplayHistory(final EventListener<EVENT> listener) {
      Args.notNull("listener", listener);

      if (wrapped.subscribe(listener)) {
         for (final EVENT event : getEventHistory()) {
            listener.onEvent(event);
         }
         return true;
      }

      return false;
   }

   @Override
   public boolean subscribe(final EventListener<EVENT> listener) {
      return wrapped.subscribe(listener);
   }

   @Override
   public boolean unsubscribe(final EventListener<EVENT> listener) {
      return wrapped.unsubscribe(listener);
   }

   @Override
   public void unsubscribeAll() {
      wrapped.unsubscribeAll();
   }
}
