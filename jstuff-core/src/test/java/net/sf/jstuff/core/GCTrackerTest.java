/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class GCTrackerTest {
   private static final Logger LOG = Logger.create();

   private volatile int garbageCollected;

   @Test
   void testGCTracker() throws InterruptedException {
      final EventListener<@Nullable Object> countGC = event -> garbageCollected++;

      final var tracker = new GCTracker<>(100);
      tracker.subscribe(countGC);

      final int objects = 10000;
      final var t1 = new Thread() {
         @Override
         public void run() {
            for (int i = 0; i < objects; i++) {
               LOG.debug("[T1] new %s", i);
               tracker.track(new Object(), null);
            }
         }
      };
      final var t2 = new Thread() {
         @Override
         public void run() {
            for (int i = 0; i < objects; i++) {
               LOG.debug("[T2] new %s", i);
               tracker.track(new Object(), null);
            }
         }
      };
      t1.start();
      t2.start();
      t1.join();
      t2.join();
      System.gc();
      Threads.sleep(1000);
      System.gc();
      Threads.sleep(1000);
      System.gc();
      Threads.sleep(1000);
      assertThat(garbageCollected).isEqualTo(2 * objects);
   }
}
