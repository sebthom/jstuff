/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Comparator;

import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Threads {
   private static final Logger LOG = Logger.create();

   private static final Comparator<Thread> THREAD_PRIORITY_COMPARATOR = new java.util.Comparator<Thread>() {
      @Override
      public int compare(final Thread t1, final Thread t2) {
         return t2.getPriority() - t1.getPriority();
      }
   };

   private static final ThreadMXBean TMX = ManagementFactory.getThreadMXBean();

   private static ThreadGroup rootTG;

   public static Thread[] all() {
      final ThreadGroup root = rootThreadGroup();
      int tmpSize = count() + 1;
      Thread[] tmp;
      int returned = 0;
      do {
         tmp = new Thread[tmpSize];
         returned = root.enumerate(tmp, true);
         tmpSize *= 2;
      }
      while (returned == tmpSize);
      final Thread[] result = new Thread[returned];
      System.arraycopy(tmp, 0, result, 0, returned);
      return result;
   }

   public static Thread[] allSortedByPriority() {
      final Thread[] allThreads = all();
      Arrays.sort(allThreads, THREAD_PRIORITY_COMPARATOR);
      return allThreads;
   }

   public static int count() {
      return TMX.getThreadCount();
   }

   /**
    * @return IDs of monitor deadlocked Threads
    */
   public static long[] deadlockedIDs() {
      final long[] result = TMX.findMonitorDeadlockedThreads();
      if (result == null)
         return ArrayUtils.EMPTY_LONG_ARRAY;
      return result;
   }

   public static void handleInterruptedException(final InterruptedException ex) {
      LOG.error(ex, "InterruptedException caught");
      Thread.currentThread().interrupt();
   }

   /**
    * Handles InterruptedException correctly.
    */
   public static void join(final Thread thread) throws RuntimeInterruptedException {
      Args.notNull("thread", thread);

      try {
         LOG.trace("Waiting for thread %s...", thread);
         thread.join();
      } catch (final InterruptedException ex) {
         handleInterruptedException(ex);
      }
   }

   public static ThreadGroup rootThreadGroup() {
      if (rootTG != null)
         return rootTG;
      ThreadGroup child = Thread.currentThread().getThreadGroup();
      ThreadGroup parent;
      while ((parent = child.getParent()) != null) {
         child = parent;
      }
      rootTG = child;
      return rootTG;
   }

   /**
    * Handles InterruptedException correctly.
    */
   public static void sleep(final long millis) throws RuntimeInterruptedException {
      try {
         LOG.trace("Sending current thread to sleep for %s ms...", millis);
         Thread.sleep(millis);
      } catch (final InterruptedException ex) {
         handleInterruptedException(ex);
      }
   }

   /**
    * Handles InterruptedException correctly.
    */
   public static void sleep(final long millis, final int nanos) throws RuntimeInterruptedException {
      try {
         LOG.trace("Sending current thread to sleep for %s ms %s nanos...", millis, nanos);
         Thread.sleep(millis, nanos);
      } catch (final InterruptedException ex) {
         handleInterruptedException(ex);
      }
   }

   /**
    * Handles InterruptedException correctly.
    */
   public static void wait(final Object obj, final long millis) throws RuntimeInterruptedException {
      try {
         LOG.trace("Waiting for %s ms...", millis);
         synchronized (obj) {
            obj.wait(millis);
         }
      } catch (final InterruptedException ex) {
         handleInterruptedException(ex);
      }
   }

}
