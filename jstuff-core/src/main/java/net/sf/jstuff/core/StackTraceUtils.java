/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StackTraceUtils
{
	private static StackTraceElement _getCallingStackTraceElement()
	{
		return Thread.currentThread().getStackTrace()[4];
	}

	private static StackTraceElement _getThisStackTraceElement()
	{
		return Thread.currentThread().getStackTrace()[3];
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
		Assert.argumentNotNull("t", t);

		final StackTraceElement[] stack = t.getStackTrace();
		final StackTraceElement[] newStack = new StackTraceElement[stack.length - 1];
		System.arraycopy(stack, 1, newStack, 0, stack.length - 1);
		t.setStackTrace(newStack);
		return t;
	}

	protected StackTraceUtils()
	{
		super();
	}
}