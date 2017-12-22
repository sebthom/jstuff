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
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UnmodifiableComparator<T> implements Comparator<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Comparator<T> delegate;

    public static <T> UnmodifiableComparator<T> of(final Comparator<T> delegate) {
        return new UnmodifiableComparator<T>(delegate);
    }

    public UnmodifiableComparator(final Comparator<T> delegate) {
        Args.notNull("delegate", delegate);

        this.delegate = delegate;
    }

    public int compare(final T o1, final T o2) {
        return delegate.compare(o1, o2);
    }
}