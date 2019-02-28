/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection.iterator;

import java.util.Collection;
import java.util.Iterator;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeIterator<V> extends Composite.Default<Iterator<? extends V>> implements Iterator<V> {

   private static final long serialVersionUID = 1L;

   private Iterator<? extends V> lastItemIterator = Iterators.empty();
   private Iterator<? extends V> nextItemIterator = Iterators.empty();

   public CompositeIterator() {
      super();
   }

   public CompositeIterator(final Collection<? extends Iterator<V>> components) {
      super(components);
      if (this.components.size() > 0) {
         nextItemIterator = CollectionUtils.remove(this.components, 0);
      }
   }

   public CompositeIterator(final Iterator<V>... components) {
      super(components);
      if (this.components.size() > 0) {
         nextItemIterator = CollectionUtils.remove(this.components, 0);
      }
   }

   public boolean hasNext() {
      prepareNextItemIterator();
      return nextItemIterator.hasNext();
   }

   public V next() {
      prepareNextItemIterator();
      final V item = nextItemIterator.next();
      lastItemIterator = nextItemIterator;
      return item;
   }

   protected void prepareNextItemIterator() {
      if (nextItemIterator.hasNext())
         return;
      if (components.size() > 0) {
         nextItemIterator = CollectionUtils.remove(components, 0);
         prepareNextItemIterator();
      }
   }

   public void remove() {
      lastItemIterator.remove();
   }
}
