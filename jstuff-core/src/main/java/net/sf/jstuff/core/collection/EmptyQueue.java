/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class EmptyQueue<E> extends AbstractQueue<E> implements Serializable {

   private static final long serialVersionUID = 1L;

   private static final Queue<?> INSTANCE = new EmptyQueue<>();

   @SuppressWarnings("unchecked")
   public static <T> EmptyQueue<T> get() {
      return (EmptyQueue<T>) INSTANCE;
   }

   private EmptyQueue() {
   }

   @Override
   public void clear() {
      // nothing to do
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public Iterator<E> iterator() {
      return Collections.<E> emptySet().iterator(); // CHECKSTYLE:IGNORE GenericWhitespace
   }

   @Override
   public boolean offer(final E o) {
      return false;
   }

   @Override
   public E peek() {
      return null;
   }

   @Override
   public E poll() {
      return null;
   }

   @SuppressWarnings("unused")
   private Object readResolve() throws ObjectStreamException {
      return INSTANCE;
   }

   @Override
   public int size() {
      return 0;
   }
}
