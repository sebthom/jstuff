/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.AbstractList;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
