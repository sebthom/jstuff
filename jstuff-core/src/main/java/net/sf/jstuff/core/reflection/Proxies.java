/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Proxies {
    @SuppressWarnings("unchecked")
    @Deprecated
    public static <T> T create(final Class<T> interfaceType, final ClassLoader loader, final InvocationHandler handler) {
        return (T) create(loader, handler, interfaceType);
    }

    @Deprecated
    public static <T> T create(final Class<T> interfaceType, final InvocationHandler handler) {
        return create(handler, interfaceType);
    }

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
