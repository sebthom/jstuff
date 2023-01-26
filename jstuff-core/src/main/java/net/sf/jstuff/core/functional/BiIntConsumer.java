/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface BiIntConsumer {

   void accept(int a, int b);

   default BiIntConsumer andThen(final BiIntConsumer next) {
      Args.notNull("next", next);

      return (a, b) -> {
         accept(a, b);
         next.accept(a, b);
      };
   }
}
