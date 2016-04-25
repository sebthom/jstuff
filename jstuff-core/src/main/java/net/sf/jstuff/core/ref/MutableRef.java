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
package net.sf.jstuff.core.ref;

import java.io.Serializable;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class MutableRef<T> implements Ref<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private T value;

    public MutableRef() {
        super();
    }

    public MutableRef(final T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public void set(final T value) {
        this.value = value;
    }
}
