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
package net.sf.jstuff.core.types;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

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

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    IdType getId();

    /**
     * @return an identifier for the realm/namespace in which the id is unique, e.g. the concrete class name.
     */
    Object getIdRealm();
}
