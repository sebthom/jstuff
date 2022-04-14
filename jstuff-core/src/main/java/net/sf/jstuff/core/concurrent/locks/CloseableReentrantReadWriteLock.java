/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * RWLock whose read/write lock objects that can be used with try-with-resources statement.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class CloseableReentrantReadWriteLock extends ReentrantReadWriteLock {

   public class CloseableReadLock extends ReadLock implements CloseableLock {

      private static final long serialVersionUID = 1L;

      private final ReadLock wrapped;

      protected CloseableReadLock(final ReadLock wrapped) {
         super(CloseableReentrantReadWriteLock.this);
         this.wrapped = wrapped;
      }

      @Override
      public void lock() {
         wrapped.lock();
      }

      @Override
      public CloseableReadLock lockAndGet() {
         wrapped.lock();
         return this;
      }

      @Override
      public void lockInterruptibly() throws InterruptedException {
         wrapped.lockInterruptibly();
      }

      @Override
      public Condition newCondition() {
         return wrapped.newCondition();
      }

      @Override
      public boolean tryLock() {
         return wrapped.tryLock();
      }

      @Override
      public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
         return wrapped.tryLock(timeout, unit);
      }

      @Override
      public void unlock() {
         wrapped.unlock();
      }
   }

   public class CloseableWriteLock extends WriteLock implements CloseableLock {

      private static final long serialVersionUID = 1L;

      private final WriteLock wrapped;

      protected CloseableWriteLock(final WriteLock wrapped) {
         super(CloseableReentrantReadWriteLock.this);
         this.wrapped = wrapped;
      }

      @Override
      public int getHoldCount() {
         return wrapped.getHoldCount();
      }

      @Override
      public boolean isHeldByCurrentThread() {
         return wrapped.isHeldByCurrentThread();
      }

      @Override
      public void lock() {
         wrapped.lock();
      }

      @Override
      public CloseableWriteLock lockAndGet() {
         wrapped.lock();
         return this;
      }

      @Override
      public void lockInterruptibly() throws InterruptedException {
         wrapped.lockInterruptibly();
      }

      @Override
      public Condition newCondition() {
         return wrapped.newCondition();
      }

      @Override
      public boolean tryLock() {
         return wrapped.tryLock();
      }

      @Override
      public boolean tryLock(final long timeout, final TimeUnit unit) throws InterruptedException {
         return wrapped.tryLock(timeout, unit);
      }

      @Override
      public void unlock() {
         wrapped.unlock();
      }
   }

   private static final long serialVersionUID = 1L;

   private final CloseableReadLock readLock;
   private final CloseableWriteLock writeLock;

   public CloseableReentrantReadWriteLock() {
      this(false);
   }

   public CloseableReentrantReadWriteLock(final boolean fair) {
      super(fair);
      readLock = new CloseableReadLock(super.readLock());
      writeLock = new CloseableWriteLock(super.writeLock());
   }

   /**
    * Locks the readLock and returns it.
    */
   @SuppressWarnings("resource")
   public CloseableReadLock lockReadLock() {
      readLock.lockAndGet();
      return readLock;
   }

   /**
    * Locks the writeLock and returns it.
    */
   @SuppressWarnings("resource")
   public CloseableWriteLock lockWriteLock() {
      writeLock.lockAndGet();
      return writeLock;
   }

   @Override
   public CloseableReadLock readLock() {
      return readLock;
   }

   @Override
   public CloseableWriteLock writeLock() {
      return writeLock;
   }
}
