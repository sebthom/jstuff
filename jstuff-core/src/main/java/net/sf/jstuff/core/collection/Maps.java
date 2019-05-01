/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.Maps.MapDiff.EntryValueDiff;
import net.sf.jstuff.core.functional.IsEqual;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Maps {

   public static class MapDiff<K, V> implements Serializable {

      public static class EntryValueDiff<K, V> implements Serializable {
         private static final long serialVersionUID = 1L;

         public final K key;

         public final Map<K, V> leftMap;
         public final V leftValue;

         public final Map<K, V> rightMap;
         public final V rightValue;

         public EntryValueDiff(final Map<K, V> leftMap, final Map<K, V> rightMap, final K key, final V leftValue, final V rightValue) {
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
         return MapDiff.class.getSimpleName() + " [entryValueDiffs=" + entryValueDiffs + ", leftOnlyEntries=" + leftOnlyEntries + ", rightOnlyEntries="
            + rightOnlyEntries + "]";
      }
   }

   public static <K, V> MapDiff<K, V> diff(final Map<K, V> leftMap, final Map<K, V> rightMap) {
      return diff(leftMap, rightMap, IsEqual.DEFAULT);
   }

   public static <K, V> MapDiff<K, V> diff(final Map<K, V> leftMap, final Map<K, V> rightMap, final IsEqual<? super V> isEqual) {
      Args.notNull("leftMap", leftMap);
      Args.notNull("rightMap", rightMap);
      Args.notNull("isEqual", isEqual);

      final MapDiff<K, V> mapDiff = new MapDiff<>(leftMap, rightMap);
      final Set<K> processedLeftKeys = CollectionUtils.newHashSet(Math.max(leftMap.size(), rightMap.size()));

      /*
       * process the entries of the left map
       */
      for (final Entry<K, V> leftEntry : leftMap.entrySet()) {
         final K leftKey = leftEntry.getKey();
         final V leftValue = leftEntry.getValue();

         if (rightMap.containsKey(leftKey)) {
            final V rightValue = rightMap.get(leftKey);
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

   public static <K, V> V get(final Map<K, V> map, final K key, final V defaultValue) {
      if (map == null)
         return null;

      if (map.containsKey(key))
         return map.get(key);
      return defaultValue;
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
      if (values == null)
         return null;
      Args.notNull("valueSeparator", valueSeparator);
      Args.notNull("assignmentOperator", assignmentOperator);

      final StringBuilder sb = new StringBuilder();

      for (final Iterator<Map.Entry<K, V>> it = values.entrySet().iterator(); it.hasNext();) {
         final Map.Entry<K, V> entry = it.next();
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

   public static <K, V, KK extends K, VV extends V> HashMap<K, V> newHashMap(final KK firstKey, final VV firstValue, final Object... moreInitialKeysAndValues) {
      final HashMap<K, V> m = new HashMap<>(1 + moreInitialKeysAndValues.length / 2);
      return putAll(m, firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V> HashMap<K, V> newHashMap(final Map<? extends K, ? extends V> initialValues) {
      return initialValues == null ? new HashMap<>() : new HashMap<>(initialValues);
   }

   public static <K, V> HashMap<K, V> newHashMap(final Object[] initialKeysAndValues) {
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
      final LinkedHashMap<K, V> m = new LinkedHashMap<>(1 + moreInitialKeysAndValues.length / 2);
      return putAll(m, firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(final Object[] initialKeysAndValues) {
      if (initialKeysAndValues == null)
         return new LinkedHashMap<>();

      return putAll(new LinkedHashMap<K, V>(1 + initialKeysAndValues.length / 2), initialKeysAndValues);
   }

   public static <K, V> ThreadLocal<HashMap<K, V>> newThreadLocalHashMap() {
      return new ThreadLocal<HashMap<K, V>>() {
         @Override
         public HashMap<K, V> initialValue() {
            return new HashMap<>();
         }
      };
   }

   public static <K, V> ThreadLocal<WeakHashMap<K, V>> newThreadLocalWeakHashMap() {
      return new ThreadLocal<WeakHashMap<K, V>>() {
         @Override
         public WeakHashMap<K, V> initialValue() {
            return new WeakHashMap<>();
         }
      };
   }

   public static <K, V> TreeMap<K, V> newTreeMap() {
      return new TreeMap<>();
   }

   public static <K, V> TreeMap<K, V> newTreeMap(final Comparator<? super K> keyComparator) {
      return new TreeMap<>(keyComparator);
   }

   public static <K, V, KK extends K, VV extends V> TreeMap<K, V> newTreeMap(final Comparator<? super K> keyComparator, final KK firstKey, final VV firstValue,
      final Object... moreInitialKeysAndValues) {
      final TreeMap<K, V> m = new TreeMap<>(keyComparator);
      return putAll(m, firstKey, firstValue, moreInitialKeysAndValues);
   }

   public static <K, V, KK extends K, VV extends V> TreeMap<K, V> newTreeMap(final Comparator<? super K> keyComparator, final Object[] initialKeysAndValues) {
      if (initialKeysAndValues == null)
         return new TreeMap<>(keyComparator);

      return putAll(new TreeMap<K, V>(keyComparator), initialKeysAndValues);
   }

   public static <K, V, KK extends K, VV extends V> TreeMap<K, V> newTreeMap(final KK firstKey, final VV firstValue, final Object... moreInitialKeysAndValues) {
      final TreeMap<K, V> m = new TreeMap<>();
      return putAll(m, firstKey, firstValue, moreInitialKeysAndValues);
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
      final Object... moreInitialKeysAndValues) {
      Args.notNull("map", map);

      map.put(firstKey, firstValue);

      boolean nextIsValue = false;
      K key = null;
      for (final Object obj : moreInitialKeysAndValues)
         if (nextIsValue) {
            map.put(key, (V) obj);
            nextIsValue = false;
         } else {
            key = (K) obj;
            nextIsValue = true;
         }
      return map;
   }

   @SuppressWarnings("unchecked")
   public static <K, V, M extends Map<K, V>> M putAll(final M map, final Object[] keysAndValues) {
      Args.notNull("map", map);
      if (keysAndValues == null || keysAndValues.length == 0)
         return map;

      boolean nextIsValue = false;
      K key = null;
      for (final Object obj : keysAndValues)
         if (nextIsValue) {
            map.put(key, (V) obj);
            nextIsValue = false;
         } else {
            key = (K) obj;
            nextIsValue = true;
         }
      return map;
   }

   public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(final Map<K, V> map) {
      if (map == null)
         return null;
      if (map.isEmpty())
         return map;

      final List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
      Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
         @Override
         public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
            return o1.getValue().compareTo(o2.getValue());
         }
      });

      final Map<K, V> result = new LinkedHashMap<>();
      for (final Map.Entry<K, V> entry : entries) {
         result.put(entry.getKey(), entry.getValue());
      }
      return result;
   }

   public static <K, V> Map<K, V> sortByValue(final Map<K, V> map, final Comparator<V> comparator) {
      if (map == null)
         return null;
      if (map.isEmpty())
         return map;

      Args.notNull("comparator", comparator);

      final List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
      Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
         @Override
         public int compare(final Map.Entry<K, V> o1, final Map.Entry<K, V> o2) {
            return comparator.compare(o1.getValue(), o2.getValue());
         }
      });

      final Map<K, V> result = new LinkedHashMap<>();
      for (final Map.Entry<K, V> entry : entries) {
         result.put(entry.getKey(), entry.getValue());
      }
      return result;
   }

   /**
    * Converts key/value pairs defined in a string into a map.
    *
    * E.g. toMap("name1=value1,name2=value2", "\"", "=")
    */
   public static Map<String, String> toMap(final String valuePairs, final String valueSeparator, final String assignmentOperator) {
      if (valuePairs == null)
         return null;

      Args.notNull("valueSeparator", valueSeparator);
      Args.notNull("assignmentOperator", assignmentOperator);

      final Map<String, String> result = newHashMap();
      for (final String element : Strings.split(valuePairs, valueSeparator)) {
         final String[] valuePairSplitted = Strings.split(element, assignmentOperator);
         result.put(valuePairSplitted[0], valuePairSplitted[1]);
      }
      return result;
   }

   @SafeVarargs
   public static <T> Map<T, T> toMap(final T... keysAndValues) {
      if (keysAndValues == null)
         return null;

      final Map<T, T> result = newHashMap();
      boolean isKey = true;
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
