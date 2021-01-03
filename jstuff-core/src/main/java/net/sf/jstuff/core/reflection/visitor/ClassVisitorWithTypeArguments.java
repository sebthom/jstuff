/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.ParameterizedType;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ClassVisitorWithTypeArguments {
   /**
    * @return false if the class hierarchy visit shall be aborted
    */
   boolean visit(Class<?> clazz, ParameterizedType type);

   /**
    * @return if the class shall be visited
    */
   boolean isVisiting(Class<?> clazz, ParameterizedType type);

   /**
    * @return if true the superclass shall be visited
    */
   boolean isVisitingSuperclass(Class<?> clazz, ParameterizedType type);

   /**
    * @return if true the implemented interfaces shall be visited
    */
   boolean isVisitingInterfaces(Class<?> clazz, ParameterizedType type);
}
