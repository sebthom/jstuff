/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;

import net.sf.jstuff.core.concurrent.ThreadSafe;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class EventDispatcherWithHistory<EVENT> implements EventDispatcher<EVENT> {
   private List<EVENT> eventHistory = Collections.emptyList();
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
   public CompletableFuture<Integer> fire(final EVENT event) {
      addEventToHistory(event);

      return wrapped.fire(event);
   }

   protected Iterable<EVENT> getEventHistory() {
      return eventHistory;
   }

   protected void initEventHistory() {
      // http://stackoverflow.com/questions/34556989/vector-vs-synchronizedlist-performance
      eventHistory = new Vector<>();
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
