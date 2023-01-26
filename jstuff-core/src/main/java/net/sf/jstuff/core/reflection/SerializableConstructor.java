/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.validation.Args;

/**
 * Serializable Wrapper for java.lang.reflect.Constructor objects since they do not implement Serializable
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SerializableConstructor implements Serializable {
   private static final long serialVersionUID = 1L;

   private transient Constructor<?> constructor;
   private @Nullable Class<?> declaringClass;
   private @NonNull Class<?> @Nullable [] parameterTypes;

   public SerializableConstructor(final Constructor<?> constructor) {
      Args.notNull("constructor", constructor);

      this.constructor = constructor;
   }

   public Constructor<?> getConstructor() {
      return constructor;
   }

   public Class<?> getDeclaringClass() {
      var declaringClass = this.declaringClass;
      if (declaringClass == null) {
         declaringClass = this.declaringClass = constructor.getDeclaringClass();
      }
      return declaringClass;
   }

   public @NonNull Class<?>[] getParameterTypes() {
      var parameterTypes = this.parameterTypes;
      if (parameterTypes == null) {
         parameterTypes = this.parameterTypes = constructor.getParameterTypes();
      }
      return parameterTypes;
   }

   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      try {
         constructor = getDeclaringClass().getDeclaredConstructor(parameterTypes);
      } catch (final NoSuchMethodException ex) {
         throw new IOException(ex);
      }
   }

   private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
      // ensure fields are populated
      getDeclaringClass();
      getParameterTypes();

      stream.defaultWriteObject();
   }
}
