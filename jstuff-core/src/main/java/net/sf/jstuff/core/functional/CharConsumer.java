/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.function.Consumer;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface CharConsumer extends Consumer<Character> {

   void accept(char ch);

   @Override
   default void accept(final Character c) {
      accept(c.charValue());
   }

   default CharConsumer andThen(final CharConsumer next) {
      Args.notNull("next", next);

      return ch -> {
         accept(ch);
         next.accept(ch);
      };
   }
}
