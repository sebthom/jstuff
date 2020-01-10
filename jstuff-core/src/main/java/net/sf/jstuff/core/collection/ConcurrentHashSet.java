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

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ConcurrentHashSet<E> extends MapBackedSet<E> {

   private static final long serialVersionUID = 1L;

   public ConcurrentHashSet() {
      super(new ConcurrentHashMap<E, Boolean>());
   }

   public ConcurrentHashSet(final int initialCapacity) {
      super(new ConcurrentHashMap<E, Boolean>(initialCapacity));
   }
}
