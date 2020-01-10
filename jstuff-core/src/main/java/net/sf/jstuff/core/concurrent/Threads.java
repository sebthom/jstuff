/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.concurrent;

import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Comparator;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.SystemUtils;
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

      Thread[] tmp = new Thread[count() + 1];
      while (true) {
         final int enumerated = root.enumerate(tmp, true);
         if (enumerated < tmp.length) {
            final Thread[] result = new Thread[enumerated];
            System.arraycopy(tmp, 0, result, 0, enumerated);
            return result;
         }
         tmp = new Thread[tmp.length + tmp.length / 2 + 1];
      }
   }

   public static Thread[] allSortedByPriority() {
      final Thread[] allThreads = all();
      Arrays.sort(allThreads, THREAD_PRIORITY_COMPARATOR);
      return allThreads;
   }

   /**
    * @return ids of blocked threads
    */
   public static long[] blockedIds() {
      long[] ids = ArrayUtils.EMPTY_LONG_ARRAY;
      for (final Thread t : Threads.all()) {
         if (t.getState() == State.BLOCKED) {
            ids = ArrayUtils.add(ids, t.getId());
         }
      }
      return ids;
   }

   public static int count() {
      return TMX.getThreadCount();
   }

   public static Thread[] deadlocked() {
      final long[] deadlockedIds = deadlockedIds();
      Thread[] result = new Thread[0];
      for (final Thread t : all()) {
         for (final long deadlockedId : deadlockedIds) {
            if (t.getId() == deadlockedId) {
               result = ArrayUtils.add(result, t);
               continue;
            }
         }
      }
      return result;
   }

   /**
    * @return ids of deadlocked threads
    */
   public static long[] deadlockedIds() {
      final long[] result = TMX.findDeadlockedThreads();
      if (result == null)
         return ArrayUtils.EMPTY_LONG_ARRAY;
      return result;
   }

   public static Thread findThreadByName(final String threadName) {
      for (final Thread t : all()) {
         if (Strings.equals(threadName, t.getName()))
            return t;
      }
      return null;
   }

   /**
    * https://stackoverflow.com/questions/297804/how-are-java-thread-priorities-translated-to-an-os-thread-priority
    *
    * @return 1000 + javaPriority if unknown
    */
   public static int guessOSThreadPriority(final Thread thread) {
      final int prio = thread.getPriority();

      if (SystemUtils.IS_OS_WINDOWS) {
         // https://docs.microsoft.com/en-us/windows/win32/procthread/scheduling-priorities
         switch (prio) {
            case 1:
            case 2:
               return -2; // THREAD_PRIORITY_LOWEST
            case 3:
            case 4:
               return -1; // THREAD_PRIORITY_BELOW_NORMAL
            case 5:
            case 6:
               return 0; // THREAD_PRIORITY_NORMAL
            case 7:
            case 8:
               return 1; // THREAD_PRIORITY_ABOVE_NORMAL
            case 9:
            case 10:
               return 2; // THREAD_PRIORITY_HIGHEST
         }
      }

      if (SystemUtils.IS_OS_SOLARIS) {
         switch (prio) {
            case 1:
               return 0;
            case 2:
               return 32;
            case 3:
               return 64;
            case 4:
               return 96;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
               return 127;
         }
      }

      if (SystemUtils.IS_OS_UNIX)
         return 5 - prio;

      return 1_000 + prio;
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
