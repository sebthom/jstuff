/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection.tuple;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.util.Collections;

import net.sf.jstuff.core.collection.DelegatingList;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
