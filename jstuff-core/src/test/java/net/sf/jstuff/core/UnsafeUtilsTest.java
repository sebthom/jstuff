/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnsafeUtilsTest {

   @Test
   public void testAddressOf() {
      final Object obj1 = new Object();
      assertThat(UnsafeUtils.addressOf(obj1)).isPositive();
      assertThat(UnsafeUtils.addressOf(obj1)).isEqualTo(UnsafeUtils.addressOf(obj1));

      final Object obj2 = new Object();
      assertThat(UnsafeUtils.addressOf(obj2)).isPositive();
      assertThat(UnsafeUtils.addressOf(obj1)).isNotEqualTo(UnsafeUtils.addressOf(obj2));
   }
}
