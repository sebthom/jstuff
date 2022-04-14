/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface FilteringEventListener<Event> extends EventListener<Event> {
   /**
    * Determines if this event listener accepts the given event.
    *
    * The {@link #onEvent(Object)} method is only called when <code>true</code> is returned.
    */
   boolean accept(Event event);
}
