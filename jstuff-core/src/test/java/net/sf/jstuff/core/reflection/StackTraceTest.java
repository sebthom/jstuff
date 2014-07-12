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

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StackTraceTest extends TestCase
{
	private static class Caller1
	{
		private class Caller2
		{
			public void call2()
			{
				ensureEquals("call2", StackTrace.getThisMethodName());
				ensureEquals(34, StackTrace.getThisLineNumber());
				ensureEquals("StackTraceTest.java", StackTrace.getThisFileName());

				ensureEquals("call1", StackTrace.getCallerMethodName());
				ensureEquals(63, StackTrace.getCallerLineNumber());
				ensureEquals("StackTraceTest.java", StackTrace.getCallerFileName());
				ensureEquals(Caller1.class.getName(), StackTrace.getCallerClassName());
				ensureEquals(Caller1.class, StackTrace.getCallerClass());
			}

			private void ensureEquals(final Object a, final Object b)
			{
				assertEquals(a, b);
			}
		}

		private final Caller2 caller2 = new Caller2();

		public void call1()
		{
			caller2.call2();
		}
	}

	public void testStackTrace()
	{
		new Caller1().call1();
	}
}
