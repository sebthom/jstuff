/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.types;

import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.reflection.Types;

/**
 * An alternative to {@link Enum} that supports generics and inheritance.
 * It is based on the type safe enum pattern described in Effective Java by Joshua Block.
 *
 * <p>
 * It allows to compare enumeration objects by reference (==), rather than with .equals(), even if the
 * enums were serialized and deserialized.
 * </p>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@SuppressWarnings("unchecked")
public abstract class ExtensibleEnum<ID> implements Serializable {
   private static final long serialVersionUID = 1L;

   private static final class WeakConcurrentMap<K, V> {
      private static final class WeakKey<T> extends WeakReference<T> {
         private final int hash;
         private final boolean useIdentity;

         WeakKey(final T referent, final ReferenceQueue<? super T> q, final boolean useIdentity) {
            super(referent, q);
            this.useIdentity = useIdentity;
            hash = referent == null //
                  ? 0
                  : useIdentity //
                        ? System.identityHashCode(referent)
                        : referent.hashCode();
         }

         @Override
         public int hashCode() {
            return hash;
         }

         @Override
         public boolean equals(final @Nullable Object obj) {
            if (this == obj)
               return true;
            if (obj instanceof final WeakKey<?> other) {
               final T referent = this.get();
               final Object otherReferent = other.get();
               if (referent == otherReferent)
                  return true;
               if (useIdentity || referent == null || otherReferent == null)
                  return false;
               return referent.equals(otherReferent);
            }
            return false;
         }
      }

      private WeakKey<K> createWeakKey(final K key) {
         return new WeakKey<>(key, referenceQueue, useIdentity);
      }

      private final Map<WeakKey<K>, V> map = new ConcurrentHashMap<>(2);
      private final ReferenceQueue<K> referenceQueue = new ReferenceQueue<>();
      private final boolean useIdentity;

      WeakConcurrentMap(final boolean useIdentity) {
         this.useIdentity = useIdentity;
      }

      V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
         processQueue();
         return map.computeIfAbsent(createWeakKey(key), wk -> mappingFunction.apply(key));
      }

      @Nullable
      V putIfAbsent(final K key, final V value) {
         processQueue();
         return map.putIfAbsent(createWeakKey(key), value);
      }

      boolean isEmpty() {
         processQueue();
         return map.isEmpty();
      }

      @Nullable
      V get(final Object key) {
         processQueue();
         return map.get(createWeakKey((K) key));
      }

      Collection<V> values() {
         processQueue();
         return map.values();
      }

      private void processQueue() {
         Object weakKey;
         while ((weakKey = referenceQueue.poll()) != null) {
            map.remove(weakKey);
         }
      }
   }

   private static final class EnumState<ID> {
      /**
       * Holds all enum types and their values by id
       */
      static final WeakConcurrentMap<Class<? extends ExtensibleEnum<?>>, EnumState<?>> STATES = new WeakConcurrentMap<>(true);

      static <ID> EnumState<ID> get(final Class<? extends ExtensibleEnum<ID>> enumType) {
         return (EnumState<ID>) STATES.computeIfAbsent(enumType, k -> new EnumState<>());
      }

      final AtomicInteger ordinalCounter = new AtomicInteger();
      final WeakConcurrentMap<ID, ExtensibleEnum<ID>> valuesById = new WeakConcurrentMap<>(false);
   }

   private static final Comparator<ExtensibleEnum<?>> ORDINAL_COMPARATOR = (o1, o2) -> o1.ordinal == o2.ordinal ? 0
         : o1.ordinal < o2.ordinal ? -1 : 1;

   public static <@NonNull ID, T extends ExtensibleEnum<ID>> @Nullable T getEnumValue(final Class<T> enumType, final ID id) {
      final EnumState<ID> enumState = EnumState.get(enumType);
      return (T) enumState.valuesById.get(id);
   }

   public static <@NonNull ID, T extends ExtensibleEnum<ID>> List<T> getEnumValues(final Class<T> enumType) {
      final EnumState<ID> enumState = EnumState.get(enumType);
      if (enumState.valuesById.isEmpty()) {
         // ensure the static fields containing the enum values are initialized
         Types.initialize(enumType);

         if (enumState.valuesById.isEmpty())
            return Collections.emptyList();
      }

      final var result = (List<T>) new ArrayList<>(enumState.valuesById.values());
      Collections.sort(result, ORDINAL_COMPARATOR);
      return result;
   }

   /**
    * The enum value's unique identifier in the scope of the enum type. Similar to {@link Enum#name()} but with customizable type.
    */
   public final ID id;

   /**
    * The ordinal of this enumeration value (its position in its enum declaration, where the initial constant is assigned an ordinal of
    * zero).
    */
   public final transient int ordinal;

   protected ExtensibleEnum(final ID id) {
      this.id = id;

      final EnumState<ID> enumState = getEnumState();
      ordinal = enumState.ordinalCounter.getAndIncrement();

      // ensure the id is unique
      final var existingValue = enumState.valuesById.putIfAbsent(id, this);
      if (existingValue != null)
         throw new IllegalStateException("The id of " + this + " is not unique.");
   }

   private EnumState<ID> getEnumState() {
      return EnumState.get((Class<ExtensibleEnum<ID>>) getClass());
   }

   @Override
   public final boolean equals(final @Nullable Object obj) {
      return this == obj;
   }

   /**
    * {@link Enum#name()} record style access to {@link #id}.
    */
   public final ID name() {
      return id;
   }

   /**
    * {@link Enum#ordinal()} record style access to {@link #ordinal}.
    */
   public final int ordinal() {
      return ordinal;
   }

   /**
    * For bean-style access to {@link #id}.
    */
   public final ID getId() {
      return id;
   }

   /**
    * For bean-style access to {@link #ordinal}.
    */
   public final int getOrdinal() {
      return ordinal;
   }

   @Override
   public final int hashCode() {
      return System.identityHashCode(this);
   }

   /**
    * @return the Singleton representation of the enum with the given value
    * @see Serializable
    */
   protected final Object readResolve() {
      final var id = this.id;
      if (id == null)
         throw new IllegalStateException("Field [id] must not be null.");

      final Object obj = getEnumValue(getClass(), id);
      if (obj == null)
         throw new IllegalStateException("Unknown enum value with id [" + id + "]");
      return obj;
   }

   @Override
   public String toString() {
      return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
   }
}
