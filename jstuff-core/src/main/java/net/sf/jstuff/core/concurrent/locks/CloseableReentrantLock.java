/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CloseableReentrantLock extends ReentrantLock implements CloseableLock {

   private static final long serialVersionUID = 1L;

   public CloseableReentrantLock() {
   }

   public CloseableReentrantLock(final boolean fair) {
      super(fair);
   }
}
