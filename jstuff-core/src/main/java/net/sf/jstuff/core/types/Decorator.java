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
package net.sf.jstuff.core.types;

import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * https://en.wikipedia.org/wiki/Decorator_pattern
 * https://stackoverflow.com/questions/350404/how-do-the-proxy-decorator-adapter-and-bridge-patterns-differ
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Decorator<T> {

    public abstract class Default<T> implements Decorator<T> {
        protected T wrapped;

        protected Default() {
        }

        protected Default(final T wrapped) {
            Args.notNull("wrapped", wrapped);
            this.wrapped = wrapped;
        }

        public T getWrapped() {
            Assert.isTrue(isWrappedGettable(), "Accessing the wrapped object is not allowed.");
            return wrapped;
        }

        public boolean isWrappedGettable() {
            return true;
        }

        public boolean isWrappedSettable() {
            return true;
        }

        public void setWrapped(final T wrapped) {
            Args.notNull("wrapped", wrapped);
            Assert.isTrue(isWrappedSettable(), "Exchanging the wrapped object is not allowed.");
            this.wrapped = wrapped;
        }
    }

    /**
     * @throw {@link IllegalStateException} if getting the wrapped object is disallowed
     */
    T getWrapped();

    boolean isWrappedGettable();

    boolean isWrappedSettable();

    /**
     * @throw {@link IllegalStateException} if setting the wrapped object is disallowed
     */
    void setWrapped(T wrapped);
}
