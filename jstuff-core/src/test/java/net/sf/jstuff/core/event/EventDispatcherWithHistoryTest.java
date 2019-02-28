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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EventDispatcherWithHistoryTest extends TestCase {

   public void testEventDispatcherWithHistory() throws InterruptedException, ExecutionException {
      final EventDispatcherWithHistory<String> em = new EventDispatcherWithHistory<String>(new SyncEventDispatcher<String>());

      assertEquals(0, em.fire("123").get().intValue());
      assertEquals(0, em.fire("1234567890").get().intValue());

      final AtomicLong listener1Count = new AtomicLong();
      final EventListener<String> listener1 = new EventListener<String>() {
         public void onEvent(final String event) {
            listener1Count.incrementAndGet();
         }
      };

      assertTrue(em.subscribe(listener1));
      assertFalse(em.subscribe(listener1));
      assertEquals(0, listener1Count.get());

      final AtomicLong listener2Count = new AtomicLong();
      final EventListener<String> listener2 = new FilteringEventListener<String>() {
         public boolean accept(final String event) {
            return event != null && event.length() < 5;
         }

         public void onEvent(final String event) {
            listener2Count.incrementAndGet();
         }
      };

      assertTrue(em.subscribeAndReplayHistory(listener2));
      assertFalse(em.subscribeAndReplayHistory(listener2));
      assertEquals(2, listener2Count.get());
   }
}
