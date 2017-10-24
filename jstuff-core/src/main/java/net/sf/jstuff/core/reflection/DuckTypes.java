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
import java.lang.reflect.Method;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.exception.ReflectionException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
        Args.notNull("duckLikeObject", duckLikeObject);
        Args.notNull("duckInterface", duckInterface);

        final Class<?> duckLikeClass = duckLikeObject.getClass();
        if (duckInterface.isAssignableFrom(duckLikeClass))
            return (T) duckLikeObject;

        LOG.debug("Duck-typing %s to type %s", duckLikeObject, duckInterface);

        return Proxies.create(new InvocationHandler() {
            public Object invoke(final Object duckProxy, final Method duckMethod, final Object[] args) throws Throwable {
                final Method duckLikeMethod = Methods.findPublicCompatible(duckLikeClass, duckMethod.getName(), duckMethod.getParameterTypes());
                if (duckLikeMethod == null || Methods.isAbstract(duckLikeMethod) || !Methods.isPublic(duckLikeMethod))
                    throw new ReflectionException("Duck typed object " + duckLikeObject + " does not implement duck method " + duckLikeMethod + ".");

                // the public method might be inaccessible if it was declared on a non-public class
                Methods.ensureAccessible(duckLikeMethod);

                // delegate method invocation on duck proxy to duckLikeObject's method
                return duckLikeMethod.invoke(duckLikeObject, args);
            }
        }, duckInterface);
    }

    /**
     * @return true if <code>duckLikeObject</code> implements all public methods declared on <code>duckType</code>
     */
    public static boolean isDuckType(final Object duckLikeObject, final Class<?> duckType) {
        Args.notNull("duckLikeObject", duckLikeObject);
        Args.notNull("duckType", duckType);

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