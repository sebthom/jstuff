/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
package net.sf.jstuff.core.collection;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Tuple2<T1, T2> extends Tuple
{
	private static final long serialVersionUID = 1L;

	public static <T1, T2> Tuple2<T1, T2> create(final T1 value1, final T2 value2)
	{
		return new Tuple2<T1, T2>(value1, value2);
	}

	public Tuple2(final T1 value1, final T2 value2)
	{
		super(value1, value2);
	}

	/*
	 * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
	 * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
	 */
	@SuppressWarnings("unchecked")
	public T1 get1()
	{
		return (T1) super.getTyped(0);
	}

	/*
	 * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
	 * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
	 */
	@SuppressWarnings("unchecked")
	public T2 get2()
	{
		return (T2) super.getTyped(1);
	}
}
