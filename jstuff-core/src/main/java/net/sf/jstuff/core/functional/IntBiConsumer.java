/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.Objects;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface IntBiConsumer {

   void accept(int a, int b);

   default IntBiConsumer andThen(final IntBiConsumer after) {
      Objects.requireNonNull(after);

      return (a, b) -> {
         accept(a, b);
         after.accept(a, b);
      };
   }
}
