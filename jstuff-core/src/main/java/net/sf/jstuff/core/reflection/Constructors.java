/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.lang.reflect.Constructor;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.reflection.exception.InvokingConstructorFailedException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Constructors extends Members {
   /**
    * @return the constructor or null if the method does not exist
    */
   public static <T> @Nullable Constructor<T> find(final Class<T> clazz, final Class<?> @Nullable... parameterTypes) {
      try {
         return clazz.getDeclaredConstructor(parameterTypes);
      } catch (final NoSuchMethodException e) {
         return null;
      }
   }

   /**
    * @return the constructor or null if the method does not exist
    */

   @SuppressWarnings({"unchecked", "null"})
   public static <T> @Nullable Constructor<T> findCompatible(final Class<T> clazz, final Class<?> @Nullable... parameterTypes) {
      final int parameterTypesLen = parameterTypes == null ? 0 : parameterTypes.length;

      ctor_loop: for (final Constructor<T> ctor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {

         final Class<?>[] ctorParamTypes = ctor.getParameterTypes();

         if (ctorParamTypes.length != parameterTypesLen) {
            continue;
         }
         if (parameterTypesLen == 0 || parameterTypes == null)
            return ctor;

         for (int i = 0; i < parameterTypesLen; i++)
            if (!Types.isAssignableTo(parameterTypes[i], ctorParamTypes[i])) {
               continue ctor_loop;
            }
         return ctor;
      }
      return null;
   }

   /**
    * @return a constructor compatible with the given arguments or null if none was found
    */
   @SuppressWarnings({"unchecked", "null"})
   public static <T> @Nullable Constructor<T> findCompatible(final Class<T> clazz, final Object @Nullable... args) {
      final int argsLen = args == null ? 0 : args.length;

      ctor_loop: for (final Constructor<T> ctor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {

         final Class<?>[] ctorParamTypes = ctor.getParameterTypes();

         if (ctorParamTypes.length != argsLen) {
            continue;
         }
         if (argsLen == 0 || args == null)
            return ctor;

         for (int i = 0; i < argsLen; i++)
            if (args[i] == null) {
               // if arg is null and ctor param is a primitive we have no match since primitives cannot be assigned null
               if (ctorParamTypes[i].getClass().isPrimitive()) {
                  continue ctor_loop;
               }
            } else if (!Types.isAssignableTo(args[i].getClass(), ctorParamTypes[i])) {
               continue ctor_loop;
            }
         return ctor;
      }
      return null;
   }

   public static <T> @NonNull T invoke(final Constructor<T> ctor, final Object... args) throws InvokingConstructorFailedException {
      try {
         ctor.trySetAccessible();
         return ctor.newInstance(args);
      } catch (final Exception ex) {
         throw new InvokingConstructorFailedException(ctor, ex);
      }
   }
}
