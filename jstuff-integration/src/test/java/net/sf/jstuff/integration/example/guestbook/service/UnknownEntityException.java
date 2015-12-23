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
package net.sf.jstuff.integration.example.guestbook.service;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnknownEntityException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnknownEntityException(final Class<?> type, final Integer id) {
        super("Unknown entity of type [" + type.getSimpleName() + "] with id [" + id + "]");
    }
}
