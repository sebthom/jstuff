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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MapWithQueues<K, V> extends MapWithCollections<K, V, Queue<V>> {
   private static final long serialVersionUID = 1L;

   public static <K, V> MapWithQueues<K, V> create() {
      return new MapWithQueues<>();
   }

   @Override
   protected Queue<V> create(final K key) {
      return new ConcurrentLinkedQueue<>();
   }

   @Override
   protected Queue<V> createNullSafe(final K key) {
      return EmptyQueue.get();
   }
}
