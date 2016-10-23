/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.core.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Exceptions extends ExceptionUtils {

    public static <T extends Throwable> T getCauseOfType(final Throwable ex, final Class<T> type) {
        if (ex == null || type == null)
            return null;

        Throwable current = ex;
        while (current != null) {
            if (type.isInstance(current))
                return type.cast(current);
            current = current.getCause();
        }
        return null;
    }
}
