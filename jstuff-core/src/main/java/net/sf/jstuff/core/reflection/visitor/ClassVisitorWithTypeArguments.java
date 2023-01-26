/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.ParameterizedType;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ClassVisitorWithTypeArguments {
   /**
    * @return false if the class hierarchy visit shall be aborted
    */
   boolean visit(Class<?> clazz, @Nullable ParameterizedType type);

   /**
    * @return if the class shall be visited
    */
   boolean isVisiting(Class<?> clazz, @Nullable ParameterizedType type);

   /**
    * @return if true the superclass shall be visited
    */
   boolean isVisitingSuperclass(Class<?> clazz, @Nullable ParameterizedType type);

   /**
    * @return if true the implemented interfaces shall be visited
    */
   boolean isVisitingInterfaces(Class<?> clazz, @Nullable ParameterizedType type);
}
