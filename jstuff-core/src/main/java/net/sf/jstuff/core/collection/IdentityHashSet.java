/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.core.collection;

import java.util.IdentityHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IdentityHashSet<E> extends MapBackedSet<E> implements Cloneable {
    private static final long serialVersionUID = 1L;

    public static <E> IdentityHashSet<E> create() {
        return new IdentityHashSet<E>();
    }

    public static <E> IdentityHashSet<E> create(final int initialCapacity) {
        return new IdentityHashSet<E>(initialCapacity);
    }

    public IdentityHashSet() {
        this(16);
    }

    public IdentityHashSet(final int initialCapacity) {
        super(new IdentityHashMap<E, Boolean>(initialCapacity));
    }

    @Override
    public IdentityHashSet<E> clone() throws CloneNotSupportedException {
        final IdentityHashSet<E> copy = new IdentityHashSet<E>(size());
        copy.addAll(this);
        return copy;
    }
}