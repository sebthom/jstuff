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

import java.lang.reflect.Constructor;

import net.sf.jstuff.core.reflection.exception.InvokingConstructorFailedException;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Constructors extends Members {
    /**
     * @return the constructor or null if the method does not exist
     */
    public static <T> Constructor<T> find(final Class<T> clazz, final Class<?>... parameterTypes) {
        Args.notNull("clazz", clazz);
        try {
            return clazz.getDeclaredConstructor(parameterTypes);
        } catch (final NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * @return the constructor or null if the method does not exist
     */
    @SuppressWarnings({ "unchecked", "null" })
    public static <T> Constructor<T> findCompatible(final Class<T> clazz, final Class<?>... parameterTypes) {
        Args.notNull("clazz", clazz);
        final int parameterTypesLen = parameterTypes == null ? 0 : parameterTypes.length;

        ctor_loop: for (final Constructor<T> ctor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {

            final Class<?>[] ctorParamTypes = ctor.getParameterTypes();

            if (ctorParamTypes.length != parameterTypesLen)
                continue;
            if (parameterTypesLen == 0)
                return ctor;

            for (int i = 0; i < parameterTypesLen; i++)
                if (!Types.isAssignableTo(parameterTypes[i], ctorParamTypes[i]))
                    continue ctor_loop;
            return ctor;
        }
        return null;
    }

    /**
     * @return a constructor compatible with the given arguments or null if none was found
     */
    @SuppressWarnings({ "unchecked", "null" })
    public static <T> Constructor<T> findCompatible(final Class<T> clazz, final Object... args) {
        Args.notNull("clazz", clazz);
        final int argsLen = args == null ? 0 : args.length;

        ctor_loop: for (final Constructor<T> ctor : (Constructor<T>[]) clazz.getDeclaredConstructors()) {

            final Class<?>[] ctorParamTypes = ctor.getParameterTypes();

            if (ctorParamTypes.length != argsLen)
                continue;
            if (argsLen == 0)
                return ctor;

            for (int i = 0; i < argsLen; i++)
                if (args[i] == null) {
                    // if arg is null and ctor param is a primitive we have no match since primitives cannot be assigned null
                    if (ctorParamTypes[i].getClass().isPrimitive())
                        continue ctor_loop;
                } else if (!Types.isAssignableTo(args[i].getClass(), ctorParamTypes[i]))
                    continue ctor_loop;
            return ctor;
        }
        return null;
    }

    public static <T> T invoke(final Constructor<T> ctor, final Object... args) throws InvokingConstructorFailedException {
        Args.notNull("ctor", ctor);

        try {
            ensureAccessible(ctor);
            return ctor.newInstance(args);
        } catch (final Exception ex) {
            throw new InvokingConstructorFailedException(ctor, ex);
        }
    }
}
