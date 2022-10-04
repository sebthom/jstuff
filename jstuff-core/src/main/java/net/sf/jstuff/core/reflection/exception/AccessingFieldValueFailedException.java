/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection.exception;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.reflection.SerializableField;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AccessingFieldValueFailedException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   private final SerializableField field;
   @Nullable
   private final transient Object targetObject;
   @Nullable
   private final Serializable targetSerializableObject;

   public AccessingFieldValueFailedException(final Field field, final @Nullable Object targetObject, final Throwable cause) {
      super("Accessing value of field " + field.getName() + " failed.", cause);
      this.field = new SerializableField(field);
      this.targetObject = targetObject;
      targetSerializableObject = targetObject instanceof Serializable ? (Serializable) targetObject : null;
   }

   public Field getField() {
      return field.getField();
   }

   @Nullable
   public Object getTargetObject() {
      return targetObject != null ? targetObject : targetSerializableObject;
   }
}
