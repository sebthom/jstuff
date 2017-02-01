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
package net.sf.jstuff.core.exception;

/**
 * Lightweight checked exception without stack trace information that can be used for flow control.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastException extends Exception {
    private static final long serialVersionUID = 1L;

    public FastException() {
        super();
    }

    public FastException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FastException(final String message) {
        super(message);
    }

    public FastException(final Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
