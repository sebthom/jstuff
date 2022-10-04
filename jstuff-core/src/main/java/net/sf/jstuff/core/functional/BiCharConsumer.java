/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface BiCharConsumer {

   void accept(char a, char b);

   default BiCharConsumer andThen(final BiCharConsumer next) {
      Args.notNull("next", next);

      return (a, b) -> {
         accept(a, b);
         next.accept(a, b);
      };
   }
}
