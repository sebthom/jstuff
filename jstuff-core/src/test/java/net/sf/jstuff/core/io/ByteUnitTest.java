/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static net.sf.jstuff.core.io.ByteUnit.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.RoundingMode;
import java.util.Locale;

import org.junit.Test;

import net.sf.jstuff.core.math.Rounding;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteUnitTest {

   @Test
   public void testToBytes() {
      assertThat(BYTES.toBytes(0).longValue()).isZero();
      assertThat(KIBIBYTES.toBytes(0).longValue()).isZero();
      assertThat(MEBIBYTES.toBytes(0).longValue()).isZero();
      assertThat(GIBIBYTES.toBytes(0).longValue()).isZero();
      assertThat(TEBIBYTES.toBytes(0).longValue()).isZero();

      assertThat(BYTES.toBytes(1).longValue()).isEqualTo(1L);
      assertThat(KIBIBYTES.toBytes(1).longValue()).isEqualTo(1L * 1024);
      assertThat(MEBIBYTES.toBytes(1).longValue()).isEqualTo(1L * 1024 * 1024);
      assertThat(GIBIBYTES.toBytes(1).longValue()).isEqualTo(1L * 1024 * 1024 * 1024);
      assertThat(TEBIBYTES.toBytes(1).longValue()).isEqualTo(1L * 1024 * 1024 * 1024 * 1024);

      assertThat(BYTES.toBytes(1024).longValue()).isEqualTo(1024L);
      assertThat(KIBIBYTES.toBytes(1024).longValue()).isEqualTo(1024L * 1024);
      assertThat(MEBIBYTES.toBytes(1024).longValue()).isEqualTo(1024L * 1024 * 1024);
      assertThat(GIBIBYTES.toBytes(1024).longValue()).isEqualTo(1024L * 1024 * 1024 * 1024);
      assertThat(TEBIBYTES.toBytes(1024).longValue()).isEqualTo(1024L * 1024 * 1024 * 1024 * 1024);

      assertThat(BYTES.toBytes(-1).longValue()).isEqualTo(-1L);
      assertThat(KIBIBYTES.toBytes(-1).longValue()).isEqualTo(-1L * 1024);
      assertThat(MEBIBYTES.toBytes(-1).longValue()).isEqualTo(-1L * 1024 * 1024);
      assertThat(GIBIBYTES.toBytes(-1).longValue()).isEqualTo(-1L * 1024 * 1024 * 1024);
      assertThat(TEBIBYTES.toBytes(-1).longValue()).isEqualTo(-1L * 1024 * 1024 * 1024 * 1024);

      assertThat(BYTES.toBytes(-1024).longValue()).isEqualTo(-1024L);
      assertThat(KIBIBYTES.toBytes(-1024).longValue()).isEqualTo(-1024L * 1024);
      assertThat(MEBIBYTES.toBytes(-1024).longValue()).isEqualTo(-1024L * 1024 * 1024);
      assertThat(GIBIBYTES.toBytes(-1024).longValue()).isEqualTo(-1024L * 1024 * 1024 * 1024);
      assertThat(TEBIBYTES.toBytes(-1024).longValue()).isEqualTo(-1024L * 1024 * 1024 * 1024 * 1024);
   }

   @Test
   public void testKiBFrom() {
      assertThat(KIBIBYTES.of(0, BYTES)).isZero();
      assertThat(KIBIBYTES.of(0, KIBIBYTES)).isZero();
      assertThat(KIBIBYTES.of(0, MEBIBYTES)).isZero();
      assertThat(KIBIBYTES.of(0, GIBIBYTES)).isZero();
      assertThat(KIBIBYTES.of(0, TEBIBYTES)).isZero();

      assertThat(KIBIBYTES.of(1, BYTES)).isZero();
      assertThat(KIBIBYTES.of(1, KIBIBYTES)).isEqualTo(1L);
      assertThat(KIBIBYTES.of(1, MEBIBYTES)).isEqualTo(1L * 1024);
      assertThat(KIBIBYTES.of(1, GIBIBYTES)).isEqualTo(1L * 1024 * 1024);
      assertThat(KIBIBYTES.of(1, TEBIBYTES)).isEqualTo(1L * 1024 * 1024 * 1024);

      assertThat(KIBIBYTES.of(1024, BYTES)).isEqualTo(1L);
      assertThat(KIBIBYTES.of(1024, KIBIBYTES)).isEqualTo(1024L);
      assertThat(KIBIBYTES.of(1024, MEBIBYTES)).isEqualTo(1024L * 1024);
      assertThat(KIBIBYTES.of(1024, GIBIBYTES)).isEqualTo(1024L * 1024 * 1024);
      assertThat(KIBIBYTES.of(1024, TEBIBYTES)).isEqualTo(1024L * 1024 * 1024 * 1024);

      assertThat(KIBIBYTES.of(-1, BYTES)).isZero();
      assertThat(KIBIBYTES.of(-1, KIBIBYTES)).isEqualTo(-1L);
      assertThat(KIBIBYTES.of(-1, MEBIBYTES)).isEqualTo(-1L * 1024);
      assertThat(KIBIBYTES.of(-1, GIBIBYTES)).isEqualTo(-1L * 1024 * 1024);
      assertThat(KIBIBYTES.of(-1, TEBIBYTES)).isEqualTo(-1L * 1024 * 1024 * 1024);

      assertThat(KIBIBYTES.of(-1024, BYTES)).isEqualTo(-1L);
      assertThat(KIBIBYTES.of(-1024, KIBIBYTES)).isEqualTo(-1024L);
      assertThat(KIBIBYTES.of(-1024, MEBIBYTES)).isEqualTo(-1024L * 1024);
      assertThat(KIBIBYTES.of(-1024, GIBIBYTES)).isEqualTo(-1024L * 1024 * 1024);
      assertThat(KIBIBYTES.of(-1024, TEBIBYTES)).isEqualTo(-1024L * 1024 * 1024 * 1024);

   }

   @Test
   public void testToHumanReadableString() {
      final var rounding = new Rounding(2, RoundingMode.HALF_UP);
      assertThat(BYTES.toHumanReadableString(999, rounding, Locale.US)).isEqualTo("999 B");
      assertThat(BYTES.toHumanReadableString(999 * 1024, rounding, Locale.US)).isEqualTo("999 KiB");
      assertThat(BYTES.toHumanReadableString(999 * 1024 * 1024, rounding, Locale.US)).isEqualTo("999 MiB");

      assertThat(BYTES.toHumanReadableString(1000, rounding, Locale.US)).isEqualTo("0.98 KiB");
      assertThat(BYTES.toHumanReadableString(1000 * 1024, rounding, Locale.US)).isEqualTo("0.98 MiB");
      assertThat(BYTES.toHumanReadableString(1000 * 1024 * 1024, rounding, Locale.US)).isEqualTo("0.98 GiB");

      assertThat(KIBIBYTES.toHumanReadableString(1 * 1024 * 1024, rounding, Locale.US)).isEqualTo("1 GiB");
      assertThat(KIBIBYTES.toHumanReadableString(999 * 1024 * 1024, rounding, Locale.US)).isEqualTo("999 GiB");
      assertThat(KIBIBYTES.toHumanReadableString(1000 * 1024 * 1024, rounding, Locale.US)).isEqualTo("0.98 TiB");

      assertThat(MEBIBYTES.toHumanReadableString(1 * 1024 * 1024, rounding, Locale.US)).isEqualTo("1 TiB");
      assertThat(MEBIBYTES.toHumanReadableString(999 * 1024 * 1024, rounding, Locale.US)).isEqualTo("999 TiB");
      assertThat(MEBIBYTES.toHumanReadableString(1000 * 1024 * 1024, rounding, Locale.US)).isEqualTo("0.98 PiB");
   }
}
