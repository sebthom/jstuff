/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import junit.framework.TestCase;
import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LoggingTest extends TestCase
{
	private static final java.util.logging.Logger ROOT_LOGGER = java.util.logging.Logger.getLogger("");

	private static final Logger LOG = Logger.create();

	@Override
	protected void setUp() throws Exception
	{
		System.out.println("############################################");
		System.out.println("Resetting loggers...");
		System.out.println("############################################");
		ROOT_LOGGER.setLevel(java.util.logging.Level.INFO);
		for (final Handler handler : ROOT_LOGGER.getHandlers())
			handler.setLevel(java.util.logging.Level.INFO);

		java.util.logging.Logger.getLogger(LoggingTest.class.getName()).setLevel(null);
		Threads.sleep(50);
	}

	private void coolMethod()
	{
		LOG.traceEntry();
		LOG.traceEntry("foo", 23);
		LOG.traceEntry("foo", 23, true, Void.class, Integer.valueOf(43));
		LOG.error("ERROR");
		LOG.error(new RuntimeException("Cannot process request."));
		LOG.fatal(new RuntimeException("Cannot initialize service."));
		LOG.warn("WARN");
		LOG.info("INFO");
		LOG.debug("DEBUG");
		LOG.trace("TRACE");
		LOG.traceExit();
		LOG.traceExit(1234);
	}

	private void genericLoggerTest()
	{
		assertNotNull(LOG);
		assertEquals(LoggingTest.class.getName(), LOG.getName());

		for (final Handler handler : ROOT_LOGGER.getHandlers())
			handler.setLevel(java.util.logging.Level.ALL);

		final int[] count = {0};
		ROOT_LOGGER.addHandler(new Handler()
			{
				@Override
				public void publish(final LogRecord record)
				{
					count[0]++;
				}

				@Override
				public void flush()
				{}

				@Override
				public void close() throws SecurityException
				{}
			});

		System.out.println("LOGGER LEVEL = DEFAULT (INFO) ****************");
		assertEquals(true, LOG.isInfoEnabled());
		assertEquals(false, LOG.isDebugEnabled());
		count[0] = 0;
		coolMethod();
		assertEquals(5, count[0]);

		Threads.sleep(50);
		System.out.println("LOGGER LEVEL = INFO **************************");
		java.util.logging.Logger.getLogger(LOG.getName()).setLevel(java.util.logging.Level.INFO);
		assertEquals(true, LOG.isInfoEnabled());
		assertEquals(false, LOG.isDebugEnabled());
		count[0] = 0;
		coolMethod();
		assertEquals(5, count[0]);

		Threads.sleep(50);
		System.out.println("LOGGER LEVEL = ALL **************************");
		java.util.logging.Logger.getLogger(LOG.getName()).setLevel(java.util.logging.Level.ALL);
		assertEquals(true, LOG.isTraceEnabled());
		count[0] = 0;
		coolMethod();
		assertEquals(12, count[0]);

		Threads.sleep(50);
		System.out.println("LOGGER LEVEL = SEVERE (INHERTIED) ************");
		ROOT_LOGGER.setLevel(java.util.logging.Level.SEVERE);
		assertEquals(true, LOG.isTraceEnabled());
		java.util.logging.Logger.getLogger(LOG.getName()).setLevel(null);
		assertEquals(false, LOG.isWarnEnabled());
		assertEquals(true, LOG.isErrorEnabled());
		count[0] = 0;
		coolMethod();
		assertEquals(3, count[0]);
	}

	public void test1LoggingJUL()
	{
		LoggerConfig.setPreferSLF4J(false);
		LoggerConfig.setCompactExceptionLogging(true);
		LoggerConfig.setDebugMessagePrefixEnabled(true);

		assertTrue(((DelegatingLogger) LOG).getDelegate() instanceof JULLogger);

		genericLoggerTest();
	}

	public void test2LoggingSLF4J()
	{
		LoggerConfig.setPreferSLF4J(true);
		LoggerConfig.setCompactExceptionLogging(false);
		LoggerConfig.setDebugMessagePrefixEnabled(false);

		assertTrue(((DelegatingLogger) LOG).getDelegate() instanceof SLF4JLogger);

		genericLoggerTest();
	}
}
