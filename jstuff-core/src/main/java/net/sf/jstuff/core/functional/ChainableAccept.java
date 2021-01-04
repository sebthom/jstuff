/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import net.sf.jstuff.core.functional.Accepts.And;
import net.sf.jstuff.core.functional.Accepts.Or;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface ChainableAccept<T> extends Accept<T> {

   default <V extends T> ChainableAccept<V> and(final Accept<? super V> next) {
      Args.notNull("next", next);

      return new And<>(ChainableAccept.this, next);
   }

   default <V extends T> ChainableAccept<V> or(final Accept<? super V> next) {
      Args.notNull("next", next);

      return new Or<>(ChainableAccept.this, next);
   }
}
