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
package net.sf.jstuff.core.reflection.visitor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultClassVisitor implements ClassVisitor {
    public boolean isVisiting(final Class<?> clazz) {
        return true;
    }

    public boolean isVisitingField(final Field field) {
        return true;
    }

    public boolean isVisitingFields(final Class<?> clazz) {
        return true;
    }

    public boolean isVisitingInterfaces(final Class<?> clazz) {
        return true;
    }

    public boolean isVisitingMethod(final Method method) {
        return true;
    }

    public boolean isVisitingMethods(final Class<?> clazz) {
        return true;
    }

    public boolean isVisitingSuperclass(final Class<?> clazz) {
        return true;
    }

    public boolean visit(final Class<?> clazz) {
        return true;
    }

    public boolean visit(final Field field) {
        return true;
    }

    public boolean visit(final Method method) {
        return true;
    }
}
