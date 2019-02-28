/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection.exception;

import java.io.Serializable;
import java.lang.reflect.Field;

import net.sf.jstuff.core.reflection.SerializableField;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AccessingFieldValueFailedException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   private final SerializableField field;
   private final transient Object targetObject;
   private final Serializable targetSerializableObject;

   public AccessingFieldValueFailedException(final Field field, final Object targetObject, final Throwable cause) {
      super("Accessing value of field " + field.getName() + " failed.", cause);
      this.field = new SerializableField(field);
      this.targetObject = targetObject;
      targetSerializableObject = targetObject instanceof Serializable ? (Serializable) targetObject : null;
   }

   public Field getField() {
      return field.getField();
   }

   public Object getTargetObject() {
      return targetObject != null ? targetObject : targetSerializableObject;
   }
}
