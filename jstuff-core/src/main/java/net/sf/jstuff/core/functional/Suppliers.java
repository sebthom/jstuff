/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.ref.LazyInitializedRef;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Suppliers {

   public static <T> Supplier<T> memoize(final Supplier<? extends T> provider) {
      Args.notNull("provider", provider);

      return new Supplier<>() {
         private final LazyInitializedRef<T> cached = new LazyInitializedRef<>() {
            @Override
            protected T create() {
               return provider.get();
            }
         };

         @Override
         public T get() {
            return cached.get();
         }
      };
   }

   public static <T> Supplier<T> memoize(final Supplier<? extends T> provider, final ToIntFunction<T> ttl) {
      Args.notNull("provider", provider);
      Args.notNull("ttl", ttl);

      return new Supplier<>() {
         @Nullable
         private Tuple2<Long, T> cached;

         @Override
         public synchronized T get() {
            final var cached = this.cached;
            if (cached != null && System.currentTimeMillis() - cached.get1() <= ttl.applyAsInt(cached.get2()))
               return cached.get2();

            final T obj = provider.get();
            this.cached = new Tuple2<>(System.currentTimeMillis(), obj);
            return obj;
         }
      };
   }

   public static <T> Supplier<T> memoize(final Supplier<? extends T> provider, final int ttl) {
      return memoize(provider, t -> ttl);
   }

   public static <T> Supplier<T> memoizeSoft(final Supplier<? extends T> provider) {
      Args.notNull("provider", provider);

      return new Supplier<>() {
         @Nullable
         private SoftReference<T> cached;

         @Override
         public synchronized T get() {
            if (cached != null) {
               final T val = cached.get();
               if (val != null)
                  return val;
            }

            final T obj = provider.get();
            cached = new SoftReference<>(obj);
            return obj;
         }
      };
   }

   public static <T> Supplier<T> memoizeSoft(final Supplier<? extends T> provider, final ToIntFunction<T> ttl) {
      Args.notNull("provider", provider);
      Args.notNull("ttl", ttl);

      return new Supplier<>() {
         @Nullable
         private SoftReference<@Nullable Tuple2<Long, T>> cached;

         @Override
         public synchronized T get() {
            final var cached = this.cached;
            if (cached != null) {
               final var val = cached.get();
               if (val != null && System.currentTimeMillis() - val.get1() <= ttl.applyAsInt(val.get2()))
                  return val.get2();
            }

            final T obj = provider.get();
            this.cached = new SoftReference<>(new Tuple2<>(System.currentTimeMillis(), obj));
            return obj;
         }
      };
   }

   public static <T> Supplier<T> memoizeSoft(final Supplier<? extends T> provider, final int ttl) {
      return memoizeSoft(provider, t -> ttl);
   }

   public static <T> Supplier<T> memoizeWeak(final Supplier<? extends T> provider) {
      Args.notNull("provider", provider);

      return new Supplier<>() {
         @Nullable
         private WeakReference<T> cached;

         @Override
         public synchronized T get() {
            if (cached != null) {
               final T val = cached.get();
               if (val != null)
                  return val;
            }

            final T obj = provider.get();
            cached = new WeakReference<>(obj);
            return obj;
         }
      };
   }

   public static <T> Supplier<T> memoizeWeak(final Supplier<? extends T> provider, final ToIntFunction<T> ttl) {
      Args.notNull("provider", provider);
      Args.notNull("ttl", ttl);

      return new Supplier<>() {
         @Nullable
         private WeakReference<@Nullable Tuple2<Long, T>> cached;

         @Override
         public synchronized T get() {
            if (cached != null) {
               final Tuple2<Long, T> val = cached.get();
               if (val != null && System.currentTimeMillis() - val.get1() <= ttl.applyAsInt(val.get2()))
                  return val.get2();
            }

            final T obj = provider.get();
            cached = new WeakReference<>(new Tuple2<>(System.currentTimeMillis(), obj));
            return obj;
         }
      };
   }

   public static <T> Supplier<T> memoizeWeak(final Supplier<? extends T> provider, final int ttl) {
      return memoizeWeak(provider, t -> ttl);
   }

   public static <T> Supplier<T> of(final T object) {
      return () -> object;
   }

   public static <T> Supplier<T> synchronizedSupplier(final Supplier<T> delegate) {
      Args.notNull("delegate", delegate);

      return () -> {
         synchronized (delegate) {
            return delegate.get();
         }
      };
   }

   public static <T> Supplier<T> synchronizedSupplier(final Supplier<T> delegate, final Object lock) {
      Args.notNull("delegate", delegate);
      Args.notNull("lock", lock);

      return () -> {
         synchronized (lock) {
            return delegate.get();
         }
      };
   }

   public static <T> Supplier<T> synchronizedSupplier(final Supplier<T> delegate, final ReadLock lock) {
      Args.notNull("delegate", delegate);
      Args.notNull("lock", lock);

      return () -> {
         lock.lock();
         try {
            return delegate.get();
         } finally {
            lock.unlock();
         }

      };
   }
}
