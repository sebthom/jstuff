/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.date;

import static org.assertj.core.api.Assertions.assertThat;

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
