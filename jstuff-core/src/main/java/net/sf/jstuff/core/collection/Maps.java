/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
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
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.Maps.MapDiff.EntryValueDiff;
import net.sf.jstuff.core.comparator.SortDirection;
import net.sf.jstuff.core.functional.IsEqual;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

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
         return entryValueDiffs.size() > 0 || leftOnlyEntries.size() > 0 || rightOnlyEntries.size() > 0;
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
      Args.notNull("leftMap", leftMap);
      Args.notNull("rightMap", rightMap);
      Args.notNull("isEqual", isEqual);

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

   public static <K, V> ArrayList<K> keysAsArrayList(final Map<K, V> map) {
      Args.notNull("map", map);

      return CollectionUtils.newArrayList(map.keySet());
   }

   /**
    * Returns string in the format of "name1=value1,name2=value2" (if valueSeparator="," and assignmentOperator="=")
    *
    * Opposite to {@link #toMap(String, String, String)}
    */
   public static <K, V> CharSequence map2string(final Map<K, V> values, final String valueSeparator, final String assignmentOperator) {
      Args.notNull("values", values);
      Args.notNull("valueSeparator", valueSeparator);
      Args.notNull("assignmentOperator", assignmentOperator);

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

      return putAll(new HashMap<K, V>(1 + initialKeysAndValues.length / 2), initialKeysAndValues);
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

      return putAll(new LinkedHashMap<K, V>(1 + initialKeysAndValues.length / 2), initialKeysAndValues);
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

   public static <K, V, KK extends K, VV extends V> TreeMap<K, V> newTreeMap(final Comparator<? super K> keyComparator,
      final Object @Nullable [] initialKeysAndValues) {
      if (initialKeysAndValues == null)
         return new TreeMap<>(keyComparator);

      return putAll(new TreeMap<K, V>(keyComparator), initialKeysAndValues);
   }

   public static <K, V, KK extends K, VV extends V> TreeMap<K, V> newTreeMap(final KK firstKey, final VV firstValue,
      final Object... moreInitialKeysAndValues) {
      return putAll(new TreeMap<>(), firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V, M extends Map<K, V>> M putAll(final M map, final K[] keys, final V[] values) {
      Args.notNull("map", map);
      Args.notNull("keys", keys);
      Args.notNull("values", values);

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
      Args.notNull("map", map);

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
      Args.notNull("map", map);

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

   public static <K, V extends Comparable<V>> Map<K, V> sortByValue(final Map<K, V> map) {
      return sortByValue(map, SortDirection.ASC);
   }

   public static <K, V extends Comparable<V>> Map<K, V> sortByValue(final Map<K, V> map, final SortDirection direction) {
      Args.notNull("map", map);

      if (map.isEmpty())
         return map;

      Args.notNull("direction", direction);

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

   public static <K, V> Map<K, V> sortByValue(final Map<K, V> map, final Comparator<V> comparator) {
      Args.notNull("map", map);

      if (map.isEmpty())
         return map;

      Args.notNull("comparator", comparator);

      final var entries = new ArrayList<>(map.entrySet());
      entries.sort((o1, o2) -> comparator.compare(o1.getValue(), o2.getValue()));

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
      Args.notNull("valuePairs", valuePairs);
      Args.notNull("valueSeparator", valueSeparator);
      Args.notNull("assignmentOperator", assignmentOperator);

      final var result = new HashMap<String, String>();
      for (final var element : Strings.split(valuePairs, valueSeparator)) {
         final var valuePairSplitted = Strings.split(element, assignmentOperator);
         result.put(valuePairSplitted[0], valuePairSplitted[1]);
      }
      return result;
   }

   @SafeVarargs
   public static <T> Map<T, T> toMap(final T... keysAndValues) {
      Args.notNull("keysAndValues", keysAndValues);

      final var result = new HashMap<T, T>();
      boolean isKey = true;
      @Nullable
      T key = null;
      for (final T item : keysAndValues)
         if (isKey) {
            key = item;
            isKey = false;
         } else {
            result.put(key, item);
            isKey = true;
         }
      return result;
   }

}
