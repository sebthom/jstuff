/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ClassVisitor {

   /**
    * @return if true the class' fields shall be visited
    */
   boolean isVisitingFields(Class<?> clazz);

   /**
    * @return true if the {@link #visit(Field)} method shall be executed on the given field
    */
   boolean isVisitingField(Field field);

   /**
    * @return false if the class hierarchy visit shall be aborted
    */
   boolean visit(Field field);

   /**
    * @return if true the class' methods shall be visited
    */
   boolean isVisitingMethods(Class<?> clazz);

   /**
    * @return true if the {@link #visit(Method)} method shall be executed on the given method
    */
   boolean isVisitingMethod(Method method);

   /**
    * @return false if the class hierarchy visit shall be aborted
    */
   boolean visit(Method method);

   /**
    * @return false if the class hierarchy visit shall be aborted
    */
   boolean visit(Class<?> clazz);

   /**
    * @return if the class shall be visited
    */
   boolean isVisiting(Class<?> clazz);

   /**
    * @return if true the superclass shall be visited
    */
   boolean isVisitingSuperclass(Class<?> clazz);

   /**
    * @return if true the implemented interfaces shall be visited
    */
   boolean isVisitingInterfaces(Class<?> clazz);
}
