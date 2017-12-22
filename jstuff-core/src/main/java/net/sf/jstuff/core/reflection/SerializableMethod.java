/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.core.reflection;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Serializable Wrapper for java.lang.reflect.Method objects since they do not implement Serializable
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SerializableMethod implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.create();

    private static final WeakHashMap<Method, SerializableMethod> CACHE = new WeakHashMap<Method, SerializableMethod>();

    public static SerializableMethod get(final Method method) {
        Args.notNull("method", method);

        /*
         * intentionally the following code is not synchronized
         */
        SerializableMethod sm = CACHE.get(method);
        if (sm == null) {
            sm = new SerializableMethod(method);
            CACHE.put(method, sm);
        }
        return sm;
    }

    private final Class<?> declaringClass;
    private transient Method method;
    private final String name;
    private final Class<?>[] parameterTypes;

    private SerializableMethod(final Method method) {
        this.method = method;
        name = method.getName();
        parameterTypes = method.getParameterTypes();
        declaringClass = method.getDeclaringClass();
    }

    /**
     * @return the declaringClass
     */
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the parameterTypes
     */
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            method = declaringClass.getDeclaredMethod(name, parameterTypes);
        } catch (final NoSuchMethodException ex) {
            LOG.debug("Unexpected NoSuchMethodException occured.", ex);
            throw new IOException(ex.getMessage());
        }
    }
}
