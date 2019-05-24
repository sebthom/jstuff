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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Enumerations {

   public static boolean contains(final Enumeration<?> en, final Object searchFor) {
      if (en == null)
         return false;

      while (en.hasMoreElements()) {
         final Object elem = en.nextElement();
         if (Objects.equals(elem, searchFor))
            return true;
      }
      return false;
   }

   public static boolean containsIdentical(final Enumeration<?> en, final Object searchFor) {
      if (en == null)
         return false;

      while (en.hasMoreElements())
         if (searchFor == en.nextElement())
            return true;
      return false;
   }

   public static int size(final Enumeration<?> en) {
      Args.notNull("en", en);

      int size = 0;
      while (en.hasMoreElements()) {
         size++;
         en.nextElement();
      }
      return size;
   }

   public static <T> Iterable<T> toIterable(final Enumeration<T> en) {
      return new Iterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return new Iterator<T>() {
               @Override
               public boolean hasNext() {
                  return en == null ? false : en.hasMoreElements();
               }

               @Override
               public T next() {
                  return en.nextElement();
               }

               @Override
               public void remove() {
                  throw new UnsupportedOperationException();
               }
            };
         }
      };
   }

   public static <T> List<T> toList(final Enumeration<T> en) {
      if (en == null)
         return null;

      final List<T> result = new ArrayList<>();
      while (en.hasMoreElements()) {
         result.add(en.nextElement());
      }
      return result;
   }

   public static <T> Set<T> toSet(final Enumeration<T> en) {
      if (en == null)
         return null;

      final Set<T> result = new HashSet<>();
      while (en.hasMoreElements()) {
         result.add(en.nextElement());
      }
      return result;
   }

}
