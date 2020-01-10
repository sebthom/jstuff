/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.functional;

import net.sf.jstuff.core.functional.Accepts.And;
import net.sf.jstuff.core.functional.Accepts.Or;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
