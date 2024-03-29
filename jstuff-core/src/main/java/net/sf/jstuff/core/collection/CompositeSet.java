/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeSet<V> extends CompositeCollection<V> implements Set<V> {

   private static final long serialVersionUID = 1L;

   public static <V> CompositeSet<V> of(final Collection<Set<? extends V>> sets) {
      return new CompositeSet<>(sets);
   }

   @SafeVarargs
   public static <V> CompositeSet<V> of(final @NonNull Set<? extends V>... sets) {
      return new CompositeSet<>(sets);
   }

   public CompositeSet() {
   }

   public CompositeSet(final Collection<Set<? extends V>> sets) {
      super(sets);
   }

   @SafeVarargs
   public CompositeSet(final @NonNull Set<? extends V>... sets) {
      super(sets);
   }

   @Override
   public boolean equals(final @Nullable Object other) {
      if (other == this)
         return true;
      if (!(other instanceof Set))
         return false;
      final Collection<?> c = (Collection<?>) other;
      if (c.size() != size())
         return false;
      try {
         return containsAll(c);
      } catch (final ClassCastException | NullPointerException ignore) {
         return false;
      }
   }

   private Set<V> getSnapshot() {
      final var values = new LinkedHashSet<V>();
      for (final Collection<? extends V> coll : components) {
         values.addAll(coll);
      }
      return values;
   }

   @Override
   public int hashCode() {
      int h = 0;
      for (final V obj : this)
         if (obj != null) {
            h += obj.hashCode();
         }
      return h;
   }

   @Override
   public Iterator<V> iterator() {
      // to avoid duplicate elements from the different backing sets we dump the values of all into a new set
      final Iterator<V> it = getSnapshot().iterator();
      return new Iterator<>() {
         @Override
         public boolean hasNext() {
            return it.hasNext();
         }

         @Override
         public V next() {
            return it.next();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   @Override
   public int size() {
      return getSnapshot().size();
   }
}
