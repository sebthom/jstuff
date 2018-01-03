/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.math;

import static net.sf.jstuff.core.math.NumericalSystem.*;

import java.math.BigInteger;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NumericalSystemTest extends TestCase {

    public void testBase16Decode() {
        assertEquals(0, BASE16.decodeAsLong("0"));
        assertEquals(1, BASE16.decodeAsLong("1"));
        assertEquals(123456789, BASE16.decodeAsLong(BASE16.encode(123456789)));
        assertEquals(Long.MAX_VALUE, BASE16.decodeAsLong(BASE16.encode(Long.MAX_VALUE)));

        assertEquals(0, BASE16.decodeAsBigInteger("0").longValue());
        assertEquals(1, BASE16.decodeAsBigInteger("1").longValue());
        assertEquals(123456789, BASE16.decodeAsBigInteger(BASE16.encode(123456789)).longValue());
        assertEquals(Long.MAX_VALUE, BASE16.decodeAsBigInteger(BASE16.encode(Long.MAX_VALUE)).longValue());
    }

    public void testBase36Decode() {
        assertEquals(0, BASE36.decodeAsLong("0"));
        assertEquals(1, BASE36.decodeAsLong("1"));
        assertEquals(123456789, BASE36.decodeAsLong(BASE36.encode(123456789)));
        assertEquals(Long.MAX_VALUE, BASE36.decodeAsLong(BASE36.encode(Long.MAX_VALUE)));

        assertEquals(0, BASE36.decodeAsBigInteger("0").longValue());
        assertEquals(1, BASE36.decodeAsBigInteger("1").longValue());
        assertEquals(123456789, BASE36.decodeAsBigInteger(BASE36.encode(123456789)).longValue());
        assertEquals(Long.MAX_VALUE, BASE36.decodeAsBigInteger(BASE36.encode(Long.MAX_VALUE)).longValue());
    }

    public void testBase62Decode() {
        assertEquals(0, BASE62.decodeAsLong("0"));
        assertEquals(1, BASE62.decodeAsLong("1"));
        assertEquals(123456789, BASE62.decodeAsLong(BASE62.encode(123456789)));
        assertEquals(Long.MAX_VALUE, BASE62.decodeAsLong(BASE62.encode(Long.MAX_VALUE)));

        assertEquals(0, BASE62.decodeAsBigInteger("0").longValue());
        assertEquals(1, BASE62.decodeAsBigInteger("1").longValue());
        assertEquals(123456789, BASE62.decodeAsBigInteger(BASE62.encode(123456789)).longValue());
        assertEquals(Long.MAX_VALUE, BASE62.decodeAsBigInteger(BASE62.encode(Long.MAX_VALUE)).longValue());
    }

    public void testToBase16Encode() {
        assertEquals("0", BASE16.encode(0));
        assertEquals("1", BASE16.encode(1));
        assertEquals("f", BASE16.encode(15));
        assertEquals("23", BASE16.encode(35));
        assertEquals("24", BASE16.encode(36));
        assertEquals("3d", BASE16.encode(61));

        assertEquals("0", BASE16.encode(BigInteger.valueOf(0)));
        assertEquals("1", BASE16.encode(BigInteger.valueOf(1)));
        assertEquals("f", BASE16.encode(BigInteger.valueOf(15)));
        assertEquals("23", BASE16.encode(BigInteger.valueOf(35)));
        assertEquals("24", BASE16.encode(BigInteger.valueOf(36)));
        assertEquals("3d", BASE16.encode(BigInteger.valueOf(61)));

        assertEquals(BigInteger.valueOf(123456789).toString(16), BASE16.encode(123456789));
    }

    public void testToBase36Encode() {
        assertEquals("0", BASE36.encode(0));
        assertEquals("1", BASE36.encode(1));
        assertEquals("f", BASE36.encode(15));
        assertEquals("z", BASE36.encode(35));
        assertEquals("10", BASE36.encode(36));
        assertEquals("1p", BASE36.encode(61));

        assertEquals("0", BASE36.encode(BigInteger.valueOf(0)));
        assertEquals("1", BASE36.encode(BigInteger.valueOf(1)));
        assertEquals("f", BASE36.encode(BigInteger.valueOf(15)));
        assertEquals("z", BASE36.encode(BigInteger.valueOf(35)));
        assertEquals("10", BASE36.encode(BigInteger.valueOf(36)));
        assertEquals("1p", BASE36.encode(BigInteger.valueOf(61)));

        assertEquals(BigInteger.valueOf(123456789).toString(36), BASE36.encode(123456789));
    }

    public void testToBase62Encode() {
        assertEquals("0", BASE62.encode(0));
        assertEquals("1", BASE62.encode(1));
        assertEquals("f", BASE62.encode(15));
        assertEquals("z", BASE62.encode(35));
        assertEquals("A", BASE62.encode(36));
        assertEquals("Z", BASE62.encode(61));

        assertEquals("0", BASE62.encode(BigInteger.valueOf(0)));
        assertEquals("1", BASE62.encode(BigInteger.valueOf(1)));
        assertEquals("f", BASE62.encode(BigInteger.valueOf(15)));
        assertEquals("z", BASE62.encode(BigInteger.valueOf(35)));
        assertEquals("A", BASE62.encode(BigInteger.valueOf(36)));
        assertEquals("Z", BASE62.encode(BigInteger.valueOf(61)));

        // Max Radix for BigInteger is 36, thus the following test would fail
        // assertEquals(BigInteger.valueOf(123456789).toString(62), BASE36.encode(123456789));
    }

}
