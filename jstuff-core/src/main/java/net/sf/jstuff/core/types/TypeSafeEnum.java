/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.sf.jstuff.core.reflection.Types;

/**
 * An implementation of the type safe enum pattern described in Effective Java by Joshua Block.
 *
 * <p>
 * It allows to compare enumeration objects by reference (==), rather than with .equals(), even if the
 * enums were serialized and deserialized.
 * </p>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class TypeSafeEnum<ID> implements Serializable {
   private static final Comparator<TypeSafeEnum<?>> ORDINAL_COMPARATOR = new Comparator<TypeSafeEnum<?>>() {
      public int compare(final TypeSafeEnum<?> o1, final TypeSafeEnum<?> o2) {
         return o1.ordinal == o2.ordinal ? 0 : o1.ordinal < o2.ordinal ? -1 : 1;
      }
   };

   private static final long serialVersionUID = 1L;

   private static final ConcurrentMap<Class<? extends TypeSafeEnum<?>>, ConcurrentMap<?, ? extends TypeSafeEnum<?>>> ENUMS_BY_TYPE = //
      new ConcurrentHashMap<Class<? extends TypeSafeEnum<?>>, ConcurrentMap<?, ? extends TypeSafeEnum<?>>>();

   private static final AtomicInteger ORDINAL_COUNTER = new AtomicInteger();

   public static <V, T extends TypeSafeEnum<V>> T getEnum(final Class<T> enumType, final V id) {
      @SuppressWarnings("unchecked")
      final Map<V, T> enumsById = (Map<V, T>) ENUMS_BY_TYPE.get(enumType);
      if (enumsById == null)
         return null;
      return enumsById.get(id);
   }

   @SuppressWarnings("unchecked")
   public static <ID, T extends TypeSafeEnum<ID>> List<T> getEnums(final Class<T> enumType) {
      Map<ID, T> enumsById = (Map<ID, T>) ENUMS_BY_TYPE.get(enumType);
      if (enumsById == null) {
         // ensure the static fields containing the enum items are initialized
         Types.initialize(enumType);

         enumsById = (Map<ID, T>) ENUMS_BY_TYPE.get(enumType);
         if (enumsById == null)
            return Collections.emptyList();
      }

      final List<T> result = new ArrayList<T>(enumsById.values());
      Collections.sort(result, ORDINAL_COMPARATOR);
      return result;
   }

   /**
    * the enum's identifier.
    */
   public final ID id;

   /**
    * The ordinal is unique in this JVM instance across all instances of sub classes of TypeSafeEnum.
    * It can be used in switch statements.
    */
   public final transient int ordinal;

   protected TypeSafeEnum(final ID id) {
      this.id = id;
      ordinal = ORDINAL_COUNTER.getAndIncrement();
      registerEnum();
   }

   @Override
   public final boolean equals(final Object obj) {
      return this == obj;
   }

   /**
    *
    * For bean-style access.
    */
   public final ID getId() {
      return id;
   }

   /**
    *
    * For bean-style access.
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
      if (id == null)
         throw new IllegalStateException("Field [id] must not be null.");
      @SuppressWarnings("unchecked")
      final Object obj = getEnum(getClass(), id);
      if (obj == null)
         throw new IllegalStateException("Unknown enum with id=" + id);
      return obj;
   }

   /**
    * registers this instance in the global map.
    */
   private void registerEnum() {
      ConcurrentMap<ID, TypeSafeEnum<?>> enumItems = new ConcurrentHashMap<ID, TypeSafeEnum<?>>(2);
      @SuppressWarnings("unchecked")
      final ConcurrentMap<ID, TypeSafeEnum<?>> existingEnumItems = (ConcurrentMap<ID, TypeSafeEnum<?>>) ENUMS_BY_TYPE.putIfAbsent(
         (Class<? extends TypeSafeEnum<?>>) getClass(), enumItems);
      if (existingEnumItems != null) {
         enumItems = existingEnumItems;
      }

      final TypeSafeEnum<?> existingItem = enumItems.putIfAbsent(id, this);
      if (existingItem != null)
         throw new IllegalStateException("The value of " + this + " is not unique.");
   }

   @Override
   public String toString() {
      return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
   }
}
