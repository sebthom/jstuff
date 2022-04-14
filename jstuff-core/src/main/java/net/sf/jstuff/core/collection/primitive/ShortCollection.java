/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.primitive;

import java.util.Collection;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ShortCollection extends Collection<Short> {

   boolean addAll(short... values);

   boolean add(short value);

   boolean contains(short value);

   boolean containsAll(short... values);

   /**
    * Use {@link #toValueArray()}
    */
   @Deprecated
   @Override
   Short[] toArray();

   short[] toValueArray();
}
