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
    * @throws IndexOutOfBoundsException if list is empty
    */
   boolean getLast();

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
   boolean remove(Object o);

   /**
    * @throws IndexOutOfBoundsException if the index is out of range (<tt>index &lt; 0 || index &gt;= size()</tt>)
    */
   boolean removeAt(int index);

   /**
    * @throws IndexOutOfBoundsException if list is empty
    */
   boolean removeLast();

   boolean removeValue(boolean value);

   boolean set(int index, boolean value);

   /**
    * Use {@link #set(int, boolean)}
    */
   @Deprecated
   @Override
   Boolean set(int index, Boolean value);
}
