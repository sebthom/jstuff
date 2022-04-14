/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
