/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.util.Objects;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@FunctionalInterface
public interface LongBiConsumer {

   void accept(long a, long b);

   default LongBiConsumer andThen(final LongBiConsumer after) {
      Objects.requireNonNull(after);

      return (a, b) -> {
         accept(a, b);
         after.accept(a, b);
      };
   }
}
