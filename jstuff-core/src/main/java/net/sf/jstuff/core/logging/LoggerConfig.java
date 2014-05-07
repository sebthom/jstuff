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

import java.util.Set;

import net.sf.jstuff.core.collection.WeakIdentityHashSet;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("null")
public final class LoggerConfig
{
	private static final Logger LOG;

	private static final boolean isSLF4JAvailable;
	private static boolean isPreferSLF4J;
	private static boolean isUseSFL4J;

	private static final Set<DelegatingLogger> LOGGERS = new WeakIdentityHashSet<DelegatingLogger>(64);

	/**
	 * If set to true, method name and line number are added to the log message.
	 * This is esp. helpful in environments where you have no control over the used logger pattern by the underlying logger infrastructure (e.g. in an JEE container).
	 */
	static boolean isDebugMessagePrefixEnabled = true;
	static boolean isCompactExceptionLoggingDisabled = false;

	static
	{
		Class< ? > slf4jClass = null;
		try
		{
			slf4jClass = org.slf4j.Logger.class;
		}
		catch (final LinkageError err)
		{}
		isSLF4JAvailable = slf4jClass != null;

		LOG = create(LoggerConfig.class.getName());

		setPreferSLF4J("true".equals(System.getProperty(Logger.class.getName() + ".preferSLF4J", "true")));
	}

	static Logger create(final String name)
	{
		Args.notNull("name", name);

		final DelegatingLogger logger = new DelegatingLogger(isUseSFL4J ? new SLF4JLogger(name) : new JULLogger(name));
		synchronized (LOGGERS)
		{
			LOGGERS.add(logger);
		}
		return logger;
	}

	public static boolean isCompactExceptionLoggingEnabled()
	{
		return !isCompactExceptionLoggingDisabled;
	}

	public static boolean isDebugMessagePrefixEnabled()
	{
		return isDebugMessagePrefixEnabled;
	}

	public static boolean isPreferSLF4J()
	{
		return isPreferSLF4J;
	}

	public static void setCompactExceptionLogging(final boolean enabled)
	{
		isCompactExceptionLoggingDisabled = !enabled;
	}

	public static void setDebugMessagePrefixEnabled(final boolean enabled)
	{
		LoggerConfig.isDebugMessagePrefixEnabled = enabled;
	}

	public synchronized static void setPreferSLF4J(final boolean value)
	{
		isPreferSLF4J = value;
		final boolean old = isUseSFL4J;
		isUseSFL4J = isSLF4JAvailable ? isPreferSLF4J : false;
		if (old == isUseSFL4J) return;

		synchronized (LOGGERS)
		{
			// hot replacing the underlying logger infrastructure
			for (final DelegatingLogger logger : LOGGERS)
			{
				final String name = logger.getName();
				logger.setDelegate(isUseSFL4J ? new SLF4JLogger(name) : new JULLogger(name));
			}

			if (isUseSFL4J)
				LOG.info("Using SLF4J as logging infrastructure.");
			else
				LOG.info("Using java.util.logging as logging infrastructure.");
		}
	}

	private LoggerConfig()
	{
		super();
	}
}
