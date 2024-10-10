/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.functional.BiIntConsumer;
import net.sf.jstuff.core.math.Numbers;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteArrayList extends AbstractList<Byte> implements ByteList, Cloneable, RandomAccess, java.io.Serializable {

   private static final long serialVersionUID = 1L;

   private byte[] values;
   private int size;
   private final boolean mutable;

   public ByteArrayList() {
      this(new byte[10], false, true);
   }

   public ByteArrayList(final byte[] initialValues, final boolean copyArray) {
      this(initialValues, copyArray, true);
   }

   public ByteArrayList(final byte[] initialValues, final boolean copyArray, final boolean mutable) {
      size = initialValues.length;
      if (copyArray) {
         values = Arrays.copyOf(initialValues, size);
      } else {
         values = initialValues;
      }
      this.mutable = mutable;
   }

   @Override
   public void add(final int index, final byte value) {
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
   public void add(final int index, final Byte value) {
      Args.notNull("value", value);
      add(index, (byte) value);
   }

   @Override
   public boolean add(final byte value) {
      add(size, value);
      return true;
   }

   @Override
   public boolean add(final Byte value) {
      Args.notNull("value", value);
      add(size, value);
      return true;
   }

   @Override
   public boolean addAll(final byte... values) {
      Args.notNull("values", values);
      for (final byte v : values) {
         add(v);
      }
      return true;
   }

   @Override
   public void clear() {
      size = 0;
   }

   @Override
   public ByteArrayList clone() {
      try {
         final ByteArrayList clone = (ByteArrayList) super.clone();
         clone.values = Arrays.copyOf(values, size);
         return clone;
      } catch (final CloneNotSupportedException e) {
         // this shouldn't happen, since we are Cloneable
         throw new InternalError(e);
      }
   }

   @Override
   public boolean contains(final byte value) {
      return indexOf(value) != -1;
   }

   @Override
   public boolean containsAll(final byte... values) {
      Args.notNull("values", values);
      for (final byte v : values) {
         if (!contains(v))
            return false;
      }
      return true;
   }

   @Override
   public void forEach(final IntConsumer consumer) {
      for (int i = 0; i < size; i++) {
         consumer.accept(values[i]);
      }
   }

   public void forEach(final BiIntConsumer consumer) {
      for (int i = 0; i < size; i++) {
         consumer.accept(i, values[i]);
      }
   }

   /**
    * @deprecated Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   public Byte get(final int index) {
      return getAt(index);
   }

   @Override
   public byte getAt(final int index) {
      if (size < 1 || index < 0 || index >= size)
         throw new IndexOutOfBoundsException();

      return values[index];
   }

   @Override
   public Byte getLast() {
      if (size < 1)
         throw new NoSuchElementException();

      return values[size - 1];
   }

   @Override
   public int indexOf(final byte value) {
      for (int i = 0; i < size; i++) {
         if (values[i] == value)
            return i;
      }
      return -1;
   }

   /**
    * @deprecated Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   public Byte remove(final int index) {
      return removeAt(index);
   }

   /**
    * @deprecated Use {@link #removeValue(byte)}
    */
   @Deprecated
   @Override
   public boolean remove(final @Nullable Object o) {
      if (o instanceof final Number n)
         return Numbers.isByte(n) && removeValue(n.byteValue());
      return false;
   }

   @Override
   public byte removeAt(final int index) {
      Assert.isTrue(mutable, "List is immutable!");

      if (size < 1 || index < 0 || index >= size)
         throw new IndexOutOfBoundsException();

      final byte old = values[index];
      System.arraycopy(values, index, values, index, size - index - 1);
      size--;
      return old;
   }

   @Override
   public boolean removeIf(final IntPredicate filter) {
      Assert.isTrue(mutable, "List is immutable!");

      final var temp = new ByteArrayList();
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
    * @deprecated Use {@link #removeIf(IntPredicate)}
    */
   @Deprecated
   @Override
   public boolean removeIf(final Predicate<? super Byte> filter) {
      return super.removeIf(filter);
   }

   @Override
   public Byte removeLast() {
      Assert.isTrue(mutable, "List is immutable!");

      if (size < 1)
         throw new NoSuchElementException();

      final byte old = values[size - 1];
      size--;
      return old;
   }

   @Override
   public boolean removeValue(final byte value) {
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
   public byte set(final int index, final byte value) {
      Assert.isTrue(mutable, "List is immutable!");

      final byte old = values[index];
      values[index] = value;
      return old;
   }

   /**
    * @deprecated Use {@link #set(int, byte)}
    */
   @Deprecated
   @Override
   public Byte set(final int index, final Byte value) {
      Args.notNull("value", value);
      return set(index, (byte) value);
   }

   @Override
   public int size() {
      return size;
   }

   /**
    * @deprecated Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   public Byte[] toArray() {
      final var result = new Byte[size];
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
         final T[] result = array.length >= size ? array : (T[]) java.lang.reflect.Array.newInstance(byte.class, size);
         System.arraycopy(values, size, result, 0, size);
         return result;
      }
      return super.toArray(array);
   }

   @Override
   public byte[] toValueArray() {
      return Arrays.copyOf(values, size);
   }

   @Override
   public String toString() {
      final var sb = new StringBuilder("[");
      for (int i = 0; i < size; i++) {
         sb.append(values[i]);
         if (i < size - 1) {
            sb.append(',').append(' ');
         }
      }
      return sb.append(']').toString();
   }
}
