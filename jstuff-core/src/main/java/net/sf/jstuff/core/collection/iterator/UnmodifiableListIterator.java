/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.collection.iterator;

import java.util.ListIterator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnmodifiableListIterator<T> extends UnmodifiableIterator<T> implements ListIterator<T> {
   private static final long serialVersionUID = 1L;

   private final ListIterator<T> delegate;

   public UnmodifiableListIterator(final ListIterator<T> delegate) {
      super(delegate);
      this.delegate = delegate;
   }

   public void add(final T o) {
      throw new UnsupportedOperationException();
   }

   public boolean hasPrevious() {
      return delegate.hasPrevious();
   }

   public int nextIndex() {
      return delegate.nextIndex();
   }

   public T previous() {
      return delegate.previous();
   }

   public int previousIndex() {
      return delegate.previousIndex();
   }

   public void set(final T o) {
      throw new UnsupportedOperationException();
   }
}
