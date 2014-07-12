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
package net.sf.jstuff.core.reflection;

import java.util.Arrays;

import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StackTrace
{
	private static final class CallerResolver extends SecurityManager
	{
		private static final CallerResolver INSTANCE = new CallerResolver();

		private Class< ? > getCallerClass()
		{
			System.out.println(Arrays.toString(getClassContext()));
			return getClassContext()[4];
		}
	}

	private static StackTraceElement _getCallerStackTraceElement()
	{
		return Thread.currentThread().getStackTrace()[4];
	}

	private static StackTraceElement _getThisStackTraceElement()
	{
		return Thread.currentThread().getStackTrace()[3];
	}

	public static Class< ? > getCallerClass()
	{
		return CallerResolver.INSTANCE.getCallerClass();
	}

	public static String getCallerClassName()
	{
		final StackTraceElement ste = _getCallerStackTraceElement();
		return ste.getClassName();
	}

	public static String getCallerClassSimpleName()
	{
		final StackTraceElement ste = _getCallerStackTraceElement();
		return StringUtils.substringAfterLast(ste.getClassName(), ".");
	}

	public static String getCallerFileName()
	{
		final StackTraceElement ste = _getCallerStackTraceElement();
		return ste.getFileName();
	}

	public static int getCallerLineNumber()
	{
		final StackTraceElement ste = _getCallerStackTraceElement();
		return ste.getLineNumber();
	}

	public static String getCallerMethodName()
	{
		final StackTraceElement ste = _getCallerStackTraceElement();
		return ste.getMethodName();
	}

	public static StackTraceElement getCallerStackTraceElement()
	{
		final StackTraceElement ste = _getCallerStackTraceElement();
		return ste;
	}

	public static StackTraceElement getCallerStackTraceElement(final Class< ? > calledClass)
	{
		final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		final String calledClassName = calledClass.getName();
		boolean foundCalledClassInStackTrace = false;
		for (final StackTraceElement curr : stes)
			if (calledClassName.equals(curr.getClassName()))
				foundCalledClassInStackTrace = true;
			else if (foundCalledClassInStackTrace) return curr;
		return null;
	}

	public static StackTraceElement getCallerStackTraceElement(final String calledClassName)
	{
		final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		boolean foundCalledClassInStackTrace = false;
		for (final StackTraceElement curr : stes)
			if (calledClassName.equals(curr.getClassName()))
				foundCalledClassInStackTrace = true;
			else if (foundCalledClassInStackTrace) return curr;
		return null;
	}

	public static String getThisFileName()
	{
		final StackTraceElement ste = _getThisStackTraceElement();
		return ste.getFileName();
	}

	public static int getThisLineNumber()
	{
		final StackTraceElement ste = _getThisStackTraceElement();
		return ste.getLineNumber();
	}

	public static String getThisMethodName()
	{
		final StackTraceElement ste = _getThisStackTraceElement();
		return ste.getMethodName();
	}

	public static StackTraceElement getThisStackTraceElement()
	{
		final StackTraceElement ste = _getThisStackTraceElement();
		return ste;
	}

	public static <T extends Throwable> T removeFirstStackTraceElement(final T t)
	{
		Args.notNull("t", t);

		final StackTraceElement[] stack = t.getStackTrace();
		final StackTraceElement[] newStack = new StackTraceElement[stack.length - 1];
		System.arraycopy(stack, 1, newStack, 0, stack.length - 1);
		t.setStackTrace(newStack);
		return t;
	}
}