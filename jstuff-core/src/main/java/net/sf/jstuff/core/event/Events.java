/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Events {
   private static final Logger LOG = Logger.create();

   /**
    * @return the number of listeners notified successfully
    */
   public static <Event> int fire(final Event type, final @Nullable Collection<EventListener<Event>> listeners) {
      if (listeners == null || listeners.isEmpty())
         return 0;

      int count = 0;
      for (final EventListener<Event> listener : listeners)
         if (fire(type, listener)) {
            count++;
         }
      return count;
   }

   /**
    * @return true if the listener was notified successfully
    */
   public static <Event> boolean fire(final Event event, final @Nullable EventListener<Event> listener) {
      if (listener == null)
         return false;

      try {
         if (listener instanceof FilteringEventListener) {
            final FilteringEventListener<Event> flistener = (FilteringEventListener<Event>) listener;
            if (!flistener.accept(event))
               return false;
            flistener.onEvent(event);
         } else {
            listener.onEvent(event);
         }
         return true;
      } catch (final RuntimeException ex) {
         LOG.error(ex, "Failed to notify event listener %s", listener);
      }
      return false;
   }

   /**
    * @return the number of listeners notified successfully
    */
   @SafeVarargs
   public static <Event> int fire(final Event type, final EventListener<Event> @Nullable... listeners) {
      if (listeners == null || listeners.length < 0)
         return 0;

      int count = 0;
      for (final EventListener<Event> listener : listeners)
         if (fire(type, listener)) {
            count++;
         }
      return count;
   }
}
