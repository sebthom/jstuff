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
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jstuff.core.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayUtils extends org.apache.commons.lang.ArrayUtils
{

	public static <T> T[] asArray(final T... values)
	{
		return values;
	}

	public static List< ? > asList(final Object array)
	{
		Assert.argumentNotNull("array", array);

		if (array instanceof Object[]) return Arrays.asList((Object[]) array);
		if (array instanceof byte[])
		{
			final byte[] arrayCasted = (byte[]) array;
			final List<Byte> result = new ArrayList<Byte>(arrayCasted.length);
			for (final byte i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof char[])
		{
			final char[] arrayCasted = (char[]) array;
			final List<Character> result = new ArrayList<Character>(arrayCasted.length);
			for (final char i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof short[])
		{
			final short[] arrayCasted = (short[]) array;
			final List<Short> result = new ArrayList<Short>(arrayCasted.length);
			for (final short i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof int[])
		{
			final int[] arrayCasted = (int[]) array;
			final List<Integer> result = new ArrayList<Integer>(arrayCasted.length);
			for (final int i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof long[])
		{
			final long[] arrayCasted = (long[]) array;
			final List<Long> result = new ArrayList<Long>(arrayCasted.length);
			for (final long i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof double[])
		{
			final double[] arrayCasted = (double[]) array;
			final List<Double> result = new ArrayList<Double>(arrayCasted.length);
			for (final double i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof float[])
		{
			final float[] arrayCasted = (float[]) array;
			final List<Float> result = new ArrayList<Float>(arrayCasted.length);
			for (final float i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof boolean[])
		{
			final boolean[] arrayCasted = (boolean[]) array;
			final List<Boolean> result = new ArrayList<Boolean>(arrayCasted.length);
			for (final boolean i : arrayCasted)
				result.add(i);
			return result;
		}

		throw new IllegalArgumentException("Argument [array] must be an array");
	}

	public static <T> boolean containsEqual(final T[] theArray, final T theItem)
	{
		Assert.argumentNotNull("theArray", theArray);

		for (final T t : theArray)
		{
			if (t == theItem) return true;
			if (t != null && t.equals(theItem)) return true;
		}
		return false;
	}

	public static <T> boolean containsSame(final T[] theArray, final T theItem)
	{
		Assert.argumentNotNull("theArray", theArray);

		for (final T t : theArray)
			if (t == theItem) return true;
		return false;
	}

	protected ArrayUtils()
	{
		super();
	}
}
