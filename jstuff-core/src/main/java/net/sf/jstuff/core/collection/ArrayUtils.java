/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.jstuff.core.functional.Accept;
import net.sf.jstuff.core.functional.Function;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {
   public static <T> boolean containsIdentical(final T[] array, final T theItem) {
      Args.notNull("array", array);

      for (final T t : array)
         if (t == theItem)
            return true;
      return false;
   }

   /**
    * Returns a new list with all items accepted by the filter
    *
    * @throws IllegalArgumentException if <code>accept == null</code>
    */
   @SuppressWarnings({"unchecked"})
   public static <T> T[] filter(final Accept<? super T> accept, final T... array) throws IllegalArgumentException {
      if (array == null)
         return null;
      if (array.length == 0)
         return array;
      Args.notNull("accept", accept);
      final ArrayList<T> result = CollectionUtils.newArrayList();
      for (final T item : array)
         if (accept.accept(item)) {
            result.add(item);
         }

      return result.toArray((T[]) Array.newInstance(array.getClass().getComponentType(), result.size()));
   }

   /**
    * @return all items that are contained in all arrays.
    */
   @SuppressWarnings("unchecked")
   public static <T> T[] intersect(final T[]... arrays) {
      if (arrays == null)
         return null;

      final Class<?> itemType = arrays.getClass().getComponentType().getComponentType();
      for (final T[] arr : arrays) {
         if (arr == null || arr.length == 0)
            return (T[]) Array.newInstance(itemType, 0);
      }

      final List<T> commonItems = new ArrayList<>();

      for (final T candidate : arrays[0]) {
         boolean isCommon = true;
         for (int i = 1; i < arrays.length; i++) {

            if (!contains(arrays[i], candidate)) {
               isCommon = false;
               break;
            }
         }
         if (isCommon) {
            commonItems.add(candidate);
         }
      }

      final T[] result = commonItems.toArray((T[]) Array.newInstance(itemType, commonItems.size()));
      return result;
   }

   @SuppressWarnings("unchecked")
   public static <T> T[] toArray(final Collection<T> values, final Class<T> itemType) {
      if (values == null)
         return null;

      return values.toArray((T[]) Array.newInstance(itemType, values.size()));
   }

   /**
    * About 30% faster than <code>new String(chars).getBytes("UTF-8")</code>
    */
   public static byte[] toByteArray(final char[] chars, final Charset charset) {
      if (chars == null)
         return null;

      if (chars.length == 0)
         return EMPTY_BYTE_ARRAY;

      final CharBuffer charBuff = CharBuffer.wrap(chars);
      final ByteBuffer bytesBuff = charset.encode(charBuff);
      final byte[] bytes = new byte[bytesBuff.remaining()];
      bytesBuff.get(bytes);
      return bytes;
   }

   public static byte[] toByteArray(final char[] chars, final int off, final int len, final Charset charset) {
      if (chars == null)
         return null;

      final CharBuffer charBuff = CharBuffer.wrap(chars, off, len);
      final ByteBuffer bytesBuff = charset.encode(charBuff);
      final byte[] bytes = new byte[bytesBuff.remaining()];
      bytesBuff.get(bytes);
      return bytes;
   }

   public static List<Boolean> toList(final boolean... array) {
      if (array == null)
         return null;

      final ArrayList<Boolean> result = new ArrayList<>(array.length);
      for (final boolean i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Byte> toList(final byte... array) {
      if (array == null)
         return null;

      final ArrayList<Byte> result = new ArrayList<>(array.length);
      for (final byte i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Character> toList(final char... array) {
      if (array == null)
         return null;

      final ArrayList<Character> result = new ArrayList<>(array.length);
      for (final char i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Double> toList(final double... array) {
      if (array == null)
         return null;

      final ArrayList<Double> result = new ArrayList<>(array.length);
      for (final double i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Float> toList(final float... array) {
      if (array == null)
         return null;

      final ArrayList<Float> result = new ArrayList<>(array.length);
      for (final float i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Integer> toList(final int... array) {
      if (array == null)
         return null;

      final ArrayList<Integer> result = new ArrayList<>(array.length);
      for (final int i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Long> toList(final long... array) {
      if (array == null)
         return null;

      final ArrayList<Long> result = new ArrayList<>(array.length);
      for (final long i : array) {
         result.add(i);
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   public static <T> List<T> toList(final Object array, @SuppressWarnings("unused") final Class<T> itemType) {
      if (array == null)
         return null;
      if (!array.getClass().isArray())
         throw new IllegalArgumentException("[array] is not an array but of type: " + array.getClass());

      final int l = Array.getLength(array);
      final List<Object> result = CollectionUtils.newArrayList(l);
      for (int i = 0; i < l; i++) {
         result.add(Array.get(array, i));
      }
      return (List<T>) result;
   }

   public static List<Short> toList(final short... array) {
      if (array == null)
         return null;

      final ArrayList<Short> result = new ArrayList<>(array.length);
      for (final short i : array) {
         result.add(i);
      }
      return result;
   }

   @SafeVarargs
   public static <T> List<T> toList(final T... array) {
      return CollectionUtils.newArrayList(array);
   }

   public static <S, T> T[] transform(final S[] source, final Class<T> targetType, final Function<? super S, ? extends T> op) {
      if (source == null)
         return null;

      @SuppressWarnings("unchecked")
      final T[] target = (T[]) Array.newInstance(targetType, source.length);
      for (int i = 0, l = source.length; i < l; i++) {
         target[i] = op.apply(source[i]);
      }
      return target;
   }
}
