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
package net.sf.jstuff.core;

import java.io.Serializable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Identifiable<IdType> {
    /**
     * Default implementation that uses the object's class as id realm
     */
    class Default<IdType> implements Identifiable<IdType>, Serializable {
        private static final long serialVersionUID = 1L;

        private IdType id;

        public IdType getId() {
            return id;
        }

        public void setId(final IdType id) {
            this.id = id;
        }

        public Object getIdRealm() {
            return getClass();
        }
    }

    IdType getId();

    /**
     * @return an identifier for the realm/namespace in which the id is unique, e.g. the concrete class name.
     */
    Object getIdRealm();
}
