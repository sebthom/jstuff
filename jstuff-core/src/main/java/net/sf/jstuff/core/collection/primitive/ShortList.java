/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection.primitive;

import java.util.List;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ShortList extends ShortCollection, List<Short> {

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt; size()</tt>)
    */
   void add(int index, short value);

   /**
    * Use {@link #getAt(int)}
    */
   @Deprecated
   @Override
   Short get(int index);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   short getAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   short getLast();

   /**
    * @return the index of the first occurrence of the specified value, or -1 if this list does not contain the value
    */
   int indexOf(short value);

   /**
    * Use {@link #removeAt(int)}
    */
   @Deprecated
   @Override
   Short remove(int index);

   /**
    * Use {@link #removeValue(short)}
    */
   @Deprecated
   @Override
   boolean remove(Object o);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   short removeAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   short removeLast();

   boolean removeValue(short value);

   /**
    * Use {@link #set(int, short)}
    */
   @Deprecated
   @Override
   Short set(int index, Short value);

   short set(int index, short value);
}
