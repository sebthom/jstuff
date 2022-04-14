/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.util.concurrent.Future;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface EventDispatcher<EVENT> extends EventListenable<EVENT> {

   Future<Integer> fire(EVENT event);

   void unsubscribeAll();
}
