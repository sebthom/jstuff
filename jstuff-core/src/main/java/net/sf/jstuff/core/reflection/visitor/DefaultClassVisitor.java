/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultClassVisitor implements ClassVisitor {
   public boolean isVisiting(final Class<?> clazz) {
      return true;
   }

   public boolean isVisitingField(final Field field) {
      return true;
   }

   public boolean isVisitingFields(final Class<?> clazz) {
      return true;
   }

   public boolean isVisitingInterfaces(final Class<?> clazz) {
      return true;
   }

   public boolean isVisitingMethod(final Method method) {
      return true;
   }

   public boolean isVisitingMethods(final Class<?> clazz) {
      return true;
   }

   public boolean isVisitingSuperclass(final Class<?> clazz) {
      return true;
   }

   public boolean visit(final Class<?> clazz) {
      return true;
   }

   public boolean visit(final Field field) {
      return true;
   }

   public boolean visit(final Method method) {
      return true;
   }
}
