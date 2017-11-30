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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Enumerations {

    public static boolean contains(final Enumeration<?> en, final Object searchFor) {
        Args.notNull("en", en);
        while (en.hasMoreElements()) {
            final Object elem = en.nextElement();
            if (ObjectUtils.equals(elem, searchFor))
                return true;
        }
        return false;
    }

    public static boolean containsIdentical(final Enumeration<?> en, final Object searchFor) {
        Args.notNull("en", en);
        while (en.hasMoreElements())
            if (searchFor == en.nextElement())
                return true;
        return false;
    }

    public static int size(final Enumeration<?> en) {
        Args.notNull("en", en);
        int size = 0;
        while (en.hasMoreElements()) {
            size++;
            en.nextElement();
        }
        return size;
    }

    public static <T> Iterable<T> toIterable(final Enumeration<T> en) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    public boolean hasNext() {
                        return en == null ? false : en.hasMoreElements();
                    }

                    public T next() {
                        return en.nextElement();
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    public static <T> List<T> toList(final Enumeration<T> en) {
        if (en == null)
            return null;
        final List<T> result = new ArrayList<T>();
        while (en.hasMoreElements()) {
            result.add(en.nextElement());
        }
        return result;
    }

}
