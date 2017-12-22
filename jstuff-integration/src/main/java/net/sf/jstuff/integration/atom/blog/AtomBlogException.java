/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.integration.atom.blog;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomBlogException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AtomBlogException() {
        super();
    }

    public AtomBlogException(final String message) {
        super(message);
    }

    public AtomBlogException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AtomBlogException(final Throwable cause) {
        super(cause);
    }
}
