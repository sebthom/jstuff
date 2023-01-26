/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.function.Predicate;

import net.sf.jstuff.core.functional.Predicates.And;
import net.sf.jstuff.core.functional.Predicates.Or;
import net.sf.jstuff.core.validation.Args;

/**
 * Predicate with better fluent and/or chaining functions.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface Predicate2<T> extends Predicate<T> {

   default <V extends T> Predicate2<V> and2(final Predicate<? super V> other) {
      Args.notNull("other", other);

      return new And<>(Predicate2.this, other);
   }

   default <V extends T> Predicate2<V> or2(final Predicate<? super V> other) {
      Args.notNull("other", other);

      return new Or<>(Predicate2.this, other);
   }
}
