/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence.jpa;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@StaticMetamodel(AbstractJPAEntity.class)
// CHECKSTYLE:IGNORE (StaticVariableName|TypeName) FOR NEXT 5 LINES
public abstract class AbstractJPAEntity_ {
   public static volatile @Nullable SingularAttribute<AbstractJPAEntity<?>, Integer> _version;
   public static volatile @Nullable SingularAttribute<AbstractJPAEntity<?>, Boolean> _isMarkedAsDeleted;
   public static volatile @Nullable SingularAttribute<AbstractJPAEntity<?>, Date> _firstPersistedOn;
   public static volatile @Nullable SingularAttribute<AbstractJPAEntity<?>, Date> _lastPersistedOn;
}
