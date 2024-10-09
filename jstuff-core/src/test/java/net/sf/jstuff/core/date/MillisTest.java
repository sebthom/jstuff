/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.date;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class MillisTest {

   @Test
   void testMillis() {
      assertThat(Millis.fromHours(24)).isEqualTo(Millis.fromDays(1));
      assertThat(Millis.fromMinutes(60)).isEqualTo(Millis.fromHours(1));
      assertThat(Millis.fromSeconds(60)).isEqualTo(Millis.fromMinutes(1));
      assertThat(Millis.fromSeconds(1)).isEqualTo(1_000);
   }
}
