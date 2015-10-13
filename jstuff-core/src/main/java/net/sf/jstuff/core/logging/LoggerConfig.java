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

import java.util.Set;

import net.sf.jstuff.core.collection.WeakIdentityHashSet;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class LoggerConfig
{
	private static final Logger LOG;

	/**
	 * weak set holding all instantiated loggers. required to switch the backing logger implementation during runtime if required.
	 */
	private static final Set<DelegatingLogger> LOGGERS = new WeakIdentityHashSet<DelegatingLogger>(64);

	private static final boolean isSLF4JAvailable;

	private static boolean isPreferSLF4J = false;

	private static boolean isUseSFL4J = false;

	/**
	 * If set to true, method name and line number are added to the log message.
	 * This is esp. helpful in environments where you have no control over the used logger pattern by the underlying logger infrastructure (e.g. in an JEE container).
	 */
	static boolean isDebugMessagePrefixEnabled = false;

	static boolean isCompactExceptionLoggingDisabled = false;

	static
	{
		LinkageError slf4jLinkageError = null;
		try
		{
			@SuppressWarnings("unused")
			final SLF4JLogger test = new SLF4JLogger("");
		}
		catch (final LinkageError err)
		{
			slf4jLinkageError = err;
		}
		isSLF4JAvailable = slf4jLinkageError == null;

		LOG = create(LoggerConfig.class.getName());
		if (slf4jLinkageError != null) LOG.debug(slf4jLinkageError);

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
				LOG.debug("Using SLF4J as logging infrastructure.");
			else
				LOG.debug("Using java.util.logging as logging infrastructure.");
		}
	}

	private LoggerConfig()
	{
		super();
	}
}
