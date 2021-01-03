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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeLock extends Composite.Default<Lock> implements CloseableLock {

   private static final long serialVersionUID = 1L;

   public CompositeLock(final Collection<Lock> locks) {
      super(false, locks);
   }

   public CompositeLock(final Lock... locks) {
      super(false, locks);
   }

   @Override
   public void close() {
      unlock();
   }

   @Override
   public void lock() {
      Locks.lockAll(components);
   }

   public CloseableLock lockAsResource() {
      lock();
      return this;
   }

   @Override
   public void lockInterruptibly() throws InterruptedException {
      Locks.lockInterruptiblyAll(components);
   }

   @Override
   public Condition newCondition() {
      throw new UnsupportedOperationException("This lock does not support conditions");
   }

   @Override
   public boolean tryLock() {
      return Locks.tryLockAll(components);
   }

   @Override
   public boolean tryLock(final long time, final TimeUnit unit) throws InterruptedException {
      return Locks.tryLockAll(time, unit, components);
   }

   @Override
   public void unlock() {
      Locks.unlockAll(CollectionUtils.reverse((List<Lock>) components));
   }
}
