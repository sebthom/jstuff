/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface LongList extends LongCollection, List<Long> {

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
    */
   void add(int index, long value);

   /**
    * @deprecated Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   Long get(int index);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   long getAt(int index);

   /**
    * For compatibility with Java 21 must return non-primitive value.
    *
    * @throws NoSuchElementException if list is empty
    */
   Long getLast();

   /**
    * @return the index of the first occurrence of the specified value, or -1 if this list does not contain the value
    */
   int indexOf(long value);

   /**
    * @deprecated Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   Long remove(int index);

   /**
    * @deprecated Use {@link #removeValue(long)}
    */
   @Deprecated
   @Override
   boolean remove(@Nullable Object o);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   long removeAt(int index);

   /**
    * For compatibility with Java 21 must return non-primitive value.
    *
    * @throws NoSuchElementException if list is empty
    */
   Long removeLast();

   boolean removeValue(long value);

   /**
    * @deprecated Use {@link #set(int, long)}
    */
   @Deprecated
   @Override
   Long set(int index, Long value);

   long set(int index, long value);
}
