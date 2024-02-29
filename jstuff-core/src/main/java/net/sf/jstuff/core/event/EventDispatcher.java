/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.event;

import java.util.concurrent.CompletableFuture;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface EventDispatcher<EVENT> extends EventListenable<EVENT> {

   CompletableFuture<Integer> fire(EVENT event);

   void unsubscribeAll();
}
