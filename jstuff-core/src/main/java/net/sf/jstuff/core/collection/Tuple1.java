/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
public class Tuple1<T1> extends Tuple
{
	private static final long serialVersionUID = 1L;

	public static <T1> Tuple1<T1> create(final T1 value1)
	{
		return new Tuple1<T1>(value1);
	}

	public Tuple1(final T1 value1)
	{
		super(value1);
	}

	/*
	 * using explicit cast as workaround for Java 5 compiler bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6302954
	 * "type parameters of <T>T cannot be determined; no unique maximal instance exists for type variable T with upper bounds T1,java.lang.Object"
	 */
	@SuppressWarnings("unchecked")
	public T1 get1()
	{
		return (T1) getTyped(0);
	}
}
