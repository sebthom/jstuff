/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent.locks;

import java.io.Closeable;
import java.util.concurrent.locks.Lock;

/**
 * Lock object that can be used with try-with-resources statement.
 *
 * <pre>
 * <code>
 * CloseableLock lock = Locks.toCloseableLock(new ReentrantLock());
 * try (CloseableLock locked = lock.lockAndGet()) {
 *   // do stuff
 * }
 * </code>
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface CloseableLock extends Closeable, Lock {

   @Override
   default void close() {
      unlock();
   }

   default CloseableLock lockAndGet() {
      lock();
      return this;
   }
}
