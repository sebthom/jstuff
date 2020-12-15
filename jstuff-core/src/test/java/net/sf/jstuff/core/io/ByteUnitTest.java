/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import static net.sf.jstuff.core.io.ByteUnit.*;

import java.math.RoundingMode;
import java.util.Locale;

import junit.framework.TestCase;
import net.sf.jstuff.core.math.Rounding;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteUnitTest extends TestCase {

   public void testToBytes() {
      assertEquals(0L, BYTES.toBytes(0).longValue());
      assertEquals(0L, KIBIBYTES.toBytes(0).longValue());
      assertEquals(0L, MEBIBYTES.toBytes(0).longValue());
      assertEquals(0L, GIBIBYTES.toBytes(0).longValue());
      assertEquals(0L, TEBIBYTES.toBytes(0).longValue());

      assertEquals(1L, BYTES.toBytes(1).longValue());
      assertEquals(1L * 1024, KIBIBYTES.toBytes(1).longValue());
      assertEquals(1L * 1024 * 1024, MEBIBYTES.toBytes(1).longValue());
      assertEquals(1L * 1024 * 1024 * 1024, GIBIBYTES.toBytes(1).longValue());
      assertEquals(1L * 1024 * 1024 * 1024 * 1024, TEBIBYTES.toBytes(1).longValue());

      assertEquals(1024L, BYTES.toBytes(1024).longValue());
      assertEquals(1024L * 1024, KIBIBYTES.toBytes(1024).longValue());
      assertEquals(1024L * 1024 * 1024, MEBIBYTES.toBytes(1024).longValue());
      assertEquals(1024L * 1024 * 1024 * 1024, GIBIBYTES.toBytes(1024).longValue());
      assertEquals(1024L * 1024 * 1024 * 1024 * 1024, TEBIBYTES.toBytes(1024).longValue());

      assertEquals(-1L, BYTES.toBytes(-1).longValue());
      assertEquals(-1L * 1024, KIBIBYTES.toBytes(-1).longValue());
      assertEquals(-1L * 1024 * 1024, MEBIBYTES.toBytes(-1).longValue());
      assertEquals(-1L * 1024 * 1024 * 1024, GIBIBYTES.toBytes(-1).longValue());
      assertEquals(-1L * 1024 * 1024 * 1024 * 1024, TEBIBYTES.toBytes(-1).longValue());

      assertEquals(-1024L, BYTES.toBytes(-1024).longValue());
      assertEquals(-1024L * 1024, KIBIBYTES.toBytes(-1024).longValue());
      assertEquals(-1024L * 1024 * 1024, MEBIBYTES.toBytes(-1024).longValue());
      assertEquals(-1024L * 1024 * 1024 * 1024, GIBIBYTES.toBytes(-1024).longValue());
      assertEquals(-1024L * 1024 * 1024 * 1024 * 1024, TEBIBYTES.toBytes(-1024).longValue());
   }

   public void testKiBFrom() {
      assertEquals(0L, KIBIBYTES.of(0, BYTES));
      assertEquals(0L, KIBIBYTES.of(0, KIBIBYTES));
      assertEquals(0L, KIBIBYTES.of(0, MEBIBYTES));
      assertEquals(0L, KIBIBYTES.of(0, GIBIBYTES));
      assertEquals(0L, KIBIBYTES.of(0, TEBIBYTES));

      assertEquals(0L, KIBIBYTES.of(1, BYTES));
      assertEquals(1L, KIBIBYTES.of(1, KIBIBYTES));
      assertEquals(1L * 1024, KIBIBYTES.of(1, MEBIBYTES));
      assertEquals(1L * 1024 * 1024, KIBIBYTES.of(1, GIBIBYTES));
      assertEquals(1L * 1024 * 1024 * 1024, KIBIBYTES.of(1, TEBIBYTES));

      assertEquals(1L, KIBIBYTES.of(1024, BYTES));
      assertEquals(1024L, KIBIBYTES.of(1024, KIBIBYTES));
      assertEquals(1024L * 1024, KIBIBYTES.of(1024, MEBIBYTES));
      assertEquals(1024L * 1024 * 1024, KIBIBYTES.of(1024, GIBIBYTES));
      assertEquals(1024L * 1024 * 1024 * 1024, KIBIBYTES.of(1024, TEBIBYTES));

      assertEquals(0L, KIBIBYTES.of(-1, BYTES));
      assertEquals(-1L, KIBIBYTES.of(-1, KIBIBYTES));
      assertEquals(-1L * 1024, KIBIBYTES.of(-1, MEBIBYTES));
      assertEquals(-1L * 1024 * 1024, KIBIBYTES.of(-1, GIBIBYTES));
      assertEquals(-1L * 1024 * 1024 * 1024, KIBIBYTES.of(-1, TEBIBYTES));

      assertEquals(-1L, KIBIBYTES.of(-1024, BYTES));
      assertEquals(-1024L, KIBIBYTES.of(-1024, KIBIBYTES));
      assertEquals(-1024L * 1024, KIBIBYTES.of(-1024, MEBIBYTES));
      assertEquals(-1024L * 1024 * 1024, KIBIBYTES.of(-1024, GIBIBYTES));
      assertEquals(-1024L * 1024 * 1024 * 1024, KIBIBYTES.of(-1024, TEBIBYTES));
   }

   public void testToHumanReadableString() {
      final Rounding rounding = new Rounding(2, RoundingMode.HALF_UP);
      assertEquals(/*   */ "999 B", BYTES.toHumanReadableString(999, rounding, Locale.US));
      assertEquals(/* */ "999 KiB", BYTES.toHumanReadableString(999 * 1024, rounding, Locale.US));
      assertEquals(/* */ "999 MiB", BYTES.toHumanReadableString(999 * 1024 * 1024, rounding, Locale.US));

      assertEquals(/**/ "0.98 KiB", BYTES.toHumanReadableString(1000, rounding, Locale.US));
      assertEquals(/**/ "0.98 MiB", BYTES.toHumanReadableString(1000 * 1024, rounding, Locale.US));
      assertEquals(/**/ "0.98 GiB", BYTES.toHumanReadableString(1000 * 1024 * 1024, rounding, Locale.US));

      assertEquals(/*   */ "1 GiB", KIBIBYTES.toHumanReadableString(1 * 1024 * 1024, rounding, Locale.US));
      assertEquals(/* */ "999 GiB", KIBIBYTES.toHumanReadableString(999 * 1024 * 1024, rounding, Locale.US));
      assertEquals(/**/ "0.98 TiB", KIBIBYTES.toHumanReadableString(1000 * 1024 * 1024, rounding, Locale.US));

      assertEquals(/*   */ "1 TiB", MEBIBYTES.toHumanReadableString(1 * 1024 * 1024, rounding, Locale.US));
      assertEquals(/* */ "999 TiB", MEBIBYTES.toHumanReadableString(999 * 1024 * 1024, rounding, Locale.US));
      assertEquals(/**/ "0.98 PiB", MEBIBYTES.toHumanReadableString(1000 * 1024 * 1024, rounding, Locale.US));
   }
}
