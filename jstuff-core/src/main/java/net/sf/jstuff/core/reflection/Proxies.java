/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Proxies {

   @SuppressWarnings("unchecked")
   public static <T> T create(final ClassLoader loader, final InvocationHandler handler, final Class<?>... interfaceTypes) {
      Args.notNull("loader", loader);
      Args.notNull("handler", handler);
      Args.notEmpty("interfaceTypes", interfaceTypes);
      Args.noNulls("interfaceTypes", interfaceTypes);

      return (T) Proxy.newProxyInstance(loader, interfaceTypes, handler);
   }

   @SuppressWarnings("unchecked")
   public static <T> T create(final InvocationHandler handler, final Class<?>... interfaceTypes) {
      Args.notNull("handler", handler);
      Args.notEmpty("interfaceTypes", interfaceTypes);
      Args.noNulls("interfaceTypes", interfaceTypes);

      if (interfaceTypes.length == 1)
         return (T) Proxy.newProxyInstance(interfaceTypes[0].getClassLoader(), interfaceTypes, handler);

      /*
       * determine a class loader that can see all interfaces
       */
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      boolean isGoodCL = Types.isVisible(cl, interfaceTypes);

      if (!isGoodCL && cl != handler.getClass().getClassLoader()) {
         cl = handler.getClass().getClassLoader();
         isGoodCL = Types.isVisible(cl, interfaceTypes);
      }

      if (!isGoodCL) {
         for (final Class<?> iface : interfaceTypes) {
            cl = iface.getClassLoader();
            isGoodCL = Types.isVisible(cl, interfaceTypes);
            if (isGoodCL) {
               break;
            }
         }
      }

      if (isGoodCL) //
         return (T) Proxy.newProxyInstance(cl, interfaceTypes, handler);

      // as a last resort we try the classloader of the current class
      return (T) Proxy.newProxyInstance(Proxies.class.getClassLoader(), interfaceTypes, handler);
   }
}
