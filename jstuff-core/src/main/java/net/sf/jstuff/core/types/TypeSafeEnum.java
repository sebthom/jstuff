/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
public abstract class TypeSafeEnum<VALUE_TYPE> implements Serializable
{
    private static final Comparator<TypeSafeEnum<?>> ORDINAL_COMPARATOR = new Comparator<TypeSafeEnum<?>>() {
        public int compare(final TypeSafeEnum<?> o1, final TypeSafeEnum<?> o2) {
            return o1.ordinal == o2.ordinal ? 0 : o1.ordinal < o2.ordinal ? -1 : 1;
        }
    };

    private static final long serialVersionUID = 1L;

    /**
     * Map<EnumClass,Map<ObjectValue,EnumValue>>
     */
    private static final ConcurrentMap<Class<? extends TypeSafeEnum<?>>, ConcurrentMap<?, ? extends TypeSafeEnum<?>>> ENUMS_BY_TYPE = new ConcurrentHashMap<Class<? extends TypeSafeEnum<?>>, ConcurrentMap<?, ? extends TypeSafeEnum<?>>>();

    private static final AtomicInteger ORDINAL_COUNTER = new AtomicInteger();;

    public static <V, T extends TypeSafeEnum<V>> T getItem(final Class<T> enumType, final V value) {
        @SuppressWarnings("unchecked")
        final Map<V, T> enumsByValue = (Map<V, T>) ENUMS_BY_TYPE.get(enumType);
        if (enumsByValue == null)
            return null;
        return enumsByValue.get(value);
    }

    @SuppressWarnings("unchecked")
    public static <V, T extends TypeSafeEnum<V>> List<T> getItems(final Class<T> enumType) {
        Map<V, T> enumsByValue = (Map<V, T>) ENUMS_BY_TYPE.get(enumType);
        if (enumsByValue == null) {
            // ensure the static fields containing the enum items are initialized
            Types.initialize(enumType);

            enumsByValue = (Map<V, T>) ENUMS_BY_TYPE.get(enumType);
            if (enumsByValue == null)
                return Collections.emptyList();
        }

        final List<T> result = new ArrayList<T>(enumsByValue.values());
        Collections.sort(result, ORDINAL_COMPARATOR);
        return result;
    }

    /**
     * the enum's value.
     */
    protected final VALUE_TYPE value;

    /**
     * the enum's JVM instance unique ordinal.
     */
    protected final transient int ordinal;

    protected TypeSafeEnum(final VALUE_TYPE value) {
        this.value = value;
        ordinal = ORDINAL_COUNTER.getAndIncrement();
        registerEnum();
    }

    @Override
    public final boolean equals(final Object obj) {
        return this == obj;
    }

    /**
     * The ordinal is unique in this JVM instance across all instances of sub classes of TypeSafeEnum.
     * It can be used in switch statements.
     *
     * @return the ordinal
     */
    public final int getOrdinal() {
        return ordinal;
    }

    /**
     * Gets the enum's value.
     *
     * @return the value of the enum
     */
    public final VALUE_TYPE getValue() {
        return value;
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
        if (value == null)
            throw new IllegalStateException("Field [value] must not be null.");
        @SuppressWarnings("unchecked")
        final Object obj = getItem(getClass(), value);
        if (obj == null)
            throw new IllegalStateException("Unknown enum with value=" + value);
        return obj;
    }

    /**
     * registers this instance in the global map.
     */
    private void registerEnum() {
        ConcurrentMap<VALUE_TYPE, TypeSafeEnum<?>> enumItems = new ConcurrentHashMap<VALUE_TYPE, TypeSafeEnum<?>>(2);
        @SuppressWarnings("unchecked")
        final ConcurrentMap<VALUE_TYPE, TypeSafeEnum<?>> existingEnumItems = (ConcurrentMap<VALUE_TYPE, TypeSafeEnum<?>>) ENUMS_BY_TYPE.putIfAbsent(
            (Class<? extends TypeSafeEnum<?>>) getClass(), enumItems);
        if (existingEnumItems != null) {
            enumItems = existingEnumItems;
        }

        final TypeSafeEnum<?> existingItem = enumItems.putIfAbsent(getValue(), this);
        if (existingItem != null)
            throw new IllegalStateException("The value of " + this + " is not unique.");
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
