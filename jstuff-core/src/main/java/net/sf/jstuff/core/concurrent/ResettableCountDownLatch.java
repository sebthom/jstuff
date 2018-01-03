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
package net.sf.jstuff.core.concurrent;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import net.sf.jstuff.core.reflection.Fields;
import net.sf.jstuff.core.reflection.Methods;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ResettableCountDownLatch extends CountDownLatch {

    private final AbstractQueuedSynchronizer _sync;
    private final Method _syncSetState;
    private final int count;

    public ResettableCountDownLatch(final int count) {
        super(count);
        this.count = count;
        _sync = Fields.read(this, "sync");
        _syncSetState = Methods.findAny(AbstractQueuedSynchronizer.class, "setState", int.class);
    }

    public void reset() {
        Methods.invoke(_sync, _syncSetState, count);
    }
}
