/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Functions.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class FunctionsTest {

   @Test
   void testConverts() {
      final var t1 = objectToString() //
         .andThen(trim()) //
         .andThen(stringToInt()) //
         .andThen(castTo(Number.class)) //
         .andThen(objectToString()) //
         .andThen(stringToInt());

      assertThat(t1.apply(null)).isNull();
      assertThat(t1.apply(" 1 ")).isEqualTo(Integer.valueOf(1));
   }
}
