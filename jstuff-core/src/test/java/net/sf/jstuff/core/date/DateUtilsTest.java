/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
        assertEquals(446582002, Dates.parseDuration("5d 4h 3m 2s 2ms"));
    }
}
