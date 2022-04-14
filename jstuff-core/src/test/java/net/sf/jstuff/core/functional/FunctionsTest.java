/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Functions.*;
import static org.assertj.core.api.Assertions.*;

import java.util.function.Function;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FunctionsTest {

   @Test
   public void testConverts() {
      final Function<Object, Integer> t1 = objectToString()//
         .andThen(stringToInt())//
         .andThen(castTo(Number.class))//
         .andThen(objectToString())//
         .andThen(trim()) //
         .andThen(stringToInt());

      assertThat(t1.apply(null)).isNull();
      assertThat(t1.apply("1")).isEqualTo(Integer.valueOf(1));
   }
}
