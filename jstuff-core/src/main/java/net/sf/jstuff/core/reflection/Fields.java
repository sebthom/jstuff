/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.UnsafeUtils;
import net.sf.jstuff.core.reflection.exception.AccessingFieldValueFailedException;
import net.sf.jstuff.core.reflection.exception.InvokingMethodFailedException;
import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.reflection.exception.SettingFieldValueFailedException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Fields extends Members {

   public static void assertAssignable(final Field field, final @Nullable Object value) throws IllegalArgumentException {
      if (value == null) {
         if (field.getType().isPrimitive())
            throw new IllegalArgumentException("Cannot assign null value to primitive field [" + field.getDeclaringClass().getSimpleName()
                  + "#" + field.getName() + "]");
         return;
      }

      if (!Types.isAssignableTo(value.getClass(), field.getType()))
         throw new IllegalArgumentException("Cannot assign value " + value + " to incompatible field [" + field.getDeclaringClass()
            .getSimpleName() + "#" + field.getName() + "]");
   }

   public static boolean exists(final Class<?> clazz, final String fieldName) {
      return find(clazz, fieldName) != null;
   }

   public static boolean exists(final Class<?> clazz, final String fieldName, final Class<?> compatibleWith) {
      return find(clazz, fieldName, compatibleWith) != null;
   }

   /**
    * @return the field or null if the field does not exist
    */
   @Nullable
   public static Field find(final Class<?> clazz, final String fieldName) {
      return find(clazz, fieldName, null);
   }

   /**
    * @param compatibleWith objects of type <code>compatibleWith</code> must be assignable to the field
    * @return the field or null if the field does not exist
    */
   @Nullable
   public static Field find(final Class<?> clazz, final String fieldName, final @Nullable Class<?> compatibleWith) {
      Args.notNull("fieldName", fieldName);

      Field field = null;
      try {
         field = clazz.getDeclaredField(fieldName);
      } catch (final NoSuchFieldException ex) {
         try {
            final Field[] fields = Methods.invoke(clazz, "getDeclaredFields0", false /*publicOnly*/);
            for (final Field f : fields) {
               if (fieldName.equals(f.getName())) {
                  field = f;
                  break;
               }
            }
         } catch (final InvokingMethodFailedException ignore) {
            // ignore
         }
      }

      if (field == null)
         return null;

      if (compatibleWith == null || Types.isAssignableTo(compatibleWith, field.getType()))
         return field;

      return null;
   }

   /**
    * @return the field or null if the field does not exist
    */
   @Nullable
   public static Field findRecursive(final Class<?> clazz, final String fieldName) {
      return findRecursive(clazz, fieldName, null);
   }

   /**
    * @param compatibleWith the field type must be a super class or interface of <code>compatibleWith</code>
    * @return the field or null if the field does not exist
    */
   @Nullable
   public static Field findRecursive(final Class<?> clazz, final String fieldName, final @Nullable Class<?> compatibleWith) {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      final Field field = find(clazz, fieldName, compatibleWith);
      if (field != null)
         return field;

      final Class<?> superclazz = clazz.getSuperclass();
      if (superclazz == null)
         return null;

      return findRecursive(superclazz, fieldName, compatibleWith);
   }

   @Nullable
   public static VarHandle findVarHandle(final Class<?> clazz, final String fieldName) {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      return findVarHandle(clazz, fieldName, null);
   }

   @Nullable
   public static VarHandle findVarHandle(final Class<?> clazz, final String fieldName, final @Nullable Class<?> fieldType) {
      try {
         UnsafeUtils.openModule(clazz.getModule());
         final Lookup lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
         return lookup.findVarHandle(Field.class, fieldName, fieldType);
      } catch (final IllegalAccessException ex) {
         throw new ReflectionException(ex);
      } catch (final NoSuchFieldException e) {
         return null;
      }
   }

   /**
    * @throws ReflectionException if field cannot be found
    */
   public static Field get(final Class<?> clazz, final String fieldName) {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      final var field = find(clazz, fieldName, null);
      if (field == null)
         throw new ReflectionException("Field [" + fieldName + "] not found in class [" + clazz.getName() + "].");
      return field;
   }

   /**
    * @param compatibleWith the field type must assignable from this type, i.e. objects of type <code>compatibleWith</code> must be
    *           assignable to the field
    * @throws ReflectionException if field cannot be found
    */
   public static Field get(final Class<?> clazz, final String fieldName, final @Nullable Class<?> compatibleWith) {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      final var field = find(clazz, fieldName, compatibleWith);
      if (field == null)
         throw new ReflectionException("Field [" + fieldName + "] compatible with [" + compatibleWith + "] not found in class [" + clazz
            .getName() + "].");
      return field;
   }

   /**
    * @throws ReflectionException if field cannot be found
    */
   public static Field getRecursive(final Class<?> clazz, final String fieldName) {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      final var field = findRecursive(clazz, fieldName, null);
      if (field == null)
         throw new ReflectionException("Field [" + fieldName + "] not found in class [" + clazz.getName()
               + "]  or any of it's super classes.");
      return field;
   }

   /**
    * @param compatibleWith objects of type <code>compatibleWith</code> must be assignable to the field
    * @throws ReflectionException if field cannot be found
    */
   public static Field getRecursive(final Class<?> clazz, final String fieldName, final @Nullable Class<?> compatibleWith) {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      final var field = findRecursive(clazz, fieldName, compatibleWith);
      if (field == null)
         throw new ReflectionException("Field [" + fieldName + "] compatible with [" + compatibleWith + "] not found in class [" + clazz
            .getName() + "] or any of it's super classes.");
      return field;
   }

   /**
    * @return true if the given value can be assigned to the given field, i.e. is compatible with the field's type
    */
   public static boolean isAssignable(final Field field, final @Nullable Object value) {
      Args.notNull("field", field);

      if (value == null)
         return !field.getType().isPrimitive();

      return Types.isAssignableTo(value.getClass(), field.getType());
   }

   /**
    * @param obj specify <code>null</code> for static fields
    */
   @SuppressWarnings("unchecked")
   public static <T> T read(final @Nullable Object obj, final Field field) throws AccessingFieldValueFailedException {
      Args.notNull("field", field);

      try {
         field.trySetAccessible();
         return (T) field.get(obj);
      } catch (final Exception ex) {
         throw new AccessingFieldValueFailedException(field, obj, ex);
      }
   }

   /**
    * @param obj specify <code>null</code> for static fields
    * @return null if field not found
    */
   @Nullable
   @SuppressWarnings("unchecked")
   public static <T> T read(final Object obj, final String fieldName) throws AccessingFieldValueFailedException {
      Args.notNull("obj", obj);
      Args.notNull("field", fieldName);

      final Field field = findRecursive(obj.getClass(), fieldName);
      if (field == null)
         return null;

      try {
         field.trySetAccessible();
         return (T) field.get(obj);
      } catch (final Exception ex) {
         throw new AccessingFieldValueFailedException(field, obj, ex);
      }
   }

   /**
    * Writes to a static field
    */
   public static void write(final Class<?> clazz, final String fieldName, final @Nullable Object value) throws ReflectionException {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      final Field field = findRecursive(clazz, fieldName);
      if (field == null)
         throw new ReflectionException("No static field with name [" + fieldName + "] found in class [" + clazz.getName() + "]");

      write(null, field, value);
   }

   /**
    * @param obj specify <code>null</code> for static fields
    */
   public static void write(final @Nullable Object obj, final Field field, final @Nullable Object value)
         throws SettingFieldValueFailedException {
      Args.notNull("field", field);

      if (isFinal(field))
         throw new SettingFieldValueFailedException(field, obj, "Cannot write to final field " + field.getDeclaringClass().getName() + "#"
               + field.getName());

      try {
         field.trySetAccessible();
         field.set(obj, value);
      } catch (final Exception ex) {
         throw new SettingFieldValueFailedException(field, obj, ex);
      }
   }

   public static void write(final Object obj, final String fieldName, final @Nullable Object value) throws ReflectionException {
      Args.notNull("obj", obj);
      Args.notNull("fieldName", fieldName);

      final Field field = findRecursive(obj.getClass(), fieldName);
      if (field == null)
         throw new ReflectionException("No field with name [" + fieldName + "] found in object [" + obj + "]");

      if (isFinal(field))
         throw new SettingFieldValueFailedException(field, obj, "Cannot write to final field " + field.getDeclaringClass().getName() + "#"
               + field.getName());

      try {
         field.trySetAccessible();
         field.set(obj, value);
      } catch (final Exception ex) {
         throw new SettingFieldValueFailedException(field, obj, ex);
      }
   }

   /**
    * Writes to a static field
    */
   public static void writeIgnoringFinal(final Class<?> clazz, final String fieldName, final @Nullable Object value)
         throws ReflectionException {
      Args.notNull("clazz", clazz);
      Args.notNull("fieldName", fieldName);

      final Field field = findRecursive(clazz, fieldName);
      if (field == null)
         throw new IllegalArgumentException("Field with name [" + fieldName + "] not found in class " + clazz.getName());
      writeIgnoringFinal(null, field, value);
   }

   /**
    * @param obj specify <code>null</code> for static fields
    */
   public static void writeIgnoringFinal(final @Nullable Object obj, final Field field, final @Nullable Object value)
         throws SettingFieldValueFailedException {
      Args.notNull("field", field);

      try {
         if (isStatic(field) && isFinal(field)) {
            try {
               assertAssignable(field, value);
            } catch (final Exception ex) {
               throw new SettingFieldValueFailedException(field, obj, ex);
            }

            // https://stackoverflow.com/a/71465198
            final var fieldBase = UnsafeUtils.UNSAFE.staticFieldBase(field);
            final var fieldOffset = UnsafeUtils.UNSAFE.staticFieldOffset(field);
            UnsafeUtils.UNSAFE.putObject(fieldBase, fieldOffset, value);
            return;
         }
         field.trySetAccessible();
         field.set(obj, value);
      } catch (final Exception ex) {
         throw new SettingFieldValueFailedException(field, obj, ex);
      }
   }

   public static void writeIgnoringFinal(final Object obj, final String fieldName, final @Nullable Object value)
         throws ReflectionException {
      Args.notNull("fieldName", fieldName);

      final Field field = findRecursive(obj.getClass(), fieldName);
      if (field == null)
         throw new ReflectionException("Field with name [" + fieldName + "] not found in object [" + obj + "]");

      writeIgnoringFinal(obj, field, value);
   }
}
