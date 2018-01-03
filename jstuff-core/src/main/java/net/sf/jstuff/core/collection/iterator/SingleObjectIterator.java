/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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
package net.sf.jstuff.core.collection.iterator;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SingleObjectIterator<T> implements Iterator<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private T item;
    private boolean hasNext = true;

    public SingleObjectIterator(final T item) {
        this.item = item;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public T next() {
        if (hasNext) {
            hasNext = false;
            final T tmp = item;
            // help the gc
            item = null;
            return tmp;
        }
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
