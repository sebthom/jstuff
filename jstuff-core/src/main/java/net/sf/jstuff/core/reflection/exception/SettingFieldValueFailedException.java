/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
public class SettingFieldValueFailedException extends ReflectionException {
    private static final long serialVersionUID = 1L;

    private final SerializableField field;
    private final transient Object targetObject;
    private final Serializable targetSerializableObject;

    public SettingFieldValueFailedException(final Field field, final Object targetObject, final String message) {
        super(message);
        this.field = SerializableField.get(field);
        this.targetObject = targetObject;
        targetSerializableObject = targetObject instanceof Serializable ? (Serializable) targetObject : null;
    }

    public SettingFieldValueFailedException(final Field field, final Object targetObject, final Throwable cause) {
        super("Setting value of field " + field.getName() + " failed.", cause);
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