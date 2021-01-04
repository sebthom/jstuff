/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection.tuple;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.util.Collections;

import net.sf.jstuff.core.collection.DelegatingList;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Tuple extends DelegatingList<Object> {
   private static final long serialVersionUID = 1L;

   protected Tuple(final Object... items) {
      super(Collections.unmodifiableList(newArrayList(items)));
   }

   /**
    * @param index 0 = first element
    */
   @SuppressWarnings("unchecked")
   public <T> T getTyped(final int index) {
      return (T) get(index);
   }
}
