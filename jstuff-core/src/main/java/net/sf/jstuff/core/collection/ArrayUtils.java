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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.functional.Function;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ArrayUtils extends org.apache.commons.lang.ArrayUtils
{
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

	public static <T> T[] copy(final T[] array)
	{
		if (array == null) return null;

		return array.clone();
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(final Collection<T> values, final Class<T> itemType)
	{
		if (values == null) return null;

		return values.toArray((T[]) Array.newInstance(itemType, values.size()));
	}

	public static <T> T[] toArray(final T... values)
	{
		return values;
	}

	public static List<Boolean> toList(final boolean[] array)
	{
		if (array == null) return null;

		final ArrayList<Boolean> result = new ArrayList<Boolean>(array.length);
		for (final boolean i : array)
			result.add(i);
		return result;
	}

	public static List<Byte> toList(final byte[] array)
	{
		if (array == null) return null;

		final ArrayList<Byte> result = new ArrayList<Byte>(array.length);
		for (final byte i : array)
			result.add(i);
		return result;
	}

	public static List<Character> toList(final char[] array)
	{
		if (array == null) return null;

		final ArrayList<Character> result = new ArrayList<Character>(array.length);
		for (final char i : array)
			result.add(i);
		return result;
	}

	public static List<Double> toList(final double[] array)
	{
		if (array == null) return null;

		final ArrayList<Double> result = new ArrayList<Double>(array.length);
		for (final double i : array)
			result.add(i);
		return result;
	}

	public static List<Float> toList(final float[] array)
	{
		if (array == null) return null;

		final ArrayList<Float> result = new ArrayList<Float>(array.length);
		for (final float i : array)
			result.add(i);
		return result;
	}

	public static List<Integer> toList(final int[] array)
	{
		if (array == null) return null;

		final ArrayList<Integer> result = new ArrayList<Integer>(array.length);
		for (final int i : array)
			result.add(i);
		return result;
	}

	public static List<Long> toList(final long[] array)
	{
		if (array == null) return null;

		final ArrayList<Long> result = new ArrayList<Long>(array.length);
		for (final long i : array)
			result.add(i);
		return result;
	}

	/**
	 * <b>Hint:</b> If you are looking for a toList(T...) method use {@link CollectionUtils#newArrayList(Object...)}
	 */
	public static List< ? > toList(final Object array)
	{
		if (array == null) return null;

		if (array instanceof Object[]) return CollectionUtils.newArrayList((Object[]) array);
		if (array instanceof byte[]) return toList((byte[]) array);
		if (array instanceof char[]) return toList((char[]) array);
		if (array instanceof short[]) return toList((short[]) array);
		if (array instanceof int[]) return toList((int[]) array);
		if (array instanceof long[]) return toList((long[]) array);
		if (array instanceof double[]) return toList((double[]) array);
		if (array instanceof float[]) return toList((float[]) array);
		if (array instanceof boolean[]) return toList((boolean[]) array);

		throw new IllegalArgumentException("Argument [array] must be an array");
	}

	public static List<Short> toList(final short[] array)
	{
		if (array == null) return null;

		final ArrayList<Short> result = new ArrayList<Short>(array.length);
		for (final short i : array)
			result.add(i);
		return result;
	}

	public static <S, T> T[] transform(final S[] source, final Class<T> targetType,
			final Function< ? super S, ? extends T> op)
	{
		if (source == null) return null;

		@SuppressWarnings("unchecked")
		final T[] target = (T[]) Array.newInstance(targetType, source.length);
		for (int i = 0, l = source.length; i < l; i++)
			target[i] = op.apply(source[i]);
		return target;
	}

	protected ArrayUtils()
	{
		super();
	}
}
