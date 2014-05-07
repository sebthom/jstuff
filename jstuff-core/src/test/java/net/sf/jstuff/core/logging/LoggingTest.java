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
	protected void tearDown() throws Exception
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

	private static interface InterfaceA
	{
		Object[] methodA(final int a, final String b, final String... c);
	}

	private static interface InterfaceB
	{
		void methodB();
	}

	private static class Entity implements InterfaceA, InterfaceB
	{
		public Object[] methodA(final int a, final String b, final String... c)
		{
			Threads.sleep(1000);
			return new Object[]{b, c};
		}

		public void methodB()
		{
			Threads.sleep(1000);
		}
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
		final Handler h = new Handler()
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
			};
		ROOT_LOGGER.addHandler(h);
		try
		{
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
		finally
		{
			ROOT_LOGGER.removeHandler(h);
		}
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

	public void testCreateLogged()
	{
		LoggerConfig.setPreferSLF4J(false);

		for (final Handler handler : ROOT_LOGGER.getHandlers())
			handler.setLevel(java.util.logging.Level.ALL);
		java.util.logging.Logger.getLogger(Entity.class.getName()).setLevel(java.util.logging.Level.FINEST);
		final int[] count = new int[1];
		final Handler h = new Handler()
			{
				@Override
				public void publish(final LogRecord record)
				{
					count[0]++;
					switch (count[0])
					{
						case 1 :
							assertEquals("methodA ENTRY >> ([1, foo, [bar]])", record.getMessage());
							break;
						case 2 :
							assertTrue(record.getMessage().startsWith("methodA EXIT  << [foo, [bar]] "));
							break;
						case 3 :
							assertEquals("methodB ENTRY >> ()", record.getMessage());
							break;
						case 4 :
							assertTrue(record.getMessage().startsWith("methodB EXIT  << *void* "));
							break;
					}
				}

				@Override
				public void flush()
				{}

				@Override
				public void close() throws SecurityException
				{}
			};
		ROOT_LOGGER.addHandler(h);
		try
		{
			final Entity entity = new Entity();
			final InterfaceA wrapped = Logger.createLogged(entity, InterfaceA.class, InterfaceB.class);
			wrapped.methodA(1, "foo", "bar");

			((InterfaceB) wrapped).methodB();
		}
		finally
		{
			ROOT_LOGGER.removeHandler(h);
		}
	}
}
