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

import java.util.AbstractList;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EmptyList<E> extends AbstractList<E> {

   @Override
   public void add(final int index, final E element) {
      // do nothing
   }

   @Override
   public E get(final int index) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public E set(final int index, final E element) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public E remove(final int index) {
      throw new IndexOutOfBoundsException();
   }

   @Override
   public int size() {
      return 0;
   }
}
