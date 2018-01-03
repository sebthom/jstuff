/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
        this.field = SerializableField.get(field);
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