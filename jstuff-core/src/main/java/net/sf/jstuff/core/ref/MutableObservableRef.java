/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.ref;

import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.concurrent.SafeAwait;
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
      public void await(final V desiredValue) throws InterruptedException {
         SafeAwait.await(() -> !isModification(value, desiredValue), this);
      }

      @Override
      public boolean await(final V desiredValue, final long timeout, final TimeUnit unit) throws InterruptedException {
         return SafeAwait.await(() -> !isModification(value, desiredValue), this, unit.toMillis(timeout));
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
      public boolean isObserving(final @Nullable Object observer) {
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

         synchronized (this) {
            value = newValue;
            notifyAll();
         }

         for (final Object observer : observers) {
            try {
               if (observer instanceof final Runnable r) {
                  r.run();
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

      private void subscribe(final @Nullable Object observer) {
         if (observer == null)
            return;

         observers.add(observer);
      }

      @Override
      public void subscribe(final Runnable observer) {
         subscribe((Object) observer);
      }

      @Override
      public String toString() {
         return String.valueOf(get());
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

   static <@Nullable V> MutableObservableRef<V> create() {
      return new Default<>();
   }

   static <@NonNull V> MutableObservableRef<V> of(final V initialValue) {
      return new Default<>(initialValue);
   }

   static <@Nullable V extends @Nullable Object> MutableObservableRef<V> ofNullable(final V initialValue) {
      return new Default<>(initialValue);
   }

}
