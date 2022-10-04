/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.ext;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ArrayListExt<E> extends ArrayList<E> implements ListExt<E> {

   private static final long serialVersionUID = 1L;

   static final ArrayListExt<?> EMPTY_LIST = new ArrayListExt<>(0) {

      private static final long serialVersionUID = 1L;

      @Override
      public void add(final int index, final @Nullable Object element) {
         throw new UnsupportedOperationException();
      }
   };

   public ArrayListExt() {
   }

   public ArrayListExt(final Collection<? extends E> c) {
      super(c);
   }

   public ArrayListExt(final int initialCapacity) {
      super(initialCapacity);
   }
}
