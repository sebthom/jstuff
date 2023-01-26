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
public interface TriConsumer<A, B, C> {

   void accept(A a, B b, C c);

   default TriConsumer<A, B, C> andThen(final TriConsumer<? super A, ? super B, ? super C> next) {
      Args.notNull("next", next);

      return (a, b, c) -> {
         accept(a, b, c);
         next.accept(a, b, c);
      };
   }
}
