/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence.jpa;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@StaticMetamodel(AbstractJPAEntity.class)
// CHECKSTYLE:IGNORE (StaticVariableName|TypeName) FOR NEXT 5 LINES
public abstract class AbstractJPAEntity_ {
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Integer> _version;
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Boolean> _isMarkedAsDeleted;
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Date> _firstPersistedOn;
   public static volatile SingularAttribute<AbstractJPAEntity<?>, Date> _lastPersistedOn;
}
