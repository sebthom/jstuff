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
public class AsyncEventDispatcherTest extends TestCase {

   public void testAsyncEventDispatcher() throws InterruptedException, ExecutionException {
      final EventDispatcher<String> em = new AsyncEventDispatcher<>();

      final AtomicLong listener1Count = new AtomicLong();
      final EventListener<String> listener1 = new EventListener<String>() {
         @Override
         public void onEvent(final String event) {
            listener1Count.incrementAndGet();
         }
      };

      assertTrue(em.subscribe(listener1));
      assertFalse(em.subscribe(listener1));

      final AtomicLong listener2Count = new AtomicLong();
      final EventListener<String> listener2 = new FilteringEventListener<String>() {
         @Override
         public boolean accept(final String event) {
            return event != null && event.length() < 5;
         }

         @Override
         public void onEvent(final String event) {
            listener2Count.incrementAndGet();
         }
      };

      assertTrue(em.subscribe(listener2));
      assertFalse(em.subscribe(listener2));

      assertEquals(2, em.fire("123").get().intValue());
      assertEquals(1, em.fire("1234567890").get().intValue());
      Thread.yield();
      assertEquals(2, listener1Count.get());
      assertEquals(1, listener2Count.get());
   }
}
