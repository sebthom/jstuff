/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
