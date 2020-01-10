/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection.exception;

import java.lang.reflect.Constructor;

import net.sf.jstuff.core.reflection.SerializableConstructor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
