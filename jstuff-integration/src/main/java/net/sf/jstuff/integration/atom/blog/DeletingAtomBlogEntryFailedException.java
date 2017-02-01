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
package net.sf.jstuff.integration.atom.blog;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 *
 */
public class DeletingAtomBlogEntryFailedException extends AtomBlogException {
    private static final long serialVersionUID = 1L;

    public DeletingAtomBlogEntryFailedException() {
        super();
    }

    public DeletingAtomBlogEntryFailedException(final String message) {
        super(message);
    }

    public DeletingAtomBlogEntryFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DeletingAtomBlogEntryFailedException(final Throwable cause) {
        super(cause);
    }
}
