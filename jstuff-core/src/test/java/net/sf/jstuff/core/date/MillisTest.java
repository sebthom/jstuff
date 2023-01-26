/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.date;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class MillisTest extends TestCase {

   @Test
   public void testMillis() {
      assertThat(Millis.fromHours(24)).isEqualTo(Millis.fromDays(1));
      assertThat(Millis.fromMinutes(60)).isEqualTo(Millis.fromHours(1));
      assertThat(Millis.fromSeconds(60)).isEqualTo(Millis.fromMinutes(1));
      assertThat(Millis.fromSeconds(1)).isEqualTo(1_000);
   }
}
