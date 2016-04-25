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
package net.sf.jstuff.core.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.sf.jstuff.core.reflection.exception.AccessingFieldValueFailedException;
import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.reflection.exception.SettingFieldValueFailedException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Fields extends Members {
    public static boolean exists(final Class<?> clazz, final String fieldName) {
        return find(clazz, fieldName) != null;
    }

    public static boolean exists(final Class<?> clazz, final String fieldName, final Class<?> compatibleTo) {
        return find(clazz, fieldName, compatibleTo) != null;
    }

    /**
     * @return the field or null if the field does not exist
     */
    public static Field find(final Class<?> clazz, final String fieldName) {
        return find(clazz, fieldName, null);
    }

    /**
     * @param compatibleTo the field type must assignable from this type, i.e. objects of type <code>compatibleTo</code> must be assignable to the field
     * @return the field or null if the field does not exist
     */
    public static Field find(final Class<?> clazz, final String fieldName, final Class<?> compatibleTo) {
        Args.notNull("clazz", clazz);
        Args.notNull("fieldName", fieldName);

        try {
            final Field field = clazz.getDeclaredField(fieldName);
            if (compatibleTo == null)
                return field;
            if (Types.isAssignableTo(compatibleTo, field.getType()))
                return field;
        } catch (final NoSuchFieldException ex) {
            // ignore
        }
        return null;
    }

    /**
     * @return the field or null if the field does not exist
     */
    public static Field findRecursive(final Class<?> clazz, final String fieldName) {
        return findRecursive(clazz, fieldName, null);
    }

    /**
     * @param compatibleTo the field type must be a super class or interface of <code>compatibleTo</code>
     * @return the field or null if the field does not exist
     */
    public static Field findRecursive(final Class<?> clazz, final String fieldName, final Class<?> compatibleTo) {
        Args.notNull("clazz", clazz);
        Args.notNull("fieldName", fieldName);

        final Field field = find(clazz, fieldName, compatibleTo);
        if (field != null)
            return field;

        final Class<?> superclazz = clazz.getSuperclass();
        if (superclazz == null)
            return null;

        return findRecursive(superclazz, fieldName, compatibleTo);
    }

    /**
     * @param obj specify <code>null</code> for static fields
     */
    @SuppressWarnings("unchecked")
    public static <T> T read(final Object obj, final Field field) throws AccessingFieldValueFailedException {
        Args.notNull("field", field);

        try {
            ensureAccessible(field);
            return (T) field.get(obj);
        } catch (final Exception ex) {
            throw new AccessingFieldValueFailedException(field, obj, ex);
        }
    }

    /**
     * @param obj specify <code>null</code> for static fields
     */
    public static void write(final Object obj, final Field field, final Object value) throws SettingFieldValueFailedException {
        Args.notNull("field", field);

        if (isFinal(field))
            throw new SettingFieldValueFailedException(field, obj, "Cannot write to final field " + field.getDeclaringClass().getName() + "#" + field
                .getName());

        try {
            ensureAccessible(field);
            field.set(obj, value);
        } catch (final Exception ex) {
            throw new SettingFieldValueFailedException(field, obj, ex);
        }
    }

    /**
     * Writes to a static field
     */
    public static void write(final Class<?> clazz, final String fieldName, final Object value) throws ReflectionException {
        Args.notNull("clazz", clazz);
        Args.notNull("fieldName", fieldName);

        final Field field = findRecursive(clazz, fieldName);
        write(null, field, value);
    }

    public static void write(final Object obj, final String fieldName, final Object value) throws ReflectionException {
        Args.notNull("obj", obj);
        Args.notNull("fieldName", fieldName);

        final Field field = findRecursive(obj.getClass(), fieldName);
        if (field == null)
            throw new ReflectionException("Field with name [" + fieldName + "] not found in object [" + obj + "]");

        if (isFinal(field))
            throw new SettingFieldValueFailedException(field, obj, "Cannot write to final field " + field.getDeclaringClass().getName() + "#" + field
                .getName());

        try {
            ensureAccessible(field);
            field.set(obj, value);
        } catch (final Exception ex) {
            throw new SettingFieldValueFailedException(field, obj, ex);
        }
    }

    /**
     * Writes to a static field
     */
    public static void writeIgnoringFinal(final Class<?> clazz, final String fieldName, final Object value) throws ReflectionException {
        Args.notNull("clazz", clazz);
        Args.notNull("fieldName", fieldName);

        final Field field = findRecursive(clazz, fieldName);
        writeIgnoringFinal(null, field, value);
    }

    /**
     * @param obj specify <code>null</code> for static fields
     */
    public static void writeIgnoringFinal(final Object obj, final Field field, final Object value) throws SettingFieldValueFailedException {
        Args.notNull("field", field);

        try {
            ensureAccessible(field);
            write(field, "modifiers", field.getModifiers() & ~Modifier.FINAL);
            field.set(obj, value);
        } catch (final Exception ex) {
            throw new SettingFieldValueFailedException(field, obj, ex);
        }
    }

    public static void writeIgnoringFinal(final Object obj, final String fieldName, final Object value) throws ReflectionException {
        Args.notNull("obj", obj);
        Args.notNull("fieldName", fieldName);

        final Field field = findRecursive(obj.getClass(), fieldName);
        if (field == null)
            throw new ReflectionException("Field with name [" + fieldName + "] not found in object [" + obj + "]");

        try {
            ensureAccessible(field);
            write(field, "modifiers", field.getModifiers() & ~Modifier.FINAL);
            field.set(obj, value);
        } catch (final Exception ex) {
            throw new SettingFieldValueFailedException(field, obj, ex);
        }
    }
}
