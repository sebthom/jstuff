/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.math;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigInteger;
import java.util.UUID;

import org.junit.Test;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumbersTest {

   @Test
   public void testIsLong() {
      assertThat(Numbers.isLong(BigInteger.ZERO)).isTrue();
      assertThat(Numbers.isLong(BigInteger.TEN)).isTrue();
      assertThat(Numbers.isLong(BigInteger.valueOf(Long.MAX_VALUE))).isTrue();
      assertThat(Numbers.isLong(BigInteger.valueOf(Long.MIN_VALUE))).isTrue();
      assertThat(Numbers.isLong(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.valueOf(1)))).isFalse();
      assertThat(Numbers.isLong(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.valueOf(1)))).isFalse();
   }

   @Test
   public void testUuidToBigInteger() {
      final UUID uuid = UUID.randomUUID();
      assertThat(new BigInteger(uuid.toString().replaceAll("-", ""), 16)).isEqualTo(Numbers.toBigInteger(uuid));
   }
}
