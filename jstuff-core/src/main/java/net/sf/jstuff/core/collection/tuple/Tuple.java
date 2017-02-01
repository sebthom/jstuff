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
package net.sf.jstuff.core.collection.tuple;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.util.Collections;

import net.sf.jstuff.core.collection.DelegatingList;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Tuple extends DelegatingList<Object> {
    private static final long serialVersionUID = 1L;

    protected Tuple(final Object... items) {
        super(Collections.unmodifiableList(newArrayList(items)));
    }

    /**
     * @param index 0 = first element
     */
    @SuppressWarnings("unchecked")
    public <T> T getTyped(final int index) {
        return (T) get(index);
    }
}
