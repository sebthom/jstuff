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
public interface BooleanList extends BooleanCollection, List<Boolean> {

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
    */
   void add(int index, boolean value);

   /**
    * Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   Boolean get(int index);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   boolean getAt(int index);

   /**
    * For compatibility with Java 21 must return non-primitive value.
    *
    * @throws NoSuchElementException if list is empty
    */
   Boolean getLast();

   /**
    * @return the index of the first occurrence of the specified value, or -1 if this list does not contain the value
    */
   int indexOf(boolean value);

   /**
    * Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   Boolean remove(int index);

   /**
    * Use {@link #removeValue(boolean)}
    */
   @Deprecated
   @Override
   boolean remove(@Nullable Object o);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   boolean removeAt(int index);

   /**
    * For compatibility with Java 21 must return non-primitive value.
    *
    * @throws NoSuchElementException if list is empty
    */
   Boolean removeLast();

   boolean removeValue(boolean value);

   boolean set(int index, boolean value);

   /**
    * Use {@link #set(int, boolean)}
    */
   @Deprecated
   @Override
   Boolean set(int index, Boolean value);
}
