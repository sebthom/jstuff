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

import net.sf.jstuff.core.StackTrace;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("null")
public abstract class Logger
{
	private static final boolean isSLF4JPresent;
	public static boolean isUseSLF4J;

	/**
	 * If set to true, method name and line number are added to the log message.
	 * This is esp. helpful in environments where you have no control over the used logger pattern by the underlying logger infrastructure (e.g. in an JEE container).
	 */
	public static boolean addSourceLocationToLogMessageIfDebugging = true;

	private static final Logger LOG;

	static
	{
		Class< ? > slf4jClass = null;
		try
		{
			slf4jClass = org.slf4j.Logger.class;
		}
		catch (final LinkageError err)
		{}
		isSLF4JPresent = slf4jClass != null;

		if ("true".equals(System.getProperty(Logger.class.getName() + ".preferJUL")))
			isUseSLF4J = false;
		else
			isUseSLF4J = isSLF4JPresent;

		LOG = Logger.create(Logger.class);
		if (isUseSLF4J)
			LOG.info("Using SLF4J as logging infrastructure.");
		else
			LOG.info("Using java.util.logging as logging infrastructure.");
	}

	public static Logger create()
	{
		final String name = StackTrace.getCallingStackTraceElement(Logger.class).getClassName();

		if (isUseSLF4J) return new LoggerSLF4J(name);
		return new LoggerJUL(name);
	}

	public static Logger create(final Class< ? > clazz)
	{
		Args.notNull("clazz", clazz);

		if (isUseSLF4J) return new LoggerSLF4J(clazz.getName());
		return new LoggerJUL(clazz.getName());
	}

	public static Logger create(final String name)
	{
		Args.notNull("name", name);

		if (isUseSLF4J) return new LoggerSLF4J(name);
		return new LoggerJUL(name);
	}

	/**
	 * @return the loggers name
	 */
	public abstract String getName();

	public abstract boolean isDebugEnabled();

	public abstract boolean isErrorEnabled();

	public abstract boolean isInfoEnabled();

	public abstract boolean isTraceEnabled();

	public abstract boolean isWarnEnabled();

	public abstract void debug(final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the message template.
	 */
	public abstract void debug(final String messageTemplate, final Object arg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void debug(final String messageTemplate, final Object arg1, final Object arg2);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 * @param arg5 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void debug(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5);

	public abstract void debug(final Throwable ex);

	public abstract void debug(final String msg, final Throwable ex);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void debug(final String messageTemplate, final Throwable ex, final Object... args);

	public abstract void info(final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the message template.
	 */
	public abstract void info(final String messageTemplate, final Object arg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void info(final String messageTemplate, final Object arg1, final Object arg2);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 * @param arg5 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void info(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5);

	public abstract void info(final Throwable ex);

	public abstract void info(final String msg, final Throwable ex);

	/**
	 * Logs the instantiation of the given object at INFO level including the corresponding class's implementation version.
	 */
	public abstract void infoNew(final Object newInstance);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void info(final String messageTemplate, final Throwable ex, final Object... args);

	public abstract void trace(final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the message template.
	 */
	public abstract void trace(final String messageTemplate, final Object arg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void trace(final String messageTemplate, final Object arg1, final Object arg2);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 * @param arg5 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void trace(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5);

	public abstract void trace(final String msg, final Throwable ex);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void trace(final String messageTemplate, final Throwable ex, final Object... args);

	public abstract void trace(final Throwable ex);

	/**
	 * Logs a method entry
	 */
	public abstract void traceEntry();

	/**
	 * Logs a method entry
	 */
	public abstract void traceEntry(final Object arg1);

	/**
	 * Logs a method entry
	 */
	public abstract void traceEntry(final Object arg1, final Object arg2);

	/**
	 * Logs a method entry
	 */
	public abstract void traceEntry(final Object arg1, final Object arg2, final Object arg3);

	/**
	 * Logs a method entry
	 */
	public abstract void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4);

	/**
	 * Logs a method entry
	 */
	public abstract void traceEntry(final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5);

	/**
	 * Logs a method exit
	 */
	public abstract void traceExit();

	/**
	 * Logs a method exit
	 *
	 * @param returnValue the returnValue of the given method
	 */
	public abstract <T> T traceExit(final T returnValue);

	public abstract void warn(final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the message template.
	 */
	public abstract void warn(final String messageTemplate, final Object arg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void warn(final String messageTemplate, final Object arg1, final Object arg2);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 * @param arg5 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void warn(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5);

	public abstract void warn(final Throwable ex);

	public abstract void warn(final String msg, final Throwable ex);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void warn(final String messageTemplate, final Throwable ex, final Object... args);

	public abstract void error(final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg Argument referenced by the format specifiers in the message template.
	 */
	public abstract void error(final String messageTemplate, final Object arg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void error(final String messageTemplate, final Object arg1, final Object arg2);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param arg1 Argument referenced by the format specifiers in the message template.
	 * @param arg2 Argument referenced by the format specifiers in the message template.
	 * @param arg3 Argument referenced by the format specifiers in the message template.
	 * @param arg4 Argument referenced by the format specifiers in the message template.
	 * @param arg5 Argument referenced by the format specifiers in the message template.
	 */
	public abstract void error(final String messageTemplate, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5);

	public abstract void error(final Throwable ex);

	public abstract void error(final String msg, final Throwable ex);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void error(final String messageTemplate, final Throwable ex, final Object... args);

	public abstract void fatal(final Throwable ex);

	public abstract void fatal(final String msg, final Throwable ex);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void fatal(final String messageTemplate, final Throwable ex, final Object... args);
}
