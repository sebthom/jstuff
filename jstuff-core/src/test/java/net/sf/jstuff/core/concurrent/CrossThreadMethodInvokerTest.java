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
package net.sf.jstuff.core.concurrent;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.CrossThreadMethodInvoker.CrossThreadProxy;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CrossThreadMethodInvokerTest extends TestCase {
    public static interface IService {
        String work(String input);
    }

    public static class Service implements IService {
        private final Thread owner = Thread.currentThread();

        int invocations = 0;

        public String work(final String input) {
            Args.notNull("input", input);

            if (owner != Thread.currentThread())
                throw new RuntimeException("wrong thread!");

            LOG.info("Working on [" + input + "]...");
            invocations++;
            return input + input;
        }
    }

    private static final Logger LOG = Logger.create();

    public void testCrossThreadMethodInvoker() {
        final Service service = new Service();

        final CrossThreadMethodInvoker methodInvoker = new CrossThreadMethodInvoker(2000);
        final CrossThreadProxy<IService> serviceProxy = methodInvoker.createProxy(service, IService.class);

        try {
            methodInvoker.waitForBackgroundThreads();
            fail();
        } catch (final IllegalStateException ex) {
            assertTrue(ex.getMessage().endsWith("is not started!"));
        }

        methodInvoker.start(2);

        new Thread() {
            @Override
            public void run() {
                try {
                    final IService srv = serviceProxy.get();
                    assertEquals("foofoo", srv.work("foo"));
                    try {
                        service.work(null);
                        fail();
                    } catch (final Exception ex) {
                        assertEquals("[input] must not be null", ex.getMessage());
                    }
                    try {
                        service.work("oops");
                        fail();
                    } catch (final Exception ex) {
                        assertEquals("wrong thread!", ex.getMessage());
                    }
                    assertEquals("barbar", srv.work("bar"));
                } finally {
                    methodInvoker.backgroundThreadDone();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    final IService srv = serviceProxy.get();
                    assertEquals("heyhey", srv.work("hey"));
                    assertEquals("hoohoo", srv.work("hoo"));
                } finally {
                    methodInvoker.backgroundThreadDone();
                }
            }
        }.start();

        assertEquals(methodInvoker, serviceProxy.getCrossThreadMethodInvoker());

        methodInvoker.waitForBackgroundThreads();

        assertEquals(4, service.invocations);
    }
}
