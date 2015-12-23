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
package net.sf.jstuff.core.comparator;

import java.util.Comparator;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ReverseComparator<T> implements Comparator<T> {
    private final Comparator<T> delegate;

    public static <T> ReverseComparator<T> of(final Comparator<T> delegate) {
        return new ReverseComparator<T>(delegate);
    }

    /**
     * @param delegate the comparator to reverse
     */
    public ReverseComparator(final Comparator<T> delegate) {
        this.delegate = delegate;
    }

    public int compare(final T o1, final T o2) {
        return -delegate.compare(o1, o2);
    }
}