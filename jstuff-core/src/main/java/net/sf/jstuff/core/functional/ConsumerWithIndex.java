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
public interface ConsumerWithIndex<T> {

   void accept(int index, T value);

   default ConsumerWithIndex<T> andThen(final ConsumerWithIndex<T> after) {
      Objects.requireNonNull(after);

      return (index, value) -> {
         accept(index, value);
         after.accept(index, value);
      };
   }
}
