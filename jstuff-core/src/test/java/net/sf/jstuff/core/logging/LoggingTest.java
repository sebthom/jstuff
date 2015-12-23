/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LoggingTest extends TestCase {
    private static class Entity implements InterfaceA, InterfaceB {
        public Object[] methodA(final int a, final String b, final String... c) {
            Threads.sleep(1000);
            return new Object[] { b, c };
        }

        public void methodB() {
            Threads.sleep(1000);
        }
    }

    private static interface InterfaceA {
        Object[] methodA(int a, String b, String... c);
    }

    private static interface InterfaceB {
        void methodB();
    }

    private static final java.util.logging.Logger ROOT_LOGGER = java.util.logging.Logger.getLogger("");
    private static final Logger LOG = Logger.create();
    private static final Formatter ROOT_LOGGER_FORMATTER = new Formatter() {
        private final DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

        @Override
        public synchronized String format(final LogRecord record) {
            final StringBuilder sb = new StringBuilder(1000);
            sb.append(df.format(new Date(record.getMillis()))).append(" ");
            sb.append(record.getLevel().getName().charAt(0)).append(" ");
            {
                sb.append("[");
                if (record.getSourceClassName() != null)
                    sb.append(record.getSourceClassName());
                else
                    sb.append(record.getLoggerName());
                if (record.getSourceMethodName() != null)
                    sb.append("#").append(record.getSourceMethodName()).append("()");
                sb.append("] ");
            }
            sb.append(formatMessage(record));
            if (record.getThrown() != null) {
                final StringWriter errors = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(errors));
                sb.append(errors);
            }
            sb.append("\n");
            return sb.toString();
        }
    };

    public void atest1LoggingJUL() {
        LoggerConfig.setPreferSLF4J(false);
        LoggerConfig.setCompactExceptionLogging(true);
        LoggerConfig.setDebugMessagePrefixEnabled(true);

        assertTrue(((DelegatingLogger) LOG).getDelegate() instanceof JULLogger);

        genericLoggerTest();
    }

    public void atestCreateLogged() {
        LoggerConfig.setPreferSLF4J(false);
        LoggerConfig.setCompactExceptionLogging(false);
        LoggerConfig.setDebugMessagePrefixEnabled(true);

        for (final Handler handler : ROOT_LOGGER.getHandlers())
            handler.setLevel(java.util.logging.Level.ALL);
        java.util.logging.Logger.getLogger(Entity.class.getName()).setLevel(java.util.logging.Level.FINEST);
        final int[] count = new int[1];
        final Handler h = new Handler() {
            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(final LogRecord record) {
                count[0]++;
                switch (count[0]) {
                    case 1:
                        assertEquals("methodA():ENTRY >> (a: 1, b: \"foo\", c: [bar])", record.getMessage());
                        break;
                    case 2:
                        assertTrue(record.getMessage().startsWith("methodA():EXIT  << [foo, [bar]] "));
                        break;
                    case 3:
                        assertEquals("methodB():ENTRY >> ()", record.getMessage());
                        break;
                    case 4:
                        assertTrue(record.getMessage().startsWith("methodB():EXIT  << *void* "));
                        break;
                }
            }
        };
        ROOT_LOGGER.addHandler(h);
        try {
            final Entity entity = new Entity();
            final InterfaceA wrapped = Logger.createLogged(entity, InterfaceA.class, InterfaceB.class);
            wrapped.methodA(1, "foo", "bar");

            ((InterfaceB) wrapped).methodB();
        } finally {
            ROOT_LOGGER.removeHandler(h);
        }
    }

    private void coolMethod(final String label, final int number, final boolean flag, final Class<?> clazz, final Object obj) {
        LOG.entry();
        LOG.entry(label, number);
        LOG.entry(label, number, flag, clazz, obj);
        LOG.error("ERROR");
        try {
            throw new RuntimeException("Cannot process request.");
        } catch (final Exception ex) {
            LOG.error(ex);
        }
        try {
            throw new RuntimeException("Cannot initialize service.");
        } catch (final Exception ex) {
            LOG.fatal(ex);
        }
        LOG.warn("WARN");
        LOG.info("INFO");
        LOG.debug("DEBUG");
        LOG.trace("TRACE");
        LOG.exit();
        LOG.exit(1234);
    }

    private void genericLoggerTest() {
        assertNotNull(LOG);
        assertEquals(LoggingTest.class.getName(), LOG.getName());

        for (final Handler handler : ROOT_LOGGER.getHandlers())
            handler.setLevel(java.util.logging.Level.ALL);

        final int[] count = { 0 };
        final Handler h = new Handler() {
            @Override
            public void close() throws SecurityException {
            }

            @Override
            public void flush() {
            }

            @Override
            public void publish(final LogRecord record) {
                count[0]++;
            }
        };
        ROOT_LOGGER.addHandler(h);
        try {
            System.out.println("LOGGER LEVEL = DEFAULT (INFO) ****************");
            assertTrue(LOG.isInfoEnabled());
            assertFalse(LOG.isDebugEnabled());
            count[0] = 0;
            coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
            assertEquals(5, count[0]);

            Threads.sleep(50);
            System.out.println("LOGGER LEVEL = INFO **************************");
            java.util.logging.Logger.getLogger(LOG.getName()).setLevel(java.util.logging.Level.INFO);
            assertTrue(LOG.isInfoEnabled());
            assertFalse(LOG.isDebugEnabled());
            count[0] = 0;
            coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
            assertEquals(5, count[0]);

            Threads.sleep(50);
            System.out.println("LOGGER LEVEL = FINEST **************************");
            java.util.logging.Logger.getLogger(LOG.getName()).setLevel(java.util.logging.Level.FINEST);
            assertTrue(LOG.isTraceEnabled());
            count[0] = 0;
            coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
            assertEquals(12, count[0]);

            Threads.sleep(50);
            System.out.println("LOGGER LEVEL = SEVERE (INHERTIED) ************");
            ROOT_LOGGER.setLevel(java.util.logging.Level.SEVERE);
            assertTrue(LOG.isTraceEnabled());
            java.util.logging.Logger.getLogger(LOG.getName()).setLevel(null);
            assertFalse(LOG.isWarnEnabled());
            assertTrue(LOG.isErrorEnabled());
            count[0] = 0;
            coolMethod("hello", 5, true, Void.class, Integer.valueOf(42));
            assertEquals(3, count[0]);
        } finally {
            ROOT_LOGGER.removeHandler(h);
        }
    }

    @Override
    protected void setUp() throws Exception {
        System.out.println(LOG.isDebugEnabled());

        System.out.println("############################################");
        System.out.println("Resetting loggers...");
        System.out.println("############################################");
        ROOT_LOGGER.setLevel(java.util.logging.Level.INFO);
        for (final Handler handler : ROOT_LOGGER.getHandlers()) {
            handler.setLevel(java.util.logging.Level.INFO);
            if (handler instanceof ConsoleHandler)
                ((ConsoleHandler) handler).setFormatter(ROOT_LOGGER_FORMATTER);
        }

        java.util.logging.Logger.getLogger(LoggingTest.class.getName()).setLevel(null);
        Threads.sleep(50);
    }

    public void test2LoggingSLF4J() {
        System.out.println(LOG.isDebugEnabled());

        LoggerConfig.setPreferSLF4J(true);
        LoggerConfig.setCompactExceptionLogging(false);
        LoggerConfig.setDebugMessagePrefixEnabled(false);

        assertTrue(((DelegatingLogger) LOG).getDelegate() instanceof SLF4JLogger);

        genericLoggerTest();
    }
}
