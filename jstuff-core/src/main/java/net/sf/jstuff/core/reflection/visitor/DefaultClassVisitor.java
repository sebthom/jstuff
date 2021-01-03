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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultClassVisitor implements ClassVisitor {
   @Override
   public boolean isVisiting(final Class<?> clazz) {
      return true;
   }

   @Override
   public boolean isVisitingField(final Field field) {
      return true;
   }

   @Override
   public boolean isVisitingFields(final Class<?> clazz) {
      return true;
   }

   @Override
   public boolean isVisitingInterfaces(final Class<?> clazz) {
      return true;
   }

   @Override
   public boolean isVisitingMethod(final Method method) {
      return true;
   }

   @Override
   public boolean isVisitingMethods(final Class<?> clazz) {
      return true;
   }

   @Override
   public boolean isVisitingSuperclass(final Class<?> clazz) {
      return true;
   }

   @Override
   public boolean visit(final Class<?> clazz) {
      return true;
   }

   @Override
   public boolean visit(final Field field) {
      return true;
   }

   @Override
   public boolean visit(final Method method) {
      return true;
   }
}
