/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.sf.jstuff.core.logging.Logger;

/**
 * Observable mutable reference holder.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObservableRef<E> extends MutableRef<E> {

   private static final Logger LOG = Logger.create();

   public static <T> ObservableRef<T> of(final T value) {
      return new ObservableRef<>(value);
   }

   private E value;
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private List<Object> observers;

   public ObservableRef() {
   }

   public ObservableRef(final E initialValue) {
      this.value = initialValue;
   }

   protected void clearObservers() {
      lock.writeLock().lock();
      try {
         if (observers != null) {
            observers.clear();
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   @Override
   public E get() {
      lock.readLock().lock();
      try {
         return value;
      } finally {
         lock.readLock().unlock();
      }
   }

   public boolean isObserved() {
      lock.readLock().lock();
      try {
         if (observers == null)
            return false;
         return !observers.isEmpty();
      } finally {
         lock.readLock().unlock();
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public void set(final E value) {
      final E oldValue;
      lock.writeLock().lock();
      try {
         oldValue = this.value;
         this.value = value;

         // downgrade writelock to readlock
         lock.readLock().lock();
         lock.writeLock().unlock();

         if (observers != null) {
            for (final Object observer : observers) {
               try {
                  if (observer instanceof Runnable) {
                     ((Runnable) observer).run();
                  } else if (observer instanceof Consumer) {
                     ((Consumer<E>) observer).accept(value);
                  } else if (observer instanceof BiConsumer) {
                     ((BiConsumer<E, E>) observer).accept(oldValue, value);
                  }
               } catch (final Exception ex) {
                  LOG.error(ex);
               }
            }
         }
      } finally {
         lock.readLock().unlock();
      }
   }

   public void subscribe(final BiConsumer<E, E> observer) {
      subscribe((Object) observer);
   }

   public void subscribe(final Consumer<E> observer) {
      subscribe((Object) observer);
   }

   private void subscribe(final Object observer) {
      if (observer == null)
         return;
      lock.writeLock().lock();
      try {
         if (observers == null) {
            observers = new ArrayList<>();
            observers.add(observer);
         } else if (!observers.contains(observer)) {
            observers.add(observer);
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   public void subscribe(final Runnable observer) {
      subscribe((Object) observer);
   }

   public void unsubscribe(final BiConsumer<E, E> observer) {
      unsubscribe((Object) observer);
   }

   public void unsubscribe(final Consumer<E> observer) {
      unsubscribe((Object) observer);
   }

   private void unsubscribe(final Object observer) {
      if (observer == null)
         return;
      lock.writeLock().lock();
      try {
         if (observers != null) {
            observers.remove(observer);
         }
      } finally {
         lock.writeLock().unlock();
      }
   }

   public void unsubscribe(final Runnable observer) {
      unsubscribe((Object) observer);
   }
}
