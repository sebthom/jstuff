/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNullUnsafe;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

   @SuppressWarnings("deprecation")
   protected ArrayUtils() {
   }

   @SuppressWarnings("unchecked")
   public static <T> T[] addAll(final T[] arr, final @Nullable Collection<T> coll) {
      if (coll == null)
         return arr;
      return addAll(arr, coll.toArray((T[]) Array.newInstance(asNonNullUnsafe(arr.getClass().getComponentType()), coll.size())));
   }

   @SuppressWarnings("unchecked")
   public static <T> T[] asArray(final T... values) {
      return values;
   }

   @SuppressWarnings("unchecked")
   public static <T> T @Nullable [] asArrayNullable(final T @Nullable... values) {
      return values;
   }

   public static <T> boolean containsNulls(final T @Nullable [] array) {
      if (array == null)
         return false;

      for (final T t : array) {
         if (t == null)
            return true;
      }
      return false;
   }

   public static <T> boolean containsIdentical(final T @Nullable [] searchIn, final T searchFor) {
      if (searchIn == null)
         return false;

      for (final T item : searchIn)
         if (item == searchFor)
            return true;
      return false;
   }

   public static boolean containsIgnoringCase(final String @Nullable [] searchIn, final String searchFor) {
      if (searchIn == null)
         return false;

      for (final String item : searchIn) {
         if (searchFor.equalsIgnoreCase(item))
            return true;
      }
      return false;
   }

   public static <T> Class<T> getComponentType(final T[] array) {
      return asNonNullUnsafe(org.apache.commons.lang3.ArrayUtils.getComponentType(array));
   }

   /**
    * @return a new array with all items accepted by the filter
    */
   @SuppressWarnings({"unchecked"})
   public static <T> T[] filter(final Predicate<? super T> filter, final T... array) {
      return asNonNullUnsafe(filterNullable(filter, array));
   }

   /**
    * @return a new array with all items accepted by the filter or <code>null</code> if <code>array == null</code>
    */
   @SuppressWarnings({"unchecked"})
   public static <T> T @Nullable [] filterNullable(final Predicate<? super T> filter, final T @Nullable... array) {
      if (array == null || array.length == 0)
         return array;

      final var result = new ArrayList<T>();
      for (final T item : array)
         if (filter.test(item)) {
            result.add(item);
         }

      return result.toArray((T[]) Array.newInstance(getComponentType(array), result.size()));
   }

   /**
    * @return all items that are contained in all arrays.
    */
   @SuppressWarnings("unchecked")
   public static <T> T[] intersect(final T[]... arrays) {
      return asNonNullUnsafe(intersectNullable(arrays));
   }

   /**
    * @return all items that are contained in all arrays.
    */
   @SuppressWarnings("unchecked")
   public static <T> T @Nullable [] intersectNullable(final T @Nullable []... arrays) {
      if (arrays == null)
         return null;
      final Class<?> itemType = asNonNullUnsafe(getComponentType(arrays).getComponentType());
      for (final T[] arr : arrays) {
         if (arr == null || arr.length == 0)
            return (T[]) Array.newInstance(itemType, 0);
      }

      final var commonItems = new ArrayList<T>();

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

      return commonItems.toArray((T[]) Array.newInstance(itemType, commonItems.size()));
   }

   @SuppressWarnings("unchecked")
   public static <T> T[] toArray(final Collection<T> values, final Class<T> itemType) {
      return values.toArray((T[]) Array.newInstance(itemType, values.size()));
   }

   /**
    * About 30% faster than <code>new String(chars).getBytes("UTF-8")</code>
    */
   public static byte[] toByteArray(final char[] chars, final Charset charset) {
      if (chars.length == 0)
         return EMPTY_BYTE_ARRAY;

      final CharBuffer charBuff = CharBuffer.wrap(chars);
      final ByteBuffer bytesBuff = charset.encode(charBuff);
      final var bytes = new byte[bytesBuff.remaining()];
      bytesBuff.get(bytes);
      return bytes;
   }

   public static byte[] toByteArray(final char[] chars, final int off, final int len, final Charset charset) {
      final CharBuffer charBuff = CharBuffer.wrap(chars, off, len);
      final ByteBuffer bytesBuff = charset.encode(charBuff);
      final var bytes = new byte[bytesBuff.remaining()];
      bytesBuff.get(bytes);
      return bytes;
   }

   public static List<Boolean> toList(final boolean... array) {
      final var result = new ArrayList<Boolean>(array.length);
      for (final boolean i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Byte> toList(final byte... array) {
      final var result = new ArrayList<Byte>(array.length);
      for (final byte i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Character> toList(final char... array) {
      final var result = new ArrayList<Character>(array.length);
      for (final char i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Double> toList(final double... array) {
      final var result = new ArrayList<Double>(array.length);
      for (final double i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Float> toList(final float... array) {
      final var result = new ArrayList<Float>(array.length);
      for (final float i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Integer> toList(final int... array) {
      final var result = new ArrayList<Integer>(array.length);
      for (final int i : array) {
         result.add(i);
      }
      return result;
   }

   public static List<Long> toList(final long... array) {
      final var result = new ArrayList<Long>(array.length);
      for (final long i : array) {
         result.add(i);
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   public static <T> List<T> toList(final Object array, @SuppressWarnings("unused") final Class<T> itemType) {
      if (!array.getClass().isArray())
         throw new IllegalArgumentException("[array] is not an array but of type: " + array.getClass());

      final int l = Array.getLength(array);
      final var result = CollectionUtils.newArrayList(l);
      for (int i = 0; i < l; i++) {
         result.add(Array.get(array, i));
      }
      return (List<T>) result;
   }

   public static List<Short> toList(final short... array) {
      final var result = new ArrayList<Short>(array.length);
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
      return asNonNullUnsafe(transformNullable(source, targetType, op));
   }

   public static <S, T> T @Nullable [] transformNullable(final S @Nullable [] source, final Class<T> targetType,
         final Function<? super S, ? extends T> op) {
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
