/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;
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

   private final CopyOnWriteArraySet<Object> observers = new CopyOnWriteArraySet<>();
   private volatile E value;

   public ObservableRef() {
   }

   public ObservableRef(final E initialValue) {
      this.value = initialValue;
   }

   protected void clearObservers() {
      observers.clear();
   }

   @Override
   public E get() {
      return value;
   }

   protected boolean isModification(final E oldValue, final E newValue) {
      if (newValue == oldValue)
         return false;

      if (isScalarValue(newValue) //
         && isScalarValue(oldValue) //
         && Objects.equals(newValue, oldValue))
         return false;

      return true;
   }

   protected boolean isScalarValue(final E value) {
      return value == null //
         || value instanceof String //
         || value instanceof BigInteger //
         || value instanceof Long //
         || value instanceof Integer //
         || value instanceof Short //
         || value instanceof Byte //
         || value instanceof Character;
   }

   public boolean isObserved() {
      return !observers.isEmpty();
   }

   @Override
   @SuppressWarnings("unchecked")
   public void set(final E newValue) {
      final E oldValue = value;

      // do nothing if value is the same object
      if (!isModification(oldValue, newValue))
         return;

      value = newValue;

      for (final Object observer : observers) {
         try {
            if (observer instanceof Runnable) {
               ((Runnable) observer).run();
            } else if (observer instanceof Consumer) {
               ((Consumer<E>) observer).accept(newValue);
            } else if (observer instanceof BiConsumer) {
               ((BiConsumer<E, E>) observer).accept(oldValue, newValue);
            }
         } catch (final Exception ex) {
            LOG.error(ex);
         }
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

      observers.add(observer);
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

      observers.remove(observer);
   }

   public void unsubscribe(final Runnable observer) {
      unsubscribe((Object) observer);
   }
}
