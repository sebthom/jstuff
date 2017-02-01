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
package net.sf.jstuff.integration.persistence.jpa;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@StaticMetamodel(AbstractJPAEntity.class)
public abstract class AbstractJPAEntity_ {
    public static volatile SingularAttribute<AbstractJPAEntity<?>, Integer> _version;
    public static volatile SingularAttribute<AbstractJPAEntity<?>, Boolean> _isMarkedAsDeleted;
    public static volatile SingularAttribute<AbstractJPAEntity<?>, Date> _firstPersistedOn;
    public static volatile SingularAttribute<AbstractJPAEntity<?>, Date> _lastPersistedOn;
}
