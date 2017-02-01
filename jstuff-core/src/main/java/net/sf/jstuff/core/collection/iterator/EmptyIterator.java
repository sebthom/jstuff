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
package net.sf.jstuff.core.collection.iterator;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EmptyIterator<T> implements Iterator<T>, Serializable {

    private static final long serialVersionUID = 1L;

    static final EmptyIterator<?> INSTANCE = new EmptyIterator<Object>();

    @SuppressWarnings("unchecked")
    public static <T> EmptyIterator<T> get() {
        return (EmptyIterator<T>) INSTANCE;
    }

    private EmptyIterator() {
        super();
    }

    public boolean hasNext() {
        return false;
    }

    public T next() {
        throw new NoSuchElementException();
    }

    @SuppressWarnings({ "static-method", "unused" })
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
