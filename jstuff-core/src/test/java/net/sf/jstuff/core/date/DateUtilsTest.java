/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.date;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
class DateUtilsTest {

   @Test
   void testParseDuration() throws ParseException {
      assertThat(Dates.parseDuration("1ms")).isEqualTo(1);
      assertThat(Dates.parseDuration("1s")).isEqualTo(1000);
      assertThat(Dates.parseDuration(" 1sec ")).isEqualTo(1000);
      assertThat(Dates.parseDuration("1m")).isEqualTo(1000 * 60);
      assertThat(Dates.parseDuration(" 1min ")).isEqualTo(1000 * 60);
      assertThat(Dates.parseDuration("1h")).isEqualTo(1000 * 60 * 60);
      assertThat(Dates.parseDuration(" 1hour ")).isEqualTo(1000 * 60 * 60);
      assertThat(Dates.parseDuration("1d")).isEqualTo(1000 * 60 * 60 * 24);
      assertThat(Dates.parseDuration(" 1day ")).isEqualTo(1000 * 60 * 60 * 24);
      assertThat(Dates.parseDuration("5d 4h 3m 2s 2ms")).isEqualTo(446_582_002);
   }
}
