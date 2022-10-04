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
public interface IntList extends IntCollection, List<Integer> {

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
    */
   void add(int index, int value);

   /**
    * Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   Integer get(int index);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   int getAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   int getLast();

   /**
    * @return the index of the first occurrence of the specified value, or -1 if this list does not contain the value
    */
   int indexOf(int value);

   /**
    * Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   Integer remove(int index);

   /**
    * Use {@link #removeValue(int)}
    */
   @Deprecated
   @Override
   boolean remove(@Nullable Object o);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   int removeAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   int removeLast();

   boolean removeValue(int value);

   int set(int index, int value);

   /**
    * Use {@link #set(int, int)}
    */
   @Deprecated
   @Override
   Integer set(int index, Integer value);
}
