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
import java.lang.reflect.Field;
import java.util.WeakHashMap;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Serializable Wrapper for java.lang.reflect.Field objects since they do not implement Serializable
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class SerializableField implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.create();

    private static final WeakHashMap<Field, SerializableField> CACHE = new WeakHashMap<Field, SerializableField>();

    public static SerializableField get(final Field field) {
        Args.notNull("field", field);

        /*
         * intentionally the following code is not synchronized
         */
        SerializableField sm = CACHE.get(field);
        if (sm == null) {
            sm = new SerializableField(field);
            CACHE.put(field, sm);
        }
        return sm;
    }

    private final Class<?> declaringClass;
    private transient Field field;
    private final String name;

    private SerializableField(final Field field) {
        this.field = field;
        name = field.getName();
        declaringClass = field.getDeclaringClass();
    }

    /**
     * @return the declaringClass
     */
    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    /**
     * @return the field
     */
    public Field getField() {
        return field;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        try {
            field = declaringClass.getDeclaredField(name);
        } catch (final NoSuchFieldException ex) {
            LOG.debug("Unexpected NoSuchFieldException occured", ex);
            throw new IOException(ex.getMessage());
        }
    }
}
