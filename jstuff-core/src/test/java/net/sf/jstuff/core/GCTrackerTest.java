/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core;

import junit.framework.TestCase;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GCTrackerTest extends TestCase {
    private static final Logger LOG = Logger.create();

    private volatile int garbageCollected;

    public void testGCTracker() throws InterruptedException {
        final EventListener<Void> countGC = new EventListener<Void>() {
            public void onEvent(final Void event) {
                garbageCollected++;
            }
        };

        final GCTracker<Void> tracker = new GCTracker<Void>(100);
        tracker.subscribe(countGC);

        final int objects = 10000;
        final Thread t1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < objects; i++) {
                    LOG.debug("[T1] new %s", i);
                    tracker.track(new Object(), null);
                }
            };
        };
        final Thread t2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < objects; i++) {
                    LOG.debug("[T2] new %s", i);
                    tracker.track(new Object(), null);
                }
            };
        };
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.gc();
        Thread.sleep(1000);
        System.gc();
        Thread.sleep(1000);
        assertEquals(2 * objects, garbageCollected);
    }
}
