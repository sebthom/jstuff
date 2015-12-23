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
package net.sf.jstuff.core.ref;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class LazyInitializedRef<T> implements Ref<T> {
    private T value;

    protected abstract T create();

    public final T get() {
        T result = value;
        if (result == null) //
            synchronized (this) // ensure only one thread creates an instance
        {
            result = value;
            if (result == null) //
                // the JVM guarantees, that accessing a final reference will return the referenced object fully initialized
                // therefore we are passing new object instance to the final wrapper and accessing indirectly via it's final field
                value = result = new FinalRef<T>(create()).value;
        }
        return result;
    }
}
