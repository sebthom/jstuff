/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.eclipse.jdt.annotation.NonNull;

import net.sf.jstuff.core.types.Composite;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CompositeLock extends Composite.Default<Lock> implements CloseableLock {

   private static final long serialVersionUID = 1L;

   public CompositeLock(final Collection<@NonNull Lock> locks) {
      super(false, locks);
   }

   public CompositeLock(final @NonNull Lock... locks) {
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
      final var locks = (List<Lock>) components;
      for (int i = locks.size(); i-- > 0;) {
         locks.get(i).unlock();
      }
   }
}
