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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface FilteringEventListener<Event> extends EventListener<Event> {
   /**
    * Determines if this event listener accepts the given event.
    *
    * The {@link #onEvent(Object)} method is only called when <code>true</code> is returned.
    */
   boolean accept(Event event);
}
