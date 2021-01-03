/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection.exception;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.jstuff.core.reflection.SerializableMethod;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class InvokingMethodFailedException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   private final SerializableMethod method;
   private final transient Object targetObject;
   private final Serializable targetSerializableObject;

   public InvokingMethodFailedException(final Method method, final Object targetObject, final Throwable cause) {
      super("Invoking method [" + method.getDeclaringClass().getName() + "." + method.getName() + "] failed.", cause);
      this.method = new SerializableMethod(method);
      this.targetObject = targetObject;
      targetSerializableObject = targetObject instanceof Serializable ? (Serializable) targetObject : null;
   }

   public Method getMethod() {
      return method.getMethod();
   }

   public Object getTargetObject() {
      return targetObject != null ? targetObject : targetSerializableObject;
   }
}
