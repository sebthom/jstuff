/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.core.profiler;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SamplingMethodProfilerTest extends TestCase {
    private static final Random RANDOM = new Random();

    public static void slowMethod() {
        Threads.sleep(400);
        for (int i = 1; i < 1000000; i++) {
            RANDOM.nextLong();
            UUID.randomUUID();
        }
    }

    public void testSampler() throws IOException {
        final SamplingMethodProfiler profiler = new SamplingMethodProfiler(500);
        profiler.start(SamplingMethodProfilerTest.class.getName(), "slowMethod");
        try {
            slowMethod();
        } finally {
            final CallTree ct = profiler.stop();
            ct.toString(System.out, 100, 10);
        }
    }
}
