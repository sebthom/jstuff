/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Serializable Wrapper for java.lang.reflect.Method objects since they do not implement Serializable
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SerializableMethod implements Serializable {
   private static final long serialVersionUID = 1L;

   private Class<?> declaringClass;
   private transient Method method;
   private String name;
   private Class<?>[] parameterTypes;

   public SerializableMethod(final Method method) {
      this.method = method;
   }

   public Class<?> getDeclaringClass() {
      if (declaringClass == null) {
         declaringClass = method.getDeclaringClass();
      }
      return declaringClass;
   }

   public Method getMethod() {
      return method;
   }

   public String getName() {
      if (name == null) {
         name = method.getName();
      }
      return name;
   }

   public Class<?>[] getParameterTypes() {
      if (parameterTypes == null) {
         parameterTypes = method.getParameterTypes();
      }
      return parameterTypes;
   }

   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      try {
         method = declaringClass.getDeclaredMethod(name, parameterTypes);
      } catch (final NoSuchMethodException ex) {
         throw new IOException(ex);
      }
   }

   private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
      // ensure fields are populated
      getName();
      getDeclaringClass();
      getParameterTypes();

      stream.defaultWriteObject();
   }
}
