/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.RandomAccess;
import java.util.function.LongConsumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.functional.BiLongConsumer;
import net.sf.jstuff.core.math.Numbers;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LongArrayList extends AbstractList<Long> implements LongList, Cloneable, RandomAccess, java.io.Serializable {

   private static final long serialVersionUID = 1L;

   private long[] values;
   private int size;
   private final boolean mutable;

   public LongArrayList() {
      this(new long[10], false, true);
   }

   public LongArrayList(final long[] initialValues, final boolean copyArray) {
      this(initialValues, copyArray, true);
   }

   public LongArrayList(final long[] initialValues, final boolean copyArray, final boolean mutable) {
      size = initialValues.length;
      if (copyArray) {
         values = Arrays.copyOf(initialValues, size);
      } else {
         values = initialValues;
      }
      this.mutable = mutable;
   }

   @Override
   public void add(final int index, final long value) {
      Assert.isTrue(mutable, "List is immutable!");

      if (index < 0 || index > size)
         throw new IndexOutOfBoundsException();

      if (size == values.length) {
         resize();
      }
      if (index != size) {
         System.arraycopy(values, index, values, index + 1, size - index);
      }
      values[index] = value;
      size++;
   }

   @Override
   public void add(final int index, final Long value) {
      Args.notNull("value", value);
      add(index, (long) value);
   }

   @Override
   public boolean add(final long value) {
      add(size, value);
      return true;
   }

   @Override
   public boolean add(final Long value) {
      Args.notNull("value", value);
      add(size, value);
      return true;
   }

   @Override
   public boolean addAll(final long... values) {
      Args.notNull("values", values);
      for (final long v : values) {
         add(v);
      }
      return true;
   }

   @Override
   public void clear() {
      size = 0;
   }

   @Override
   public LongArrayList clone() {
      try {
         final LongArrayList clone = (LongArrayList) super.clone();
         clone.values = Arrays.copyOf(values, size);
         return clone;
      } catch (final CloneNotSupportedException e) {
         // this shouldn't happen, since we are Cloneable
         throw new InternalError(e);
      }
   }

   @Override
   public boolean contains(final long value) {
      return indexOf(value) != -1;
   }

   @Override
   public boolean containsAll(final long... values) {
      Args.notNull("values", values);
      for (final long v : values) {
         if (!contains(v))
            return false;
      }
      return true;
   }

   @Override
   public void forEach(final LongConsumer consumer) {
      for (int i = 0; i < size; i++) {
         consumer.accept(values[i]);
      }
   }

   public void forEach(final BiLongConsumer consumer) {
      for (int i = 0; i < size; i++) {
         consumer.accept(i, values[i]);
      }
   }

   /**
    * Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   public Long get(final int index) {
      return getAt(index);
   }

   @Override
   public long getAt(final int index) {
      if (size < 1 || index < 0 || index >= size)
         throw new IndexOutOfBoundsException();

      return values[index];
   }

   @Override
   public long getLast() {
      if (size < 1)
         throw new IndexOutOfBoundsException();

      return values[size - 1];
   }

   @Override
   public int indexOf(final long value) {
      for (int i = 0; i < size; i++) {
         if (values[i] == value)
            return i;
      }
      return -1;
   }

   /**
    * Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   public Long remove(final int index) {
      return removeAt(index);
   }

   /**
    * Use {@link #removeValue(long)}
    */
   @Deprecated
   @Override
   public boolean remove(final @Nullable Object o) {
      if (o instanceof Number) {
         final Number n = (Number) o;
         return Numbers.isLong(n) && removeValue(n.longValue());
      }
      return false;
   }

   @Override
   public long removeAt(final int index) {
      Assert.isTrue(mutable, "List is immutable!");

      if (size < 1 || index < 0 || index >= size)
         throw new IndexOutOfBoundsException();

      final long old = values[index];
      System.arraycopy(values, index, values, index, size - index - 1);
      size--;
      return old;
   }

   @Override
   public boolean removeIf(final LongPredicate filter) {
      Assert.isTrue(mutable, "List is immutable!");

      final LongArrayList temp = new LongArrayList();
      for (int i = 0; i < size; i++) {
         if (filter.test(values[i])) {
            temp.add(values[i]);
         }
      }
      if (temp.size == size)
         return false;
      values = temp.values;
      size = temp.size;
      return true;
   }

   /**
    * @deprecated use {@link #removeIf(LongPredicate)}
    */
   @Deprecated
   @Override
   public boolean removeIf(final Predicate<? super Long> filter) {
      return super.removeIf(filter);
   }

   @Override
   public long removeLast() {
      Assert.isTrue(mutable, "List is immutable!");

      if (size < 1)
         throw new IndexOutOfBoundsException();

      final long old = values[size - 1];
      size--;
      return old;
   }

   @Override
   public boolean removeValue(final long value) {
      final int index = indexOf(value);
      if (index == -1)
         return false;
      removeAt(index);
      return true;
   }

   private void resize() {
      values = Arrays.copyOf(values, (int) (size * 1.6F));
   }

   @Override
   public long set(final int index, final long value) {
      Assert.isTrue(mutable, "List is immutable!");

      final long old = values[index];
      values[index] = value;
      return old;
   }

   /**
    * Use {@link #set(int, long)}
    */
   @Deprecated
   @Override
   public Long set(final int index, final Long value) {
      Args.notNull("value", value);
      return set(index, (long) value);
   }

   @Override
   public int size() {
      return size;
   }

   /**
    * Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   public Long[] toArray() {
      final Long[] result = new Long[size];
      for (int i = 0; i < result.length; i++) {
         result[i] = values[i];
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T[] toArray(final T[] array) {
      Args.notNull("array", array);
      if (array.getClass().getComponentType() == int.class) {
         if (array.length == size)
            return array;
         final T[] result = array.length >= size ? array : (T[]) java.lang.reflect.Array.newInstance(long.class, size);
         System.arraycopy(values, size, result, 0, size);
         return result;
      }
      return super.toArray(array);
   }

   @Override
   public long[] toValueArray() {
      return Arrays.copyOf(values, size);
   }

   @Override
   public String toString() {
      final StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < size; i++) {
         sb.append(values[i]);
         if (i < size - 1) {
            sb.append(',').append(' ');
         }
      }
      return sb.append(']').toString();
   }
}
