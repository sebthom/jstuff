/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.math;

import static net.sf.jstuff.core.math.NumericalSystem.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigInteger;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumericalSystemTest {
   @Test
   public void testBase16Decode() {
      assertThat(BASE16.decodeAsLong("0")).isZero();
      assertThat(BASE16.decodeAsLong("1")).isEqualTo(1);
      assertThat(BASE16.decodeAsLong(BASE16.encode(123_456_789))).isEqualTo(123_456_789);
      assertThat(BASE16.decodeAsLong(BASE16.encode(Long.MAX_VALUE))).isEqualTo(Long.MAX_VALUE);

      assertThat(BASE16.decodeAsBigInteger("0").longValue()).isZero();
      assertThat(BASE16.decodeAsBigInteger("1").longValue()).isEqualTo(1);
      assertThat(BASE16.decodeAsBigInteger(BASE16.encode(123_456_789)).longValue()).isEqualTo(123_456_789);
      assertThat(BASE16.decodeAsBigInteger(BASE16.encode(Long.MAX_VALUE)).longValue()).isEqualTo(Long.MAX_VALUE);
   }

   @Test
   public void testBase36Decode() {
      assertThat(BASE36.decodeAsLong("0")).isZero();
      assertThat(BASE36.decodeAsLong("1")).isEqualTo(1);
      assertThat(BASE36.decodeAsLong(BASE36.encode(123_456_789))).isEqualTo(123_456_789);
      assertThat(BASE36.decodeAsLong(BASE36.encode(Long.MAX_VALUE))).isEqualTo(Long.MAX_VALUE);

      assertThat(BASE36.decodeAsBigInteger("0").longValue()).isZero();
      assertThat(BASE36.decodeAsBigInteger("1").longValue()).isEqualTo(1);
      assertThat(BASE36.decodeAsBigInteger(BASE36.encode(123_456_789)).longValue()).isEqualTo(123_456_789);
      assertThat(BASE36.decodeAsBigInteger(BASE36.encode(Long.MAX_VALUE)).longValue()).isEqualTo(Long.MAX_VALUE);
   }

   @Test
   public void testBase62Decode() {
      assertThat(BASE62.decodeAsLong("0")).isZero();
      assertThat(BASE62.decodeAsLong("1")).isEqualTo(1);
      assertThat(BASE62.decodeAsLong(BASE62.encode(123_456_789))).isEqualTo(123_456_789);
      assertThat(BASE62.decodeAsLong(BASE62.encode(Long.MAX_VALUE))).isEqualTo(Long.MAX_VALUE);

      assertThat(BASE62.decodeAsBigInteger("0").longValue()).isZero();
      assertThat(BASE62.decodeAsBigInteger("1").longValue()).isEqualTo(1);
      assertThat(BASE62.decodeAsBigInteger(BASE62.encode(123_456_789)).longValue()).isEqualTo(123_456_789);
      assertThat(BASE62.decodeAsBigInteger(BASE62.encode(Long.MAX_VALUE)).longValue()).isEqualTo(Long.MAX_VALUE);
   }

   @Test
   public void testToBase16Encode() {
      assertThat(BASE16.encode(0)).isEqualTo("0");
      assertThat(BASE16.encode(1)).isEqualTo("1");
      assertThat(BASE16.encode(15)).isEqualTo("f");
      assertThat(BASE16.encode(35)).isEqualTo("23");
      assertThat(BASE16.encode(36)).isEqualTo("24");
      assertThat(BASE16.encode(61)).isEqualTo("3d");

      assertThat(BASE16.encode(BigInteger.valueOf(0))).isEqualTo("0");
      assertThat(BASE16.encode(BigInteger.valueOf(1))).isEqualTo("1");
      assertThat(BASE16.encode(BigInteger.valueOf(15))).isEqualTo("f");
      assertThat(BASE16.encode(BigInteger.valueOf(35))).isEqualTo("23");
      assertThat(BASE16.encode(BigInteger.valueOf(36))).isEqualTo("24");
      assertThat(BASE16.encode(BigInteger.valueOf(61))).isEqualTo("3d");

      assertThat(BASE16.encode(123_456_789)).isEqualTo(BigInteger.valueOf(123_456_789).toString(16));
   }

   @Test
   public void testToBase36Encode() {
      assertThat(BASE36.encode(0)).isEqualTo("0");
      assertThat(BASE36.encode(1)).isEqualTo("1");
      assertThat(BASE36.encode(15)).isEqualTo("f");
      assertThat(BASE36.encode(35)).isEqualTo("z");
      assertThat(BASE36.encode(36)).isEqualTo("10");
      assertThat(BASE36.encode(61)).isEqualTo("1p");

      assertThat(BASE36.encode(BigInteger.valueOf(0))).isEqualTo("0");
      assertThat(BASE36.encode(BigInteger.valueOf(1))).isEqualTo("1");
      assertThat(BASE36.encode(BigInteger.valueOf(15))).isEqualTo("f");
      assertThat(BASE36.encode(BigInteger.valueOf(35))).isEqualTo("z");
      assertThat(BASE36.encode(BigInteger.valueOf(36))).isEqualTo("10");
      assertThat(BASE36.encode(BigInteger.valueOf(61))).isEqualTo("1p");

      assertThat(BASE36.encode(123_456_789)).isEqualTo(BigInteger.valueOf(123_456_789).toString(36));
   }

   @Test
   public void testToBase62Encode() {
      assertThat(BASE62.encode(0)).isEqualTo("0");
      assertThat(BASE62.encode(1)).isEqualTo("1");
      assertThat(BASE62.encode(15)).isEqualTo("f");
      assertThat(BASE62.encode(35)).isEqualTo("z");
      assertThat(BASE62.encode(36)).isEqualTo("A");
      assertThat(BASE62.encode(61)).isEqualTo("Z");

      assertThat(BASE62.encode(BigInteger.valueOf(0))).isEqualTo("0");
      assertThat(BASE62.encode(BigInteger.valueOf(1))).isEqualTo("1");
      assertThat(BASE62.encode(BigInteger.valueOf(15))).isEqualTo("f");
      assertThat(BASE62.encode(BigInteger.valueOf(35))).isEqualTo("z");
      assertThat(BASE62.encode(BigInteger.valueOf(36))).isEqualTo("A");
      assertThat(BASE62.encode(BigInteger.valueOf(61))).isEqualTo("Z");

      // Max Radix for BigInteger is 36, thus the following test would fail
      // assertThat(BASE36.encode(123_456_789)).isEqualTo(BigInteger.valueOf(123_456_789).toString(62));
   }

   @Test
   public void testToBase62EncodeBigIntPerf() {

      final BigInteger number = BigInteger.valueOf(Long.MAX_VALUE).multiply(BigInteger.valueOf(Long.MAX_VALUE));

      assertThat(NumericalSystem.BASE62.encode_slow(number)).isEqualTo(NumericalSystem.BASE62.encode(number));

      final int iterations = 1_000_000;
      final StopWatch sw = new StopWatch();
      sw.start();
      for (int i = 0; i < iterations; i++) {
         NumericalSystem.BASE62.encode(number);
      }
      sw.stop();
      System.out.println(sw);
      sw.reset();
      sw.start();
      for (int i = 0; i < iterations; i++) {
         NumericalSystem.BASE62.encode_slow(number);
      }
      sw.stop();
      System.out.println(sw);
   }
}
