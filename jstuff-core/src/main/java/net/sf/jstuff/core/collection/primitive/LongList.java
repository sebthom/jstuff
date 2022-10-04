/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.List;

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
    * Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   Long get(int index);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   long getAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   long getLast();

   /**
    * @return the index of the first occurrence of the specified value, or -1 if this list does not contain the value
    */
   int indexOf(long value);

   /**
    * Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   Long remove(int index);

   /**
    * Use {@link #removeValue(long)}
    */
   @Deprecated
   @Override
   boolean remove(@Nullable Object o);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   long removeAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   long removeLast();

   boolean removeValue(long value);

   /**
    * Use {@link #set(int, long)}
    */
   @Deprecated
   @Override
   Long set(int index, Long value);

   long set(int index, long value);
}
