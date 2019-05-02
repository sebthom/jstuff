/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.date;

import java.text.ParseException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DateUtilsTest extends TestCase {
   public void testParseDuration() throws ParseException {
      assertEquals(1, Dates.parseDuration("1ms"));
      assertEquals(1000, Dates.parseDuration("1s"));
      assertEquals(1000, Dates.parseDuration(" 1sec "));
      assertEquals(1000 * 60, Dates.parseDuration("1m"));
      assertEquals(1000 * 60, Dates.parseDuration(" 1min "));
      assertEquals(1000 * 60 * 60, Dates.parseDuration("1h"));
      assertEquals(1000 * 60 * 60, Dates.parseDuration(" 1hour "));
      assertEquals(1000 * 60 * 60 * 24, Dates.parseDuration("1d"));
      assertEquals(1000 * 60 * 60 * 24, Dates.parseDuration(" 1day "));
      assertEquals(446_582_002, Dates.parseDuration("5d 4h 3m 2s 2ms"));
   }
}
