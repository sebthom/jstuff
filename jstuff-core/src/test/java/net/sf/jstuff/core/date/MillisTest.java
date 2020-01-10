/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.date;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MillisTest extends TestCase {

   public void testMillis() {
      assertEquals(Millis.fromDays(1), Millis.fromHours(24));
      assertEquals(Millis.fromHours(1), Millis.fromMinutes(60));
      assertEquals(Millis.fromMinutes(1), Millis.fromSeconds(60));
      assertEquals(Millis.fromSeconds(1), 1_000);
   }
}
