/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import net.sf.jstuff.core.types.Decorator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DelegatingCollection<V> extends Decorator.Default<Collection<V>> implements Collection<V>, Serializable {

   private static final long serialVersionUID = 1L;

   public DelegatingCollection(final Collection<V> delegate) {
      super(delegate);
   }

   @Override
   public boolean add(final V o) {
      return wrapped.add(o);
   }

   @Override
   public boolean addAll(final Collection<? extends V> c) {
      return wrapped.addAll(c);
   }

   @Override
   public void clear() {
      wrapped.clear();
   }

   @Override
   public boolean contains(final Object o) {
      return wrapped.contains(o);
   }

   @Override
   public boolean containsAll(final Collection<?> c) {
      return wrapped.containsAll(c);
   }

   @Override
   public boolean equals(final Object o) {
      return wrapped.equals(o);
   }

   @Override
   public int hashCode() {
      return wrapped.hashCode();
   }

   @Override
   public boolean isEmpty() {
      return wrapped.isEmpty();
   }

   @Override
   public Iterator<V> iterator() {
      return wrapped.iterator();
   }

   @Override
   public boolean remove(final Object o) {
      return wrapped.remove(o);
   }

   @Override
   public boolean removeAll(final Collection<?> c) {
      return wrapped.removeAll(c);
   }

   @Override
   public boolean retainAll(final Collection<?> c) {
      return wrapped.retainAll(c);
   }

   @Override
   public int size() {
      return wrapped.size();
   }

   @Override
   public Object[] toArray() {
      return wrapped.toArray();
   }

   @Override
   public <T> T[] toArray(final T[] a) {
      return wrapped.toArray(a);
   }
}
