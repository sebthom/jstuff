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

   public boolean subscribe(final EventListener<EVENT> listener) {
      return wrapped.subscribe(listener);
   }

   public boolean unsubscribe(final EventListener<EVENT> listener) {
      return wrapped.unsubscribe(listener);
   }

   public void unsubscribeAll() {
      wrapped.unsubscribeAll();
   }
}
