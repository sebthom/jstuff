/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.util.function.Predicate;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface FilteringEventListener<Event> extends EventListener<Event> {

   static <Event> FilteringEventListener<Event> create(final EventListener<Event> listener, final Predicate<Event> when) {
      return new FilteringEventListener<>() {
         @Override
         public void onEvent(final Event event) {
            listener.onEvent(event);
         }

         @Override
         public boolean accept(final Event event) {
            return when.test(event);
         }
      };
   }

   /**
    * Determines if this event listener accepts the given event.
    *
    * The {@link #onEvent(Object)} method is only called when <code>true</code> is returned.
    */
   boolean accept(Event event);
}
