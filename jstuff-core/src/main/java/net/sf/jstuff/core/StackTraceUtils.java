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
	public static String getCallingClassName()
	{
		return Thread.currentThread().getStackTrace()[3].getClassName();
	}

	public static String getCallingClassSimpleName()
	{
		return StringUtils.substringAfterLast(Thread.currentThread().getStackTrace()[3].getClassName(), ".");
	}

	public static String getCallingFileName()
	{
		return Thread.currentThread().getStackTrace()[3].getFileName();
	}

	public static int getCallingLineNumber()
	{
		return Thread.currentThread().getStackTrace()[3].getLineNumber();
	}

	public static String getCallingMethodName()
	{
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	public static StackTraceElement getCallingStackTraceElement()
	{
		return Thread.currentThread().getStackTrace()[3];
	}

	public static String getThisClassName()
	{
		return Thread.currentThread().getStackTrace()[2].getClassName();
	}

	public static String getThisClassSimpleName()
	{
		return StringUtils.substringAfterLast(Thread.currentThread().getStackTrace()[2].getClassName(), ".");
	}

	public static String getThisFileName()
	{
		return Thread.currentThread().getStackTrace()[2].getFileName();
	}

	public static int getThisLineNumber()
	{
		return Thread.currentThread().getStackTrace()[2].getLineNumber();
	}

	public static String getThisMethodName()
	{
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	public static StackTraceElement getThisStackTraceElement()
	{
		return Thread.currentThread().getStackTrace()[2];
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