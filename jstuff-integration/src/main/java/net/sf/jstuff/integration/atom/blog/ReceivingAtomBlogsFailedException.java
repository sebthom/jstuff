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
package net.sf.jstuff.integration.atom.blog;

public class ReceivingAtomBlogsFailedException extends AtomBlogException {
    private static final long serialVersionUID = 1L;

    public ReceivingAtomBlogsFailedException() {
        super();
    }

    public ReceivingAtomBlogsFailedException(final String message) {
        super(message);
    }

    public ReceivingAtomBlogsFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ReceivingAtomBlogsFailedException(final Throwable cause) {
        super(cause);
    }

}
