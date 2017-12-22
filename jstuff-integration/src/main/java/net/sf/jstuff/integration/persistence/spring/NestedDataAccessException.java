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
package net.sf.jstuff.integration.persistence.spring;

import org.springframework.dao.DataAccessException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NestedDataAccessException extends DataAccessException {
    private static final long serialVersionUID = 1L;

    public NestedDataAccessException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
