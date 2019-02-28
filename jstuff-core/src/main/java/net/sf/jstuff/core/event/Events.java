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

import java.util.Collection;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Events {
   private static final Logger LOG = Logger.create();

   /**
    * @return the number of listeners notified successfully
    */
   public static <Event> int fire(final Event type, final Collection<EventListener<Event>> listeners) {
      int count = 0;
      if (listeners != null && listeners.size() > 0) {
         for (final EventListener<Event> listener : listeners)
            if (fire(type, listener)) {
               count++;
            }
      }
      return count;
   }

   /**
    * @return true if the listener was notified successfully
    */
   public static <Event> boolean fire(final Event event, final EventListener<Event> listener) {
      if (listener != null) {
         try {
            if (listener instanceof FilteringEventListener) {
               final FilteringEventListener<Event> flistener = (FilteringEventListener<Event>) listener;
               if (flistener.accept(event)) {
                  flistener.onEvent(event);
               } else
                  return false;
            } else {
               listener.onEvent(event);
            }
            return true;
         } catch (final RuntimeException ex) {
            LOG.error(ex, "Failed to notify event listener %s", listener);
         }
      }
      return false;
   }

   /**
    * @return the number of listeners notified successfully
    */
   public static <Event> int fire(final Event type, final EventListener<Event>... listeners) {
      int count = 0;
      if (listeners != null && listeners.length > 0) {
         for (final EventListener<Event> listener : listeners)
            if (fire(type, listener)) {
               count++;
            }
      }
      return count;
   }
}
