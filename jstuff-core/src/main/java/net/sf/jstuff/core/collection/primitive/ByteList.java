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
public interface ByteList extends ByteCollection, List<Byte> {

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
    */
   void add(int index, byte value);

   /**
    * Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   Byte get(int index);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   byte getAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   byte getLast();

   /**
    * @return the index of the first occurrence of the specified value, or -1 if this list does not contain the value
    */
   int indexOf(byte value);

   /**
    * Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   Byte remove(int index);

   /**
    * Use {@link #removeValue(byte)}
    */
   @Deprecated
   @Override
   boolean remove(@Nullable Object o);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   byte removeAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   byte removeLast();

   boolean removeValue(byte value);

   byte set(int index, byte value);

   /**
    * Use {@link #set(int, byte)}
    */
   @Deprecated
   @Override
   Byte set(int index, Byte value);
}
