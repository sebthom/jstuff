/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
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
public interface MutableObservableRef<V> extends MutableRef<V>, ObservableRef<V> {

   class Default<V> extends MutableRef.Default<V> implements MutableObservableRef<V> {
      private static final Logger LOG = Logger.create();

      private final CopyOnWriteArraySet<Object> observers = new CopyOnWriteArraySet<>();

      protected Default() {
      }

      protected Default(final V initialValue) {
         super(initialValue);
      }

      @Override
      public V get() {
         return value;
      }

      protected boolean isModification(final V oldValue, final V newValue) {
         if (newValue == oldValue //
            || isScalarValue(newValue) //
               && isScalarValue(oldValue) //
               && Objects.equals(newValue, oldValue))
            return false;

         return true;
      }

      @Override
      public boolean isObserved() {
         return !observers.isEmpty();
      }

      @Override
      public boolean isObserving(final Object observer) {
         return observer != null && observers.contains(observer);
      }

      protected boolean isScalarValue(final V value) {
         return value == null //
            || value instanceof String //
            || value instanceof BigInteger //
            || value instanceof Long //
            || value instanceof Integer //
            || value instanceof Short //
            || value instanceof Byte //
            || value instanceof Character;
      }

      @Override
      @SuppressWarnings("unchecked")
      public void set(final V newValue) {
         final V oldValue = value;

         // do nothing if value is the same object
         if (!isModification(oldValue, newValue))
            return;

         value = newValue;

         for (final Object observer : observers) {
            try {
               if (observer instanceof Runnable) {
                  ((Runnable) observer).run();
               } else if (observer instanceof Consumer) {
                  ((Consumer<V>) observer).accept(newValue);
               } else if (observer instanceof BiConsumer) {
                  ((BiConsumer<V, V>) observer).accept(oldValue, newValue);
               }
            } catch (final Exception ex) {
               LOG.error(ex);
            }
         }
      }

      @Override
      public void subscribe(final BiConsumer<V, V> observer) {
         subscribe((Object) observer);
      }

      @Override
      public void subscribe(final Consumer<V> observer) {
         subscribe((Object) observer);
      }

      private void subscribe(final Object observer) {
         if (observer == null)
            return;

         observers.add(observer);
      }

      @Override
      public void subscribe(final Runnable observer) {
         subscribe((Object) observer);
      }

      @Override
      public void unsubscribe(final BiConsumer<V, V> observer) {
         unsubscribe((Object) observer);
      }

      @Override
      public void unsubscribe(final Consumer<V> observer) {
         unsubscribe((Object) observer);
      }

      private void unsubscribe(final Object observer) {
         if (observer == null)
            return;

         observers.remove(observer);
      }

      @Override
      public void unsubscribe(final Runnable observer) {
         unsubscribe((Object) observer);
      }

      public void unsubscribeAll() {
         observers.clear();
      }
   }

   static <V> MutableObservableRef<V> create() {
      return new Default<>();
   }

   static <V> MutableObservableRef<V> of(final V initialValue) {
      return new Default<>(initialValue);
   }
}
