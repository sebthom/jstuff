/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.apache.commons.io.IOExceptionWithCause;

import net.sf.jstuff.core.validation.Args;

/**
 * Serializable Wrapper for java.lang.reflect.Constructor objects since they do not implement Serializable
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SerializableConstructor implements Serializable {
   private static final long serialVersionUID = 1L;

   private transient Constructor<?> constructor;
   private Class<?> declaringClass;
   private Class<?>[] parameterTypes;

   public SerializableConstructor(final Constructor<?> constructor) {
      Args.notNull("constructor", constructor);

      this.constructor = constructor;
   }

   public Constructor<?> getConstructor() {
      return constructor;
   }

   public Class<?> getDeclaringClass() {
      if (declaringClass == null) {
         declaringClass = constructor.getDeclaringClass();
      }
      return declaringClass;
   }

   public Class<?>[] getParameterTypes() {
      if (parameterTypes == null) {
         parameterTypes = constructor.getParameterTypes();
      }
      return parameterTypes;
   }

   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      try {
         constructor = declaringClass.getDeclaredConstructor(parameterTypes);
      } catch (final NoSuchMethodException ex) {
         throw new IOExceptionWithCause(ex);
      }
   }

   private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
      // ensure fields are populated
      getDeclaringClass();
      getParameterTypes();

      stream.defaultWriteObject();
   }
}
