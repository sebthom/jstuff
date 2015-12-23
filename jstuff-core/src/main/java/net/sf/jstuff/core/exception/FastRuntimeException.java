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
package net.sf.jstuff.core.exception;

/**
 * Lightweight unchecked exception without stack trace information that can be used for flow control.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FastRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public FastRuntimeException() {
        super();
    }

    public FastRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FastRuntimeException(final String message) {
        super(message);
    }

    public FastRuntimeException(final Throwable cause) {
        super(cause);
    }

    @Override
    public Throwable fillInStackTrace() {
        return null;
    }
}
