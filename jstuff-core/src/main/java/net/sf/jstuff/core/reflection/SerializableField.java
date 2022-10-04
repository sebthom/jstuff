/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Serializable Wrapper for java.lang.reflect.Field objects since they do not implement Serializable
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SerializableField implements Serializable {
   private static final long serialVersionUID = 1L;

   @Nullable
   private Class<?> declaringClass;
   private transient Field field;
   @Nullable
   private String name;

   public SerializableField(final Field field) {
      this.field = field;
   }

   public Class<?> getDeclaringClass() {
      var declaringClass = this.declaringClass;
      if (declaringClass == null) {
         declaringClass = this.declaringClass = field.getDeclaringClass();
      }
      return declaringClass;
   }

   public Field getField() {
      return field;
   }

   public String getName() {
      var name = this.name;
      if (name == null) {
         name = this.name = field.getName();
      }
      return name;
   }

   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      try {
         field = getDeclaringClass().getDeclaredField(getName());
      } catch (final NoSuchFieldException ex) {
         throw new IOException(ex);
      }
   }

   private void writeObject(final java.io.ObjectOutputStream stream) throws IOException {
      // ensure fields are populated
      getName();
      getDeclaringClass();

      stream.defaultWriteObject();
   }
}
