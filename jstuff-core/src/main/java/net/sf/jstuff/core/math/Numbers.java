/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.core.math;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.UUID;

import net.sf.jstuff.core.validation.Args;

/**
 * Base62 = Duosexagesimal
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Numbers extends org.apache.commons.lang3.math.NumberUtils {

    public static final int MILLION = 1000 * 1000;
    public static final int BILLION = 1000 * MILLION;

    private static final BigInteger LONG_MAX_VALUE = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger LONG_MIN_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

    public static boolean isLong(final BigInteger number) {
        Args.notNull("number", number);
        return LONG_MAX_VALUE.compareTo(number) >= 0 && LONG_MIN_VALUE.compareTo(number) <= 0;
    }

    public static BigInteger toBigInteger(final UUID uuid) {
        if (uuid == null)
            return null;
        return new BigInteger(1, ByteBuffer.wrap(new byte[16]) //
            .putLong(uuid.getMostSignificantBits()) //
            .putLong(uuid.getLeastSignificantBits()) //
            .array());
    }
}
