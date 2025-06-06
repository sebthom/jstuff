/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;
import java.util.function.ObjIntConsumer;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.functional.BiObjIntConsumer;
import net.sf.jstuff.core.functional.ByteConsumer;
import net.sf.jstuff.core.functional.CharConsumer;
import net.sf.jstuff.core.functional.ThrowingConsumer;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Loops {

   private static final CompletableFuture<?> DONE = CompletableFuture.completedFuture(null);

   /* ********************
    * Enumerations
    * ********************/

   public static <T> void forEach(final @Nullable Enumeration<T> en, final @Nullable Consumer<T> consumer) {
      if (en == null || consumer == null)
         return;

      while (en.hasMoreElements()) {
         consumer.accept(en.nextElement());
      }
   }

   public static <T, X extends Throwable> void forEach(final @Nullable Enumeration<T> en, final @Nullable ThrowingConsumer<T, X> consumer)
         throws X {
      if (en == null || consumer == null)
         return;

      while (en.hasMoreElements()) {
         consumer.acceptOrThrow(en.nextElement());
      }
   }

   public static <T> void forEachWithIndex(final @Nullable Enumeration<T> en, final @Nullable ObjIntConsumer<T> consumer) {
      if (en == null || consumer == null)
         return;

      int i = -1;
      while (en.hasMoreElements()) {
         consumer.accept(en.nextElement(), ++i);
      }
   }

   public static <T> CompletableFuture<?> forEachConcurrent(final @Nullable Enumeration<T> en, final @Nullable ExecutorService workers,
         final @Nullable Consumer<T> consumer) {
      if (en == null || consumer == null)
         return DONE;

      return forEachWithIndexConcurrent(en, workers, (elem, idx) -> consumer.accept(elem));
   }

   @SuppressWarnings("null")
   public static <T> CompletableFuture<?> forEachWithIndexConcurrent(final @Nullable Enumeration<T> en, @Nullable ExecutorService workers,
         final @Nullable ObjIntConsumer<T> consumer) {
      if (en == null || consumer == null)
         return DONE;

      if (workers == null) {
         workers = ForkJoinPool.commonPool();
      }

      int i = -1;
      final var futures = new ArrayList<CompletableFuture<?>>();
      while (en.hasMoreElements()) {
         final var next = en.nextElement();
         final var idx = ++i;
         futures.add(CompletableFuture.runAsync(() -> consumer.accept(next, idx), workers));
      }

      return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
   }

   /* ********************
    * Iterables
    * ********************/

   public static <T> void forEach(final @Nullable Iterable<T> it, final @Nullable Consumer<T> consumer) {
      if (it == null || consumer == null)
         return;

      it.forEach(consumer);
   }

   public static <T, X extends Throwable> void forEach(final @Nullable Iterable<T> it, final @Nullable ThrowingConsumer<T, X> consumer)
         throws X {
      if (it == null || consumer == null)
         return;

      for (final T t : it) {
         consumer.acceptOrThrow(t);
      }
   }

   public static <T> void forEachWithIndex(final @Nullable Iterable<T> it, final @Nullable ObjIntConsumer<T> consumer) {
      if (it == null || consumer == null)
         return;

      int i = -1;
      for (final T t : it) {
         consumer.accept(t, ++i);
      }
   }

   public static <T> CompletableFuture<?> forEachConcurrent(final @Nullable Iterable<T> it, final @Nullable ExecutorService workers,
         final @Nullable Consumer<T> consumer) {
      if (it == null || consumer == null)
         return DONE;

      return forEachConcurrent(it.iterator(), workers, consumer);
   }

   public static <T> CompletableFuture<?> forEachWithIndexConcurrent(final @Nullable Iterable<T> it,
         final @Nullable ExecutorService workers, final @Nullable ObjIntConsumer<T> consumer) {
      if (it == null || consumer == null)
         return DONE;

      return forEachWithIndexConcurrent(it.iterator(), workers, consumer);
   }

   /* ********************
    * Iterators
    * ********************/

   public static <T> void forEach(final @Nullable Iterator<T> it, final @Nullable Consumer<T> consumer) {
      if (it == null || consumer == null)
         return;

      while (it.hasNext()) {
         consumer.accept(it.next());
      }
   }

   public static <T, X extends Throwable> void forEach(final @Nullable Iterator<T> it, final @Nullable ThrowingConsumer<T, X> consumer)
         throws X {
      if (it == null || consumer == null)
         return;

      while (it.hasNext()) {
         consumer.acceptOrThrow(it.next());
      }
   }

   public static <T> void forEachWithIndex(final @Nullable Iterator<T> it, final @Nullable ObjIntConsumer<T> consumer) {
      if (it == null || consumer == null)
         return;

      int i = -1;
      while (it.hasNext()) {
         consumer.accept(it.next(), ++i);
      }
   }

   public static <T> CompletableFuture<?> forEachConcurrent(final @Nullable Iterator<T> it, final @Nullable ExecutorService workers,
         final @Nullable Consumer<T> consumer) {
      if (it == null || consumer == null)
         return DONE;

      return forEachWithIndexConcurrent(it, workers, (elem, idx) -> consumer.accept(elem));
   }

   @SuppressWarnings("null")
   public static <T> CompletableFuture<?> forEachWithIndexConcurrent(final @Nullable Iterator<T> it, @Nullable ExecutorService workers,
         final @Nullable ObjIntConsumer<T> consumer) {
      if (it == null || consumer == null)
         return DONE;

      if (workers == null) {
         workers = ForkJoinPool.commonPool();
      }

      int i = -1;
      final var futures = new ArrayList<CompletableFuture<?>>();
      while (it.hasNext()) {
         final var next = it.next();
         final var idx = ++i;
         futures.add(CompletableFuture.runAsync(() -> consumer.accept(next, idx), workers));
      }

      return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
   }

   /* ********************
    * Maps
    * ********************/

   public static <K, V> void forEach(final @Nullable Map<K, V> map, final @Nullable BiConsumer<K, V> consumer) {
      if (map == null || consumer == null)
         return;

      map.entrySet().forEach(e -> consumer.accept(e.getKey(), e.getValue()));
   }

   public static <K, V> void forEach(final @Nullable Map<K, V> map, final @Nullable Consumer<Entry<K, V>> consumer) {
      if (map == null || consumer == null)
         return;

      map.entrySet().forEach(consumer);
   }

   public static <K, V, X extends Throwable> void forEach(final @Nullable Map<K, V> map,
         final @Nullable ThrowingConsumer<Entry<K, V>, X> consumer) throws X {
      if (map == null || consumer == null)
         return;

      for (final var e : map.entrySet()) {
         consumer.acceptOrThrow(e);
      }
   }

   public static <K, V> void forEachWithIndex(final @Nullable Map<K, V> map, final @Nullable BiObjIntConsumer<K, V> consumer) {
      if (map == null || consumer == null)
         return;

      int i = -1;
      for (final var e : map.entrySet()) {
         consumer.accept(e.getKey(), e.getValue(), ++i);
      }
   }

   public static <K, V> void forEachWithIndex(final @Nullable Map<K, V> map, final @Nullable ObjIntConsumer<Entry<K, V>> consumer) {
      if (map == null || consumer == null)
         return;

      forEachWithIndex(map.entrySet(), consumer);
   }

   public static <K, V> CompletableFuture<?> forEachConcurrent(final @Nullable Map<K, V> map, final @Nullable ExecutorService workers,
         final @Nullable BiConsumer<K, V> consumer) {
      if (map == null || consumer == null)
         return DONE;

      return forEachConcurrent(map.entrySet(), workers, e -> consumer.accept(e.getKey(), e.getValue()));
   }

   public static <K, V> CompletableFuture<?> forEachConcurrent(final @Nullable Map<K, V> map, final @Nullable ExecutorService workers,
         final @Nullable Consumer<Entry<K, V>> consumer) {
      if (map == null || consumer == null)
         return DONE;

      return forEachConcurrent(map.entrySet(), workers, consumer);
   }

   public static <K, V> CompletableFuture<?> forEachWithIndexConcurrent(final @Nullable Map<K, V> map,
         final @Nullable ExecutorService workers, final @Nullable BiObjIntConsumer<K, V> consumer) {
      if (map == null || consumer == null)
         return DONE;

      return forEachWithIndexConcurrent(map.entrySet(), workers, (e, idx) -> consumer.accept(e.getKey(), e.getValue(), idx));
   }

   public static <K, V> CompletableFuture<?> forEachWithIndexConcurrent(final @Nullable Map<K, V> map,
         final @Nullable ExecutorService workers, final @Nullable ObjIntConsumer<Entry<K, V>> consumer) {
      if (map == null || consumer == null)
         return DONE;

      return forEachWithIndexConcurrent(map.entrySet(), workers, consumer);
   }

   /* ********************
    * Object arrays
    * ********************/

   public static <T> void forEach(final T @Nullable [] array, final @Nullable Consumer<T> consumer) {
      if (array == null || consumer == null || array.length == 0)
         return;

      for (final T element : array) {
         consumer.accept(element);
      }
   }

   public static <T, X extends Throwable> void forEach(final T @Nullable [] array, final @Nullable ThrowingConsumer<T, X> consumer)
         throws X {
      if (array == null || consumer == null)
         return;

      for (final T element : array) {
         consumer.acceptOrThrow(element);
      }
   }

   @SafeVarargs
   public static <T> void forEach(final @Nullable Consumer<T> consumer, final T @Nullable... array) {
      forEach(array, consumer);
   }

   @SafeVarargs
   public static <T, X extends Throwable> void forEach(final @Nullable ThrowingConsumer<T, X> consumer, final T @Nullable... array)
         throws X {
      forEach(array, consumer);
   }

   public static <T> void forEachWithIndex(final T @Nullable [] array, final @Nullable ObjIntConsumer<T> consumer) {
      if (array == null || consumer == null || array.length == 0)
         return;

      for (int i = 0, l = array.length; i < l; i++) {
         consumer.accept(array[i], i);
      }
   }

   @SafeVarargs
   public static <T> void forEachWithIndex(final @Nullable ObjIntConsumer<T> consumer, final T @Nullable... array) {
      forEachWithIndex(array, consumer);
   }

   /* ********************
    * byte arrays
    * ********************/

   public static void forEach(final byte @Nullable [] array, final @Nullable ByteConsumer consumer) {
      if (array == null || consumer == null || array.length == 0)
         return;

      for (final byte element : array) {
         consumer.accept(element);
      }
   }

   public static void forEach(final @Nullable ByteConsumer consumer, final byte @Nullable... array) {
      forEach(array, consumer);
   }

   /* ********************
    * char arrays
    * ********************/

   public static void forEach(final char @Nullable [] array, final @Nullable CharConsumer consumer) {
      if (array == null || consumer == null || array.length == 0)
         return;

      for (final char element : array) {
         consumer.accept(element);
      }
   }

   public static void forEach(final @Nullable CharConsumer consumer, final char @Nullable... array) {
      forEach(array, consumer);
   }

   /* ********************
    * int arrays
    * ********************/

   public static void forEach(final int @Nullable [] array, final @Nullable IntConsumer consumer) {
      if (array == null || consumer == null || array.length == 0)
         return;

      for (final int element : array) {
         consumer.accept(element);
      }
   }

   public static void forEach(final @Nullable IntConsumer consumer, final int @Nullable... array) {
      forEach(array, consumer);
   }

   /* ********************
    * long arrays
    * ********************/

   public static void forEach(final long @Nullable [] array, final @Nullable LongConsumer consumer) {
      if (array == null || consumer == null || array.length == 0)
         return;

      for (final long element : array) {
         consumer.accept(element);
      }
   }

   public static void forEach(final @Nullable LongConsumer consumer, final long @Nullable... array) {
      forEach(array, consumer);
   }

   /* ********************
    * ranges
    * ********************/

   public static void forRange(final int startInclusive, final int endExclusive, final @Nullable IntConsumer consumer) {
      forRange(startInclusive, endExclusive, 1, consumer);
   }

   public static void forRange(final int startInclusive, final int endExclusive, final int offset, final @Nullable IntConsumer consumer) {
      if (consumer == null)
         return;

      for (long i = startInclusive; i < endExclusive; i += offset) {
         consumer.accept(endExclusive);
      }
   }

   public static void forRange(final long startInclusive, final long endExclusive, final @Nullable LongConsumer consumer) {
      forRange(startInclusive, endExclusive, 1, consumer);
   }

   public static void forRange(final long startInclusive, final long endExclusive, final long offset,
         final @Nullable LongConsumer consumer) {
      if (consumer == null)
         return;

      for (long i = startInclusive; i < endExclusive; i += offset) {
         consumer.accept(endExclusive);
      }
   }
}
