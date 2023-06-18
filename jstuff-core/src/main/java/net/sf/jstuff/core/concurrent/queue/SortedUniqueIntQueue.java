/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.queue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ArrayUtils;

/**
 * A thread-safe unbounded queue of sorted unique primitive int values.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortedUniqueIntQueue {

   private int count = 0;

   /** package visibility for tests */
   int headIndex = -1;

   /** package visibility for tests */
   int[] items;

   private final ReentrantLock lock = new ReentrantLock();
   private final Condition onElementAdded = lock.newCondition();

   public SortedUniqueIntQueue() {
      this(10);
   }

   public SortedUniqueIntQueue(final int initialCapacity) {
      items = new int[initialCapacity < 0 ? 0 : initialCapacity];
   }

   public void clear() {
      final var lock = this.lock;
      lock.lock();
      try {
         count = 0;
         headIndex = -1;
      } finally {
         lock.unlock();
      }
   }

   public boolean contains(final int searchFor) {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return false;
         return indexOf(searchFor) > -1;
      } finally {
         lock.unlock();
      }
   }

   private void ensureCapacity(final int valueToAdd) {
      if (count < items.length)
         return;

      if (items.length == 0) {
         items = new int[1];
         return;
      }

      final boolean isInsertAtHead = valueToAdd < items[headIndex];
      final int newHeadIndex = isInsertAtHead ? 1 : 0;
      if (items.length == 1) {
         items = isInsertAtHead ? new int[] {0, items[0]} : new int[] {items[0], 0};
      } else {
         final int[] oldItems = items;
         items = new int[oldItems.length + (oldItems.length >> 1)];
         System.arraycopy(oldItems, headIndex, items, newHeadIndex, count);
      }
      headIndex = newHeadIndex;
   }

   private int indexOf(final int searchFor) {
      if (count == 0)
         return -1;
      return Arrays.binarySearch(items, headIndex, headIndex + count, searchFor);
   }

   public boolean isEmpty() {
      final var lock = this.lock;
      lock.lock();
      try {
         return count == 0;
      } finally {
         lock.unlock();
      }
   }

   public boolean isFirstElement(final int value) {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return false;
         return items[headIndex] == value;
      } finally {
         lock.unlock();
      }
   }

   public boolean isNotEmpty() {
      final var lock = this.lock;
      lock.lock();
      try {
         return count != 0;
      } finally {
         lock.unlock();
      }
   }

   /**
    * @return true if inserted into the queue and false if already on the queue
    */
   public boolean offer(final int valueToAdd) {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0) {
            ensureCapacity(valueToAdd);
            headIndex = 0;
            items[0] = valueToAdd;
         } else {
            // append to tail?
            final int lastIndex = headIndex + count - 1;
            if (valueToAdd > items[lastIndex]) {
               ensureCapacity(valueToAdd);
               items[lastIndex + 1] = valueToAdd;

               // insert at head?
            } else if (valueToAdd < items[headIndex]) {
               ensureCapacity(valueToAdd);

               if (headIndex > 0) {
                  headIndex--;
                  items[headIndex] = valueToAdd;
               } else {
                  System.arraycopy(items, 0, items, 1, count);
                  headIndex = 0;
                  items[0] = valueToAdd;
               }

               // insert somewhere in the middle of the queue
            } else {
               // already in queue?
               final int idx = indexOf(valueToAdd);
               if (idx > -1)
                  return false;

               ensureCapacity(valueToAdd);
               final int insertAt = -idx - 1;
               System.arraycopy(items, insertAt, items, insertAt + 1, count - (insertAt - headIndex));
               items[insertAt] = valueToAdd;
            }
         }
         count++;
         onElementAdded.signal();
         return true;
      } finally {
         lock.unlock();
      }
   }

   public @Nullable Integer peek() {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return null;
         return items[headIndex];
      } finally {
         lock.unlock();
      }
   }

   public int peek(final int valueIfEmpty) {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return valueIfEmpty;
         return items[headIndex];
      } finally {
         lock.unlock();
      }
   }

   public @Nullable Integer poll() {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return null;
         final int val = items[headIndex];
         if (count == 1) {
            headIndex = -1;
            count = 0;
            return val;
         }
         count--;
         headIndex--;
         return val;
      } finally {
         lock.unlock();
      }
   }

   public int poll(final int valueIfEmpty) {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return valueIfEmpty;
         final int val = items[headIndex];
         if (count == 1) {
            headIndex = -1;
            count = 0;
            return val;
         }
         count--;
         headIndex--;
         return val;
      } finally {
         lock.unlock();
      }
   }

   public @Nullable Integer poll(final long timeout, final TimeUnit unit) throws InterruptedException {
      final var lock = this.lock;
      lock.lockInterruptibly();
      try {
         if (count == 0) {
            headIndex = -1;
            if (!onElementAdded.await(timeout, unit))
               return null;
         }
         final int val = items[headIndex];
         if (count == 1) {
            headIndex = -1;
            count = 0;
            return val;
         }
         count--;
         headIndex++;
         return val;
      } finally {
         lock.unlock();
      }
   }

   public int size() {
      final var lock = this.lock;
      lock.lock();
      try {
         return count;
      } finally {
         lock.unlock();
      }
   }

   public int take() throws InterruptedException {
      final var lock = this.lock;
      lock.lockInterruptibly();
      try {
         if (count == 0) {
            headIndex = -1;
            onElementAdded.await();
         }
         final int val = items[headIndex];
         if (count == 1) {
            headIndex = -1;
            count = 0;
            return val;
         }
         count--;
         headIndex++;
         return val;
      } finally {
         lock.unlock();
      }
   }

   public int[] toArray() {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return ArrayUtils.EMPTY_INT_ARRAY;
         return Arrays.copyOfRange(items, headIndex, headIndex + count);
      } finally {
         lock.unlock();
      }
   }

   @Override
   public String toString() {
      final var lock = this.lock;
      lock.lock();
      try {
         if (count == 0)
            return "[]";
         final var sb = new StringBuilder("[");
         final int lastIndex = headIndex + count - 1;
         for (int i = headIndex; i <= lastIndex; i++) {
            sb.append(items[i]);
            if (i != lastIndex) {
               sb.append(',');
            }
         }
         return sb.append(']').toString();
      } finally {
         lock.unlock();
      }
   }
}
