/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent.queue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.concurrent.SafeAwait;

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

   private final Object lock = new Object();

   public SortedUniqueIntQueue() {
      this(10);
   }

   public SortedUniqueIntQueue(final int initialCapacity) {
      items = new int[initialCapacity < 0 ? 0 : initialCapacity];
   }

   public void clear() {
      synchronized (lock) {
         count = 0;
         headIndex = -1;
      }
   }

   public boolean contains(final int searchFor) {
      synchronized (lock) {
         if (count == 0)
            return false;
         return indexOf(searchFor) > -1;
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
         items = isInsertAtHead //
               ? new int[] {0, items[0]}
               : new int[] {items[0], 0};
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
      synchronized (lock) {
         return count == 0;
      }
   }

   public boolean isFirstElement(final int value) {
      synchronized (lock) {
         if (count == 0)
            return false;
         return items[headIndex] == value;
      }
   }

   public boolean isNotEmpty() {
      synchronized (lock) {
         return count != 0;
      }
   }

   /**
    * Inserts the given value into the queue in sorted order.
    *
    * @return true if the value was inserted, false if it was already present.
    */
   public boolean offer(final int valueToAdd) {
      synchronized (lock) {
         if (count == 0) {
            ensureCapacity(valueToAdd);
            headIndex = 0;
            items[0] = valueToAdd;
         } else {
            final int lastIndex = headIndex + count - 1;
            if (valueToAdd > items[lastIndex]) {
               ensureCapacity(valueToAdd);
               items[lastIndex + 1] = valueToAdd;
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
            } else {
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
         lock.notifyAll();
         return true;
      }
   }

   public @Nullable Integer peek() {
      synchronized (lock) {
         if (count == 0)
            return null;
         return items[headIndex];
      }
   }

   public int peek(final int valueIfEmpty) {
      synchronized (lock) {
         if (count == 0)
            return valueIfEmpty;
         return items[headIndex];
      }
   }

   public @Nullable Integer poll() {
      synchronized (lock) {
         if (count == 0)
            return null;
         final int val = items[headIndex];
         count--;
         headIndex++;
         return val;
      }
   }

   public int poll(final int valueIfEmpty) {
      synchronized (lock) {
         if (count == 0)
            return valueIfEmpty;
         final int val = items[headIndex];
         count--;
         headIndex++;
         return val;
      }
   }

   /**
    * Retrieves and removes the head of the queue, waiting up to the specified timeout if necessary.
    */
   public @Nullable Integer poll(final long timeout, final TimeUnit unit) throws InterruptedException {
      synchronized (lock) {
         if (count == 0 && !SafeAwait.await(() -> count > 0, lock, unit.toMillis(timeout)))
            return null;

         final int val = items[headIndex];
         count--;
         headIndex++;
         return val;
      }
   }

   public int size() {
      synchronized (lock) {
         return count;
      }
   }

   public int take() throws InterruptedException {
      synchronized (lock) {
         if (count == 0) {
            SafeAwait.await(() -> count > 0, lock);
         }
         final int val = items[headIndex];
         count--;
         headIndex++;
         return val;
      }
   }

   public int[] toArray() {
      synchronized (lock) {
         if (count == 0)
            return ArrayUtils.EMPTY_INT_ARRAY;
         return Arrays.copyOfRange(items, headIndex, headIndex + count);
      }
   }

   @Override
   public String toString() {
      synchronized (lock) {
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
      }
   }
}
