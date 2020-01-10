/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.util.AbstractSet;
import java.util.Iterator;

import net.sf.jstuff.core.collection.iterator.Iterators;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EmptySet<E> extends AbstractSet<E> {

   @Override
   public boolean add(final E e) {
      return true;
   }

   @Override
   public Iterator<E> iterator() {
      return Iterators.empty();
   }

   @Override
   public int size() {
      return 0;
   }
}
