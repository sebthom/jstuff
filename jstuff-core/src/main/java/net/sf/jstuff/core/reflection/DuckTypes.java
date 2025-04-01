/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.reflection;

import java.lang.reflect.Method;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.exception.ReflectionException;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class DuckTypes {
   private static final Logger LOG = Logger.create();

   /**
    * Creates a dynamic proxy object of type <code>duckInterface</code> forwarding all method invocations
    * to methods with the same signature on <code>duckLikeObject</code>.
    *
    * @return <code>duckLikeObject</code> if instance of <code>duckInterface</code> or a dynamic proxy object.
    */
   @SuppressWarnings("unchecked")
   public static <T> T duckType(final Object duckLikeObject, final Class<T> duckInterface) {
      final Class<?> duckLikeClass = duckLikeObject.getClass();
      if (duckInterface.isAssignableFrom(duckLikeClass))
         return (T) duckLikeObject;

      LOG.debug("Duck-typing %s to type %s", duckLikeObject, duckInterface);

      return Proxies.create((final Object duckProxy, final Method duckMethod, final @Nullable Object[] args) -> {
         final Method duckLikeMethod = Methods.findPublicCompatible(duckLikeClass, duckMethod.getName(), duckMethod.getParameterTypes());
         if (duckLikeMethod == null || Methods.isAbstract(duckLikeMethod) || !Methods.isPublic(duckLikeMethod))
            throw new ReflectionException("Duck typed object " + duckLikeObject + " does not implement duck method " + duckLikeMethod
                  + ".");

         // the public method might be inaccessible if it was declared on a non-public class
         duckLikeMethod.trySetAccessible();

         // delegate method invocation on duck proxy to duckLikeObject's method
         return duckLikeMethod.invoke(duckLikeObject, args);
      }, duckInterface);
   }

   /**
    * @return true if <code>duckLikeObject</code> implements all public methods declared on <code>duckType</code>
    */
   public static boolean isDuckType(final Object duckLikeObject, final Class<?> duckType) {
      final Class<?> duckLikeClass = duckLikeObject.getClass();
      if (duckType.isAssignableFrom(duckLikeClass))
         return true;
      for (final Method method : duckType.getMethods()) {
         final Method m = Methods.findPublicCompatible(duckLikeClass, method.getName(), method.getParameterTypes());
         if (m == null || Methods.isAbstract(m) || !Methods.isPublic(m))
            return false;
      }
      return true;
   }
}
