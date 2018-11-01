/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.persistence.jpa;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@StaticMetamodel(AbstractJPAEntity.class)
// CHECKSTYLE:IGNORE (StaticVariableName|TypeName) FOR NEXT 5 LINES
public abstract class AbstractJPAEntity_ {
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Integer> _version;
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Boolean> _isMarkedAsDeleted;
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Date> _firstPersistedOn;
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Date> _lastPersistedOn;
}
