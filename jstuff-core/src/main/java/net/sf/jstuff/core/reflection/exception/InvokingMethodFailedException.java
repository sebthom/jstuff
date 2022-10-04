/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection.exception;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.reflection.SerializableMethod;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class InvokingMethodFailedException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   private final SerializableMethod method;
   @Nullable
   private final transient Object targetObject;
   @Nullable
   private final Serializable targetSerializableObject;

   public InvokingMethodFailedException(final Method method, final @Nullable Object targetObject, final Throwable cause) {
      super("Invoking method [" + method.getDeclaringClass().getName() + "." + method.getName() + "] failed.", cause);
      this.method = new SerializableMethod(method);
      this.targetObject = targetObject;
      targetSerializableObject = targetObject instanceof Serializable ? (Serializable) targetObject : null;
   }

   public Method getMethod() {
      return method.getMethod();
   }

   @Nullable
   public Object getTargetObject() {
      return targetObject != null ? targetObject : targetSerializableObject;
   }
}
