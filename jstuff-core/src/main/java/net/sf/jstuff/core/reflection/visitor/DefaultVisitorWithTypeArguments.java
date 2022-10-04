/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.ParameterizedType;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultVisitorWithTypeArguments implements ClassVisitorWithTypeArguments {
   @Override
   public boolean isVisiting(final Class<?> clazz, final @Nullable ParameterizedType type) {
      return true;
   }

   @Override
   public boolean isVisitingInterfaces(final Class<?> clazz, final @Nullable ParameterizedType type) {
      return true;
   }

   @Override
   public boolean isVisitingSuperclass(final Class<?> clazz, final @Nullable ParameterizedType type) {
      return true;
   }

   @Override
   public boolean visit(final Class<?> clazz, final @Nullable ParameterizedType type) {
      return true;
   }
}
