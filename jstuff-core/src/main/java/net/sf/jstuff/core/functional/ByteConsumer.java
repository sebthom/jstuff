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
public interface ByteConsumer extends Consumer<Byte> {

   void accept(byte b);

   @Override
   default void accept(final Byte b) {
      accept(b.byteValue());
   }

   default ByteConsumer andThen(final ByteConsumer next) {
      Args.notNull("next", next);

      return ch -> {
         accept(ch);
         next.accept(ch);
      };
   }
}
