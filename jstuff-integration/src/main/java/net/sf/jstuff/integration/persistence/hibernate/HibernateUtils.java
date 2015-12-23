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
package net.sf.jstuff.integration.persistence.hibernate;

import org.hibernate.Hibernate;
import org.hibernate.collection.AbstractPersistentCollection;
import org.hibernate.proxy.HibernateProxy;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class HibernateUtils {
    public static boolean isInitializable(final Object object) {
        if (Hibernate.isInitialized(object))
            return true;

        if (object instanceof HibernateProxy) {
            final HibernateProxy proxy = (HibernateProxy) object;
            return proxy.getHibernateLazyInitializer().getSession() != null && proxy.getHibernateLazyInitializer().getSession().isOpen();
        }
        final AbstractPersistentCollection coll = (AbstractPersistentCollection) object;
        return coll.getSession() != null && coll.getSession().isOpen();
    }
}
