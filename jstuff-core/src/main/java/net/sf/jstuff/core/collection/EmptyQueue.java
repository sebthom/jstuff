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
package net.sf.jstuff.core.collection;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class EmptyQueue<E> extends AbstractQueue<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Queue<?> INSTANCE = new EmptyQueue<Object>();

    @SuppressWarnings("unchecked")
    public static <T> EmptyQueue<T> get() {
        return (EmptyQueue<T>) INSTANCE;
    }

    private EmptyQueue() {
        super();
    }

    @Override
    public void clear() {
        // nothing to do
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.<E> emptySet().iterator();
    }

    public boolean offer(final E o) {
        return false;
    }

    public E peek() {
        return null;
    }

    public E poll() {
        return null;
    }

    @SuppressWarnings({ "static-method", "unused" })
    private Object readResolve() throws ObjectStreamException {
        return INSTANCE;
    }

    @Override
    public int size() {
        return 0;
    }
}