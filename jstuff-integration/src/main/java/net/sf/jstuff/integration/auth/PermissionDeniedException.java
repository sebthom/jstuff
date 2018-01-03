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
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PermissionDeniedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PermissionDeniedException() {
        super();
    }

    public PermissionDeniedException(final String message) {
        super(message);
    }

    public PermissionDeniedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PermissionDeniedException(final Throwable cause) {
        super(cause);
    }
}
