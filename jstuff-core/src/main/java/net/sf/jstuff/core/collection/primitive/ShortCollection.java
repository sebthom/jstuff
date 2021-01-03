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
