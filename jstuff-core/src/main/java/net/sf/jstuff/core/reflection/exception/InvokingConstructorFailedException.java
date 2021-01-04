/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection.exception;

import java.lang.reflect.Constructor;

import net.sf.jstuff.core.reflection.SerializableConstructor;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class InvokingConstructorFailedException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   private final SerializableConstructor ctor;

   public InvokingConstructorFailedException(final Constructor<?> ctor, final Throwable cause) {
      super("Invoking constructor " + ctor + " failed.", cause);
      this.ctor = new SerializableConstructor(ctor);
   }

   public Constructor<?> getConstructor() {
      return ctor.getConstructor();
   }
}
