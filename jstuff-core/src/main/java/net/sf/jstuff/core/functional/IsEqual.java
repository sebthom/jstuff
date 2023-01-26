/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface IsEqual<T> {

   IsEqual<Object> DEFAULT = Objects::equals;

   boolean isEqual(@Nullable T obj1, @Nullable T obj2);
}
