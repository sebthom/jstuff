/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @deprecated use {@link ConcurrentHashMap#newKeySet()} or {@link Sets#newConcurrentHashSet()}
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Deprecated
public class ConcurrentHashSet<E> extends MapBackedSet<E> {

   private static final long serialVersionUID = 1L;

   public ConcurrentHashSet() {
      super(new ConcurrentHashMap<>());
   }

   public ConcurrentHashSet(final int initialCapacity) {
      super(new ConcurrentHashMap<>(initialCapacity));
   }
}
