/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.exception.Exceptions;
import net.sf.jstuff.core.ref.LazyInitializedRef;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Suppliers {

   public interface ExpirationPredicate<V> {
      boolean isExpired(V value, long ageMS);
   }

   public static <T> Supplier<T> fromCallable(final Callable<? extends T> callable) {
      return () -> {
         try {
            return callable.call();
         } catch (final Exception ex) {
            throw Exceptions.wrapAsRuntimeException(ex);
         }
      };
   }

   public static <I, O> Supplier<O> fromFunction(final Function<I, ? extends O> fn, final I input) {
      return () -> {
         try {
            return fn.apply(input);
         } catch (final Exception ex) {
            throw Exceptions.wrapAsRuntimeException(ex);
         }
      };
   }

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

   public static <T> Supplier<T> memoize(final Supplier<? extends T> provider, final ExpirationPredicate<T> isExpired) {
      Args.notNull("provider", provider);
      Args.notNull("isExpired", isExpired);

      return new Supplier<>() {

         private T cached = lateNonNull();
         private long cachedAt = -1;

         @Override
         public synchronized T get() {
            if (cachedAt != -1 && !isExpired.isExpired(cached, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - cachedAt)))
               return cached;

            cached = provider.get();
            cachedAt = System.nanoTime();
            return cached;
         }
      };
   }

   public static <T> Supplier<T> memoize(final Supplier<? extends T> provider, final int ttlMS) {
      return memoize(provider, (v, ageMS) -> ageMS > ttlMS);
   }

   public static <T> Supplier<T> memoize(final Supplier<? extends T> provider, final int ttl, final TimeUnit unit) {
      return memoize(provider, (v, ageMS) -> ageMS > unit.toMillis(ttl));
   }

   public static <T> Supplier<T> memoizeSoft(final Supplier<? extends T> provider) {
      Args.notNull("provider", provider);

      return new Supplier<>() {
         private @Nullable SoftReference<T> cached;

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

   public static <T> Supplier<T> memoizeSoft(final Supplier<? extends T> provider, final ExpirationPredicate<T> isExpired) {
      Args.notNull("provider", provider);
      Args.notNull("isExpired", isExpired);

      return new Supplier<>() {

         private SoftReference<T> cached = lateNonNull();
         private long cachedAt = -1;
         private boolean cachedNull = false;

         @Override
         public synchronized T get() {
            if (cachedAt != -1) {
               final var obj = cached.get();
               if ((cachedNull || obj != null) && !isExpired.isExpired(obj, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - cachedAt)))
                  return obj;
            }

            final T obj = provider.get();
            cachedNull = obj == null;
            cached = new SoftReference<>(obj);
            cachedAt = System.nanoTime();
            return obj;
         }
      };
   }

   public static <T> Supplier<T> memoizeSoft(final Supplier<? extends T> provider, final int ttlMS) {
      return memoizeSoft(provider, (v, ageMS) -> ageMS > ttlMS);
   }

   public static <T> Supplier<T> memoizeSoft(final Supplier<? extends T> provider, final int ttl, final TimeUnit unit) {
      return memoizeSoft(provider, (v, ageMS) -> ageMS > unit.toMillis(ttl));
   }

   public static <T> Supplier<T> memoizeWeak(final Supplier<? extends T> provider) {
      Args.notNull("provider", provider);

      return new Supplier<>() {
         private @Nullable WeakReference<T> cached;

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

   public static <T> Supplier<T> memoizeWeak(final Supplier<? extends T> provider, final ExpirationPredicate<T> isExpired) {
      Args.notNull("provider", provider);
      Args.notNull("isExpired", isExpired);

      return new Supplier<>() {

         private WeakReference<T> cached = lateNonNull();
         private long cachedAt = -1;
         private boolean cachedNull = false;

         @Override
         public synchronized T get() {
            if (cachedAt != -1) {
               final var obj = cached.get();
               if ((cachedNull || obj != null) && !isExpired.isExpired(obj, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - cachedAt)))
                  return obj;
            }

            final T obj = provider.get();
            cachedNull = obj == null;
            cached = new WeakReference<>(obj);
            cachedAt = System.nanoTime();
            return obj;
         }
      };
   }

   public static <T> Supplier<T> memoizeWeak(final Supplier<? extends T> provider, final int ttlMS) {
      return memoizeWeak(provider, (v, ageMS) -> ageMS > ttlMS);
   }

   public static <T> Supplier<T> memoizeWeak(final Supplier<? extends T> provider, final int ttl, final TimeUnit unit) {
      return memoizeWeak(provider, (v, ageMS) -> ageMS > unit.toMillis(ttl));
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

   public static <T> Supplier<T> synchronizedSupplier(final Supplier<T> delegate, final Lock lock) {
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
