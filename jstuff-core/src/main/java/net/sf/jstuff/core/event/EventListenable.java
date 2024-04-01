/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface EventListenable<EVENT> {
   boolean subscribe(EventListener<EVENT> listener);

   default boolean subscribe(final FilteringEventListener<EVENT> listener) {
      return subscribe((EventListener<EVENT>) listener);
   }

   boolean unsubscribe(EventListener<EVENT> listener);
}
