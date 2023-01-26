/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import net.sf.jstuff.core.collection.ext.CollectionExt;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface BooleanCollection extends CollectionExt<Boolean> {

   boolean addAll(boolean... values);

   boolean add(boolean value);

   boolean contains(boolean value);

   boolean containsAll(boolean... values);

   /**
    * Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   Boolean[] toArray();

   boolean[] toValueArray();
}
