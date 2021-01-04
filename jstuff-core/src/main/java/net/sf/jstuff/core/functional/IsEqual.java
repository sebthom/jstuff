/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.Objects;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface IsEqual<T> {

   IsEqual<Object> DEFAULT = Objects::equals;

   boolean isEqual(T obj1, T obj2);
}
