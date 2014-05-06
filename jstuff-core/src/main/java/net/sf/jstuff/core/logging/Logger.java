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

import net.sf.jstuff.core.reflection.StackTrace;
import net.sf.jstuff.core.validation.Args;

/**
 * Logger implementation supporting the {@link java.util.Formatter} syntax.
 *
 * Defaults to SLF4J as logging infrastructure and falls back to java.util.logging if SLF4J is not available.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Logger
{
	public static Logger create()
	{
		final String name = StackTrace.getCallingStackTraceElement(Logger.class).getClassName();
		return LoggerConfig.create(name);
	}

	public static Logger create(final Class< ? > clazz)
	{
		Args.notNull("clazz", clazz);
		return LoggerConfig.create(clazz.getName());
	}

	public static Logger create(final String name)
	{
		Args.notNull("name", name);
		return LoggerConfig.create(name);
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
