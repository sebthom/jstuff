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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import net.sf.jstuff.core.reflection.Methods;
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
	protected static final String METHOD_ENTRY_MARKER = "ENTRY >>";
	protected static final String METHOD_EXIT_MARKER = "EXIT  <<";
	protected static final String INTERNAL_PACKAGE_NAME = Logger.class.getPackage().getName();

	protected static String argToString(final Object object)
	{
		if (object == null) return "null";
		if (object.getClass().isArray()) return Arrays.deepToString((Object[]) object);
		if (object instanceof String) return "\"" + object + "\"";
		return object.toString();
	}

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

	@SuppressWarnings("unchecked")
	public static <I> I createLogged(final I object, final Class<I> primaryInterface, final Class< ? >... secondaryInterfaces)
	{
		Args.notNull("object", object);
		Args.notNull("primaryInterface", primaryInterface);

		final Class< ? >[] interfaces;
		if (secondaryInterfaces == null || secondaryInterfaces.length == 0)
			interfaces = new Class< ? >[]{primaryInterface};
		else
		{
			interfaces = new Class< ? >[secondaryInterfaces.length + 1];
			interfaces[0] = primaryInterface;
			System.arraycopy(secondaryInterfaces, 0, interfaces, 1, secondaryInterfaces.length);
		}

		return (I) Proxy.newProxyInstance(object.getClass().getClassLoader(), interfaces, new InvocationHandler()
			{
				final Logger log = Logger.create(object.getClass());

				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					if (log.isTraceEnabled())
					{
						final long start = System.currentTimeMillis();
						if (args == null)
							log.trace(method.getName() + " " + METHOD_ENTRY_MARKER + " ()");
						else
							log.trace(method.getName() + " " + METHOD_ENTRY_MARKER + " (" + Arrays.deepToString(args) + ")");
						final Object returnValue = method.invoke(object, args);
						final String elapsed = String.format("%,d", System.currentTimeMillis() - start);
						if (Methods.isReturningVoid(method))
							log.trace(method.getName() + " " + METHOD_EXIT_MARKER + " *void* " + elapsed + "ms");
						else
							log.trace(method.getName() + " " + METHOD_EXIT_MARKER + " " + argToString(returnValue) + " " + elapsed + "ms");
						return returnValue;
					}
					return method.invoke(object, args);
				}
			});
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

	public abstract void debug(final Throwable ex, final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void debug(final Throwable ex, final String messageTemplate, final Object... args);

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

	public abstract void info(final Throwable ex, final String msg);

	/**
	 * Logs the instantiation of the given object at INFO level including the corresponding class's implementation version.
	 */
	public abstract void infoNew(final Object newInstance);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void info(final Throwable ex, final String messageTemplate, final Object... args);

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

	public abstract void trace(final Throwable ex, final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void trace(final Throwable ex, final String messageTemplate, final Object... args);

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

	public abstract void warn(final Throwable ex, final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void warn(final Throwable ex, final String messageTemplate, final Object... args);

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

	public abstract void error(final Throwable ex, final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void error(final Throwable ex, final String messageTemplate, final Object... args);

	public abstract void fatal(final Throwable ex);

	public abstract void fatal(final Throwable ex, final String msg);

	/**
	 * @param messageTemplate A format string understandable by {@link java.util.Formatter}.
	 * @param args Arguments referenced by the format specifiers in the message template. If there are more arguments than format specifiers, the extra arguments are ignored.
	 */
	public abstract void fatal(final Throwable ex, final String messageTemplate, final Object... args);
}
