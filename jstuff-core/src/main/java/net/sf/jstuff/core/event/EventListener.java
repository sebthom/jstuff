/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface EventListener<Event> {
   void onEvent(Event event);
}
