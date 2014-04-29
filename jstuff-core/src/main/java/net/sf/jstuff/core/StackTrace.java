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
package net.sf.jstuff.core;

import java.util.Arrays;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class StackTrace
{
	private static StackTraceElement _getCallingStackTraceElement()
	{
		final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stes.length; i++)
			if ("_getCallingStackTraceElement".equals(stes[i].getMethodName())) return stes[i + 3];

		// should never be reached
		throw new IllegalStateException("Unexpected stack trace " + Arrays.toString(stes));
	}

	private static StackTraceElement _getThisStackTraceElement()
	{
		final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		for (int i = 0; i < stes.length; i++)
			if ("_getThisStackTraceElement".equals(stes[i].getMethodName())) return stes[i + 2];

		// should never be reached
		throw new IllegalStateException("Unexpected stack trace " + Arrays.toString(stes));
	}

	public static String getCallingClassName()
	{
		final StackTraceElement ste = _getCallingStackTraceElement();
		return ste.getClassName();
	}

	public static String getCallingClassSimpleName()
	{
		final StackTraceElement ste = _getCallingStackTraceElement();
		return StringUtils.substringAfterLast(ste.getClassName(), ".");
	}

	public static String getCallingFileName()
	{
		final StackTraceElement ste = _getCallingStackTraceElement();
		return ste.getFileName();
	}

	public static int getCallingLineNumber()
	{
		final StackTraceElement ste = _getCallingStackTraceElement();
		return ste.getLineNumber();
	}

	public static String getCallingMethodName()
	{
		final StackTraceElement ste = _getCallingStackTraceElement();
		return ste.getMethodName();
	}

	public static StackTraceElement getCallingStackTraceElement()
	{
		final StackTraceElement ste = _getCallingStackTraceElement();
		return ste;
	}

	public static StackTraceElement getCallingStackTraceElement(final Class< ? > calledClass)
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

	public static StackTraceElement getCallingStackTraceElement(final String calledClassName)
	{
		final StackTraceElement[] stes = Thread.currentThread().getStackTrace();
		boolean foundCalledClassInStackTrace = false;
		for (final StackTraceElement curr : stes)
			if (calledClassName.equals(curr.getClassName()))
				foundCalledClassInStackTrace = true;
			else if (foundCalledClassInStackTrace) return curr;
		return null;
	}

	public static String getThisClassName()
	{
		final StackTraceElement ste = _getThisStackTraceElement();
		return ste.getClassName();
	}

	public static String getThisClassSimpleName()
	{
		final StackTraceElement ste = _getThisStackTraceElement();
		return StringUtils.substringAfterLast(ste.getClassName(), ".");
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