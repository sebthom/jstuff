/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.collection;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.Maps.MapDiff.EntryValueDiff;
import net.sf.jstuff.core.comparator.SortDirection;
import net.sf.jstuff.core.functional.IsEqual;
import net.sf.jstuff.core.reflection.Types;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Maps {

   public static class MapDiff<K, V> implements Serializable {

      public static class EntryValueDiff<K, V> implements Serializable {
         private static final long serialVersionUID = 1L;

         public final K key;

         public final Map<K, V> leftMap;
         public final @Nullable V leftValue;

         public final Map<K, V> rightMap;
         public final @Nullable V rightValue;

         public EntryValueDiff(final Map<K, V> leftMap, final Map<K, V> rightMap, final K key, final @Nullable V leftValue,
               final @Nullable V rightValue) {
            this.leftMap = leftMap;
            this.rightMap = rightMap;
            this.key = key;
            this.leftValue = leftValue;
            this.rightValue = rightValue;
         }

         @Override
         public String toString() {
            return EntryValueDiff.class.getSimpleName() + " [key=" + key + ", leftValue=" + leftValue + ", rightValue=" + rightValue + "]";
         }
      }

      private static final long serialVersionUID = 1L;

      public final List<EntryValueDiff<K, V>> entryValueDiffs = CollectionUtils.newArrayList();

      public final Map<K, V> leftMap;
      public final Map<K, V> leftOnlyEntries = newHashMap();
      public final Map<K, V> rightMap;
      public final Map<K, V> rightOnlyEntries = newHashMap();

      public MapDiff(final Map<K, V> leftMap, final Map<K, V> rightMap) {
         this.leftMap = leftMap;
         this.rightMap = rightMap;
      }

      public boolean isDifferent() {
         return !entryValueDiffs.isEmpty() || !leftOnlyEntries.isEmpty() || !rightOnlyEntries.isEmpty();
      }

      @Override
      public String toString() {
         return MapDiff.class.getSimpleName() + " [entryValueDiffs=" + entryValueDiffs + ", leftOnlyEntries=" + leftOnlyEntries
               + ", rightOnlyEntries=" + rightOnlyEntries + "]";
      }
   }

   public static <K, V> MapDiff<K, V> diff(final Map<K, V> leftMap, final Map<K, V> rightMap) {
      return diff(leftMap, rightMap, IsEqual.DEFAULT);
   }

   public static <K, V> MapDiff<K, V> diff(final Map<K, V> leftMap, final Map<K, V> rightMap, final IsEqual<? super V> isEqual) {
      final var mapDiff = new MapDiff<>(leftMap, rightMap);
      final var processedLeftKeys = new HashSet<K>(Math.max(leftMap.size(), rightMap.size()));

      /*
       * process the entries of the left map
       */
      for (final Entry<K, V> leftEntry : leftMap.entrySet()) {
         final K leftKey = leftEntry.getKey();
         final V leftValue = leftEntry.getValue();

         if (rightMap.containsKey(leftKey)) {
            final var rightValue = rightMap.get(leftKey);
            if (!isEqual.isEqual(leftValue, rightValue)) {
               mapDiff.entryValueDiffs.add(new EntryValueDiff<>(leftMap, rightMap, leftKey, leftValue, rightValue));
            }
         } else {
            mapDiff.leftOnlyEntries.put(leftKey, leftValue);
         }
         processedLeftKeys.add(leftKey);
      }

      /*
       * process remaining entries of the right map
       */
      for (final Entry<K, V> rightEntry : rightMap.entrySet()) {
         final K rightKey = rightEntry.getKey();

         if (processedLeftKeys.contains(rightKey)) {
            continue;
         }
         mapDiff.rightOnlyEntries.put(rightKey, rightEntry.getValue());
      }

      return mapDiff;
   }

   public static <K, V> V get(final @Nullable Map<K, V> map, final K key, final V defaultValue) {
      if (map == null)
         return defaultValue;
      return map.getOrDefault(key, defaultValue);
   }

   public static <K, V> V getOrThrow(final Map<K, V> map, final K key) throws NoSuchElementException {
      if (map.containsKey(key)) {
         final var v = map.get(key);
         return asNonNullUnsafe(v);
      }
      throw new NoSuchElementException("Key [" + key + "] not present");
   }

   public static <K, V> ArrayList<K> keysAsArrayList(final Map<K, V> map) {
      return CollectionUtils.newArrayList(map.keySet());
   }

   /**
    * Returns string in the format of "name1=value1,name2=value2" (if valueSeparator="," and assignmentOperator="=")
    *
    * Opposite to {@link #toMap(String, String, String)}
    */
   public static <K, V> CharSequence map2string(final Map<K, V> values, final String valueSeparator, final String assignmentOperator) {
      final var sb = new StringBuilder();

      for (final Iterator<Entry<K, V>> it = values.entrySet().iterator(); it.hasNext();) {
         final Entry<K, V> entry = it.next();
         sb.append(entry.getKey());
         sb.append(assignmentOperator);
         sb.append(entry.getValue());
         if (it.hasNext()) {
            sb.append(valueSeparator);
         }
      }
      return sb;
   }

   public static <K, V> HashMap<K, V> newHashMap() {
      return new HashMap<>();
   }

   public static <K, V> HashMap<K, V> newHashMap(final int initialSize) {
      return new HashMap<>(initialSize);
   }

   public static <K, V, KK extends K, VV extends V> HashMap<K, V> newHashMap(final KK firstKey, final VV firstValue,
         final Object... moreInitialKeysAndValues) {
      return putAll(new HashMap<>(1 + moreInitialKeysAndValues.length / 2), firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V> HashMap<K, V> newHashMap(final @Nullable Map<? extends K, ? extends V> initialValues) {
      return initialValues == null ? new HashMap<>() : new HashMap<>(initialValues);
   }

   public static <K, V> HashMap<K, V> newHashMap(final Object @Nullable [] initialKeysAndValues) {
      if (initialKeysAndValues == null)
         return new HashMap<>();

      return putAll(new HashMap<>(1 + initialKeysAndValues.length / 2), initialKeysAndValues);
   }

   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
      return new LinkedHashMap<>();
   }

   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final int initialSize) {
      return new LinkedHashMap<>(initialSize);
   }

   public static <K, V, KK extends K, VV extends V> LinkedHashMap<K, V> newLinkedHashMap(final KK firstKey, final VV firstValue,
         final Object... moreInitialKeysAndValues) {
      return putAll(new LinkedHashMap<>(1 + moreInitialKeysAndValues.length / 2), firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final Object @Nullable [] initialKeysAndValues) {
      if (initialKeysAndValues == null)
         return new LinkedHashMap<>();

      return putAll(new LinkedHashMap<>(1 + initialKeysAndValues.length / 2), initialKeysAndValues);
   }

   public static <K, V> ThreadLocal<HashMap<K, V>> newThreadLocalHashMap() {
      return ThreadLocal.withInitial(HashMap::new);
   }

   public static <K, V> ThreadLocal<WeakHashMap<K, V>> newThreadLocalWeakHashMap() {
      return ThreadLocal.withInitial(WeakHashMap::new);
   }

   public static <K, V> TreeMap<K, V> newTreeMap() {
      return new TreeMap<>();
   }

   public static <K, V> TreeMap<K, V> newTreeMap(final Comparator<? super K> keyComparator) {
      return new TreeMap<>(keyComparator);
   }

   public static <K, V, KK extends K, VV extends V> TreeMap<K, V> newTreeMap(final Comparator<? super K> keyComparator, final KK firstKey,
         final VV firstValue, final Object... moreInitialKeysAndValues) {
      return putAll(new TreeMap<>(keyComparator), firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V> TreeMap<K, V> newTreeMap(final Comparator<? super K> keyComparator,
         final Object @Nullable [] initialKeysAndValues) {
      if (initialKeysAndValues == null)
         return new TreeMap<>(keyComparator);

      return putAll(new TreeMap<>(keyComparator), initialKeysAndValues);
   }

   public static <K, V, KK extends K, VV extends V> TreeMap<K, V> newTreeMap(final KK firstKey, final VV firstValue,
         final Object... moreInitialKeysAndValues) {
      return putAll(new TreeMap<>(), firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V, M extends Map<K, V>> M putAll(final M map, final K[] keys, final V[] values) {
      if (keys.length != values.length)
         throw new IllegalArgumentException("Arguments [keys] and [values] must have the same array size.");

      for (int i = 0; i < keys.length; i++) {
         map.put(keys[i], values[i]);
      }
      return map;
   }

   @SuppressWarnings("unchecked")
   public static <K, V, KK extends K, VV extends V, M extends Map<K, V>> M putAll(final M map, final KK firstKey, final VV firstValue,
         final Object... moreKeysAndValues) {
      map.put(firstKey, firstValue);

      boolean nextIsValue = false;
      @Nullable
      K key = null;
      for (final Object obj : moreKeysAndValues)
         if (nextIsValue) {
            map.put(asNonNullUnsafe(key), (V) obj);
            nextIsValue = false;
         } else {
            key = (K) obj;
            nextIsValue = true;
         }
      return map;
   }

   @SuppressWarnings("unchecked")
   public static <K, V, M extends Map<K, V>> M putAll(final M map, final Object @Nullable [] keysAndValues) {
      if (keysAndValues == null || keysAndValues.length == 0)
         return map;

      boolean nextIsValue = false;
      @Nullable
      K key = null;
      for (final Object obj : keysAndValues)
         if (nextIsValue) {
            map.put(asNonNullUnsafe(key), (V) obj);
            nextIsValue = false;
         } else {
            key = (K) obj;
            nextIsValue = true;
         }
      return map;
   }

   public static <K, V> Map<K, V> putAllIfAbsent(final Map<K, V> map, final Map<? extends K, ? extends V> entriesToAdd) {
      if (entriesToAdd.isEmpty())
         return map;

      entriesToAdd.forEach(map::putIfAbsent);
      return map;
   }

   public static <K, V, M extends Map<K, V>> M putAllIfAbsent(final M map, final K[] keys, final V[] values) {
      if (keys.length != values.length)
         throw new IllegalArgumentException("Arguments [keys] and [values] must have the same array size.");

      for (int i = 0; i < keys.length; i++) {
         map.putIfAbsent(keys[i], values[i]);
      }
      return map;
   }

   @SuppressWarnings("unchecked")
   public static <K, V, KK extends K, VV extends V, M extends Map<K, V>> M putAllIfAbsent(final M map, final KK firstKey,
         final VV firstValue, final Object... moreKeysAndValues) {
      map.put(firstKey, firstValue);

      boolean nextIsValue = false;
      @Nullable
      K key = null;
      for (final Object obj : moreKeysAndValues)
         if (nextIsValue) {
            map.putIfAbsent(asNonNullUnsafe(key), (V) obj);
            nextIsValue = false;
         } else {
            key = (K) obj;
            nextIsValue = true;
         }
      return map;
   }

   @SuppressWarnings("unchecked")
   public static <K, V, M extends Map<K, V>> M putAllIfAbsent(final M map, final Object @Nullable [] keysAndValues) {
      if (keysAndValues == null || keysAndValues.length == 0)
         return map;

      boolean nextIsValue = false;
      @Nullable
      K key = null;
      for (final Object obj : keysAndValues)
         if (nextIsValue) {
            map.putIfAbsent(asNonNullUnsafe(key), (V) obj);
            nextIsValue = false;
         } else {
            key = (K) obj;
            nextIsValue = true;
         }
      return map;
   }

   public static <KK, V> Map<KK, V> remap(final Map<?, V> map, final Function<V, KK> keyMapper) {
      final var newMap = new HashMap<KK, V>();
      for (final V v : map.values()) {
         newMap.put(keyMapper.apply(v), v);
      }
      return newMap;
   }

   public static <KK, V, VV> Map<KK, VV> remap(final Map<?, V> map, final Function<V, KK> keyMapper, final Function<V, VV> valueMapper) {
      final var newMap = new HashMap<KK, VV>();
      for (final V v : map.values()) {
         newMap.put(keyMapper.apply(v), valueMapper.apply(v));
      }
      return newMap;
   }

   public static <K, KK, V> Map<KK, V> remap(final Map<K, V> map, final BiFunction<K, V, KK> keyMapper) {
      final var newMap = new HashMap<KK, V>();
      for (final var e : map.entrySet()) {
         newMap.put(keyMapper.apply(e.getKey(), e.getValue()), e.getValue());
      }
      return newMap;
   }

   public static <K, KK, V, VV> Map<KK, VV> remap(final Map<K, V> map, final BiFunction<K, V, KK> keyMapper,
         final BiFunction<K, V, VV> valueMapper) {
      final var newMap = new HashMap<KK, VV>();
      for (final var e : map.entrySet()) {
         newMap.put(keyMapper.apply(e.getKey(), e.getValue()), valueMapper.apply(e.getKey(), e.getValue()));
      }
      return newMap;
   }

   @NonNullByDefault({})
   public static <K, V extends Comparable<V>> @NonNull Map<K, V> sortByValue(final @NonNull Map<K, V> map) {
      return sortByValue(map, SortDirection.ASC);
   }

   public static <K, V> Map<K, V> sortByValue(final Map<K, V> map, final Comparator<V> comparator) {
      if (map.isEmpty())
         return map;

      final var entries = new ArrayList<>(map.entrySet());
      entries.sort((o1, o2) -> comparator.compare(o1.getValue(), o2.getValue()));

      final var sortedMap = new LinkedHashMap<K, V>();
      for (final Entry<K, V> e : entries) {
         sortedMap.put(e.getKey(), e.getValue());
      }
      return sortedMap;
   }

   @NonNullByDefault({})
   public static <K, V extends Comparable<V>> @NonNull Map<K, V> sortByValue(final @NonNull Map<K, V> map,
         final @NonNull SortDirection direction) {
      if (map.isEmpty())
         return map;

      final var entries = new ArrayList<>(map.entrySet());
      entries.sort((o1, o2) -> {
         final int valueCmp = ObjectUtils.compare(o1.getValue(), o2.getValue());
         if (valueCmp == 0) {
            final Object k1 = o1.getKey();
            final Object k2 = o2.getKey();
            if (k1 == null)
               return direction == SortDirection.ASC ? -1 : 1;
            if (k2 == null)
               return direction == SortDirection.ASC ? 1 : -1;

            if (k1 instanceof Comparable && Types.isAssignableTo(k2.getClass(), k1.getClass())) {
               @SuppressWarnings({"rawtypes", "unchecked"})
               final int keyCmp = ObjectUtils.compare((Comparable) k1, (Comparable) k2);
               return direction == SortDirection.ASC ? keyCmp : -keyCmp;
            }
            return 0;
         }
         return direction == SortDirection.ASC ? valueCmp : -valueCmp;
      });

      final var sortedMap = new LinkedHashMap<K, V>();
      for (final Entry<K, V> e : entries) {
         sortedMap.put(e.getKey(), e.getValue());
      }
      return sortedMap;
   }

   /**
    * Converts key/value pairs defined in a string into a map.
    *
    * E.g. toMap("name1=value1,name2=value2", "\"", "=")
    */
   public static Map<String, String> toMap(final String valuePairs, final String valueSeparator, final String assignmentOperator) {
      final var result = new HashMap<String, String>();
      for (final var element : Strings.split(valuePairs, valueSeparator)) {
         final var valuePairSplitted = Strings.split(element, assignmentOperator);
         result.put(valuePairSplitted[0], valuePairSplitted[1]);
      }
      return result;
   }

   @SafeVarargs
   public static <T> Map<T, T> toMap(final T... keysAndValues) {
      final var result = new HashMap<T, T>();
      boolean isKey = true;
      T key = lateNonNull();
      for (final T item : keysAndValues)
         if (isKey) {
            key = item;
            isKey = false;
         } else {
            result.put(asNonNullUnsafe(key), item);
            isKey = true;
         }
      return result;
   }
}
