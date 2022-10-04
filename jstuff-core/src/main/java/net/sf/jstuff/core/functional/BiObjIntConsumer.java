/*
 * Copyright 2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface BiObjIntConsumer<T, U> {

   void accept(int index, T first, U second);

   default BiObjIntConsumer<T, U> andThen(final BiObjIntConsumer<T, U> next) {
      Args.notNull("next", next);

      return (index, first, second) -> {
         accept(index, first, second);
         next.accept(index, first, second);
      };
   }
}
