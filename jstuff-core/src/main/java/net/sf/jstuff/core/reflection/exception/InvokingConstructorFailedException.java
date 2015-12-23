/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import java.lang.reflect.Constructor;

import net.sf.jstuff.core.reflection.SerializableConstructor;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class InvokingConstructorFailedException extends ReflectionException {
    private static final long serialVersionUID = 1L;

    private final SerializableConstructor ctor;

    public InvokingConstructorFailedException(final Constructor<?> ctor, final Throwable cause) {
        super("Invoking constructor " + ctor + " failed.", cause);
        this.ctor = SerializableConstructor.get(ctor);
    }

    public Constructor<?> getConstructor() {
        return ctor.getConstructor();
    }
}