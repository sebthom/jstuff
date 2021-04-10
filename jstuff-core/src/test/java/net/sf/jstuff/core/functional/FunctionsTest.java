/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import static net.sf.jstuff.core.functional.Functions.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class FunctionsTest {

   @Test
   public void testConverts() {
      final Function<Object, Integer> t1 = objectToString()//
         .and(stringToInt())//
         .and(castTo(Number.class))//
         .and(objectToString())//
         .and(trim()) //
         .and(stringToInt());

      assertThat(t1.apply(null)).isNull();
      assertThat(t1.apply("1")).isEqualTo(Integer.valueOf(1));
   }
}
