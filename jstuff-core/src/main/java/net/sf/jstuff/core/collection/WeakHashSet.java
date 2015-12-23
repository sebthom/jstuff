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
package net.sf.jstuff.core.collection;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.WeakHashMap;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class WeakHashSet<E> extends MapBackedSet<E> implements Cloneable {
    private static final long serialVersionUID = 1L;

    public static <E> WeakHashSet<E> create() {
        return new WeakHashSet<E>();
    }

    public static <E> WeakHashSet<E> create(final int initialCapacity) {
        return new WeakHashSet<E>(initialCapacity);
    }

    public static <E> WeakHashSet<E> create(final int initialCapacity, final float growthFactor) {
        return new WeakHashSet<E>(initialCapacity, growthFactor);
    }

    public WeakHashSet() {
        this(16, 0.75f);
    }

    public WeakHashSet(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public WeakHashSet(final int initialCapacity, final float growthFactor) {
        super(new WeakHashMap<E, Boolean>(initialCapacity, growthFactor));
    }

    @Override
    protected WeakHashSet<E> clone() {
        final WeakHashSet<E> copy = new WeakHashSet<E>(size());
        copy.addAll(this);
        return copy;
    }

    @SuppressWarnings("static-method")
    private void writeObject(@SuppressWarnings("unused") final ObjectOutputStream oos) throws IOException {
        throw new NotSerializableException();
    }
}
