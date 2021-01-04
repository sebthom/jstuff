/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import net.sf.jstuff.core.functional.Functions.And;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface ChainableFunction<In, Out> extends Function<In, Out> {

   default <NextOut> ChainableFunction<In, NextOut> and(final Function<? super Out, NextOut> next) {
      return new And<>(this, next);
   }

}
