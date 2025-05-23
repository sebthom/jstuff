/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class BooleanArrayList extends AbstractList<Boolean> implements BooleanList, Cloneable, RandomAccess, java.io.Serializable {

   private static final long serialVersionUID = 1L;

   private boolean[] values;
   private int size;
   private final boolean mutable;

   public BooleanArrayList() {
      this(new boolean[10], false, true);
   }

   public BooleanArrayList(final boolean[] initialValues, final boolean copyArray) {
      this(initialValues, copyArray, true);
   }

   public BooleanArrayList(final boolean[] initialValues, final boolean copyArray, final boolean mutable) {
      size = initialValues.length;
      if (copyArray) {
         values = Arrays.copyOf(initialValues, size);
      } else {
         values = initialValues;
      }
      this.mutable = mutable;
   }

   @Override
   public void add(final int index, final boolean value) {
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
   public void add(final int index, final Boolean value) {
      add(index, (boolean) value);
   }

   @Override
   public boolean add(final boolean value) {
      add(size, value);
      return true;
   }

   @Override
   public boolean add(final Boolean value) {
      add(size, value);
      return true;
   }

   @Override
   public boolean addAll(final boolean... values) {
      for (final boolean v : values) {
         add(v);
      }
      return true;
   }

   @Override
   public void clear() {
      size = 0;
   }

   @Override
   public BooleanArrayList clone() {
      try {
         final BooleanArrayList clone = (BooleanArrayList) super.clone();
         clone.values = Arrays.copyOf(values, size);
         return clone;
      } catch (final CloneNotSupportedException e) {
         // this shouldn't happen, since we are Cloneable
         throw new InternalError(e);
      }
   }

   @Override
   public boolean contains(final boolean value) {
      return indexOf(value) != -1;
   }

   @Override
   public boolean containsAll(final boolean... values) {
      for (final boolean v : values) {
         if (!contains(v))
            return false;
      }
      return true;
   }

   /**
    * @deprecated Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   public Boolean get(final int index) {
      return getAt(index);
   }

   @Override
   public boolean getAt(final int index) {
      if (size < 1 || index < 0 || index >= size)
         throw new IndexOutOfBoundsException();

      return values[index];
   }

   @Override
   public Boolean getLast() {
      if (size < 1)
         throw new NoSuchElementException();

      return values[size - 1];
   }

   @Override
   public int indexOf(final boolean value) {
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
   public Boolean remove(final int index) {
      return removeAt(index);
   }

   /**
    * @deprecated Use {@link #removeValue(boolean)}
    */
   @Deprecated
   @Override
   public boolean remove(final @Nullable Object o) {
      if (o instanceof final Boolean bool)
         return removeValue(bool);
      return false;
   }

   @Override
   public boolean removeAt(final int index) {
      Assert.isTrue(mutable, "List is immutable!");

      if (size < 1 || index < 0 || index >= size)
         throw new IndexOutOfBoundsException();

      final boolean old = values[index];
      System.arraycopy(values, index, values, index, size - index - 1);
      size--;
      return old;
   }

   @Override
   public Boolean removeLast() {
      Assert.isTrue(mutable, "List is immutable!");

      if (size < 1)
         throw new NoSuchElementException();

      final boolean old = values[size - 1];
      size--;
      return old;
   }

   @Override
   public boolean removeValue(final boolean value) {
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
   public boolean set(final int index, final boolean value) {
      Assert.isTrue(mutable, "List is immutable!");

      final boolean old = values[index];
      values[index] = value;
      return old;
   }

   /**
    * @deprecated Use {@link #set(int, boolean)}
    */
   @Deprecated
   @Override
   public Boolean set(final int index, final Boolean value) {
      return set(index, (boolean) value);
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
   public Boolean[] toArray() {
      final var result = new Boolean[size];
      for (int i = 0; i < result.length; i++) {
         result[i] = values[i];
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> T[] toArray(final T[] array) {
      if (array.getClass().getComponentType() == int.class) {
         if (array.length == size)
            return array;
         final var result = array.length >= size ? array : (T @NonNull []) java.lang.reflect.Array.newInstance(boolean.class, size);
         System.arraycopy(values, size, result, 0, size);
         return result;
      }
      return super.toArray(array);
   }

   @Override
   public boolean[] toValueArray() {
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
