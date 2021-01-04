/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface EventListenable<EVENT> {
   boolean subscribe(EventListener<EVENT> listener);

   boolean unsubscribe(EventListener<EVENT> listener);
}
