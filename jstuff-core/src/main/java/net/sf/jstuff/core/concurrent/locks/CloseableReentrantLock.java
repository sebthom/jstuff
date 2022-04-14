/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock object that can be used with try-with-resources statement.
 *
 * <pre>
 * <code>
 * CloseableReentrantLock lock = Locks.tosCloseableLock(new ReentrantLock());
 * try (CloseableReentrantLock locked = lock.lockAndGet()) {
 *   // do stuff
 * }
 * </code>
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CloseableReentrantLock extends ReentrantLock implements CloseableLock {

   private static final long serialVersionUID = 1L;

   public CloseableReentrantLock() {
   }

   public CloseableReentrantLock(final boolean fair) {
      super(fair);
   }
}
