/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import static net.sf.jstuff.core.Strings.*;
import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.IOException;
import java.lang.Thread.State;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.lang3.time.FastDateFormat;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.builder.Builder;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.fluent.Fluent;
import net.sf.jstuff.core.functional.ThrowingConsumer;
import net.sf.jstuff.core.io.RuntimeIOException;
import net.sf.jstuff.core.validation.Args;

/**
 * Creates thread-dumps of the current Java process that mimic the jstack output format to be parseable
 * by third-party thread-dump analyzers, e.g. https://jstack.review/
 *
 * <b>Limitations:</b>
 * <ol>
 * <li>Since physical memory addresses of locks, native threads etc are not accessible, surrogate values are
 * used that still allow to correlate threads and lock usage.
 * <li>Native thread priority (os_prio) is only guessed/estimated
 * <li>Non-Java threads are not dumped (e.g. Compiler, GC threads)
 * </ol>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ThreadDumper {

   public interface ThreadDumperBuilder extends Builder<ThreadDumper> {

      /**
       * Print a separate section about deadlocked threads.
       *
       * Default is <code>true</code>
       */
      @Fluent
      @Builder.Property
      ThreadDumperBuilder withDeadlockReport(boolean value);

      /**
       * Default is {@link ThreadDumper#DEFAULT_FOOTER_PRINTER}
       */
      ThreadDumperBuilder withFooterPrinter(ThrowingConsumer<Appendable, Exception> value);

      /**
       * Default is {@link ThreadDumper#DEFAULT_HEADER_PRINTER}
       */
      ThreadDumperBuilder withHeaderPrinter(ThrowingConsumer<Appendable, Exception> value);

      /**
       * Only dump threads that match the given filter.
       */
      @Fluent
      @Builder.Property
      ThreadDumperBuilder withThreadFilter(Predicate<ThreadMeta> value);

      /**
       * Sort threads with the given comparator. Default is {@link ThreadDumper#SORT_THREADS_BY_NAME}
       */
      @Fluent
      @Builder.Property
      ThreadDumperBuilder withThreadSorter(Comparator<ThreadMeta> value);
   }

   public static class ThreadMeta {

      @Nullable
      private WeakReference<Thread> thread;
      private final ThreadInfo threadInfo;

      public ThreadMeta(final ThreadInfo threadInfo) {
         this.threadInfo = threadInfo;
      }

      public int getLockCount() {
         return threadInfo.getLockedMonitors().length + threadInfo.getLockedSynchronizers().length;
      }

      @Nullable
      private Thread getThread() {
         final var thread = this.thread;
         return thread == null ? null : thread.get();
      }

      public long getThreadId() {
         return threadInfo.getThreadId();
      }

      public String getThreadName() {
         return threadInfo.getThreadName();
      }

      public State getThreadState() {
         return threadInfo.getThreadState();
      }

      public boolean isAtObjectWait() {
         final StackTraceElement[] stack = threadInfo.getStackTrace();
         if (stack.length == 0)
            return false;

         return Object.class.getName().equals(stack[0].getClassName()) //
            && "wait".equals(stack[0].getMethodName());
      }

      public boolean isAtThreadSleep() {
         final StackTraceElement[] stack = threadInfo.getStackTrace();
         if (stack.length == 0)
            return false;

         return Thread.class.getName().equals(stack[0].getClassName()) //
            && "sleep".equals(stack[0].getMethodName());
      }

      public boolean isAtUnsafePark() {
         final StackTraceElement[] stack = threadInfo.getStackTrace();
         if (stack.length == 0)
            return false;

         return "sun.misc.Unsafe".equals(stack[0].getClassName()) //
            && "park".equals(stack[0].getMethodName());
      }

      public boolean isInNative() {
         return threadInfo.isInNative();
      }

      public boolean isWaitingForLock() {
         return threadInfo.getLockInfo() != null;
      }

      @Override
      public String toString() {
         return Strings.toString(this, //
            "name", getThreadName(), //
            "id", getThreadId(), //
            "state", getThreadState() //
         );
      }
   }

   public static final Comparator<ThreadMeta> SORT_THREADS_BY_ID = Comparator.comparing(ThreadMeta::getThreadId);
   public static final Comparator<ThreadMeta> SORT_THREADS_BY_NAME = Comparator.comparing(ThreadMeta::getThreadName);
   public static final Comparator<ThreadMeta> SORT_THREADS_BY_STATE_AND_ID = (t1, t2) -> {
      final int stateCmp = t1.getThreadState().compareTo(t2.getThreadState());
      return stateCmp == 0 ? SORT_THREADS_BY_ID.compare(t1, t2) : stateCmp;
   };
   public static final Comparator<ThreadMeta> SORT_THREADS_BY_STATE_AND_NAME = (t1, t2) -> {
      final int stateCmp = t1.getThreadState().compareTo(t2.getThreadState());
      return stateCmp == 0 ? SORT_THREADS_BY_NAME.compare(t1, t2) : stateCmp;
   };

   private static final FastDateFormat TIMESTAMP = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

   public static final ThrowingConsumer<Appendable, Exception> DEFAULT_HEADER_PRINTER = out -> out //
      .append(TIMESTAMP.format(System.currentTimeMillis())).append(NEW_LINE) //
      .append(String.format("Java thread dump %s (%s %s):%s", //
         System.getProperty("java.vm.name"), //
         System.getProperty("java.vm.version"), //
         System.getProperty("java.vm.info"), //
         NEW_LINE));

   public static final ThrowingConsumer<Appendable, Exception> DEFAULT_FOOTER_PRINTER = out -> { /* nothing to do */ };

   public static ThreadDumperBuilder builder() {
      return BuilderFactory.of(ThreadDumperBuilder.class).create();
   }

   protected boolean deadlockReport = true;
   protected Predicate<ThreadMeta> threadFilter = thread -> true;
   protected Comparator<ThreadMeta> threadSorter = SORT_THREADS_BY_NAME;
   protected Consumer<Appendable> headerPrinter = DEFAULT_HEADER_PRINTER;
   protected Consumer<Appendable> footerPrinter = DEFAULT_FOOTER_PRINTER;

   protected ThreadDumper() {
   }

   /**
    * Generates a thread dump report and writes it to <code>out</code>.
    */
   public void dumpThreads(final Appendable out) throws IOException {
      Args.notNull("out", out);

      headerPrinter.accept(out);

      final Map<Long, ThreadMeta> threadsById = getThreadsByThreadId();

      printThreads(out, threadsById);

      if (deadlockReport) {
         printDeadLockReport(out, threadsById);
      }

      footerPrinter.accept(out);
   }

   /**
    * Generates a thread dump report and writes it to <code>out</code>.
    */
   public void dumpThreads(final StringBuffer out) {
      try {
         dumpThreads((Appendable) out);
      } catch (final IOException ex) {
         // should never happen
         throw new RuntimeIOException(ex);
      }
   }

   /**
    * Generates a thread dump report and writes it to <code>out</code>.
    */
   public void dumpThreads(final StringBuilder out) {
      try {
         dumpThreads((Appendable) out);
      } catch (final IOException ex) {
         // should never happen
         throw new RuntimeIOException(ex);
      }
   }

   private String getLockMemoryAddress(final LockInfo lock) {
      // TODO since we have no access to the real lock objects, we use the hashCode to generate an address
      // that can be used by thread dump analyzers for correlation
      return "0x" + Strings.leftPad(Long.toHexString(lock.getIdentityHashCode()), 16, '0');
   }

   private Map<Long, ThreadMeta> getThreadsByThreadId() {
      final var result = new HashMap<Long, ThreadMeta>();

      // get all threads for access to isDeamon/getPriority which are not in ThreadInfo on Java < 9
      final Thread[] threads = Threads.all();

      for (final ThreadInfo threadInfo : ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)) {
         final var td = new ThreadMeta(threadInfo);
         for (final Thread t : threads) {
            if (threadInfo.getThreadId() == t.getId()) {
               td.thread = new WeakReference<>(t);
            }
         }
         result.put(threadInfo.getThreadId(), td);
      }
      return result;
   }

   private void printDeadLockedThread(final Appendable out, final ThreadMeta thread) throws IOException {
      out.append('"').append(thread.getThreadName()).append("\":").append(NEW_LINE);
      final LockInfo lock = asNonNull(thread.threadInfo.getLockInfo());
      if (thread.getThreadState() == State.BLOCKED) {
         // "  waiting to lock monitor 0x000000003cc6e7a8 (object 0x000000066c67fbe8, a java.util.concurrent.locks.ReentrantLock),"
         out.append(String.format("  waiting to lock monitor 0x%s (object 0x%s, a %s),%s", //
            getLockMemoryAddress(lock), //
            Strings.leftPad(Long.toHexString(lock.getIdentityHashCode()), 16, '0'), // TODO surrogate value
            lock.getClassName(), //
            NEW_LINE) //
         );
      } else {
         // "  waiting for ownable synchronizer 0x000000066c67fbf8, (a java.util.concurrent.locks.ReentrantLock$NonfairSync),"
         out.append(String.format("  waiting for ownable synchronizer 0x%s, (a %s),%s", //
            getLockMemoryAddress(lock), //
            lock.getClassName(), //
            NEW_LINE) //
         );
      }
      out.append("  which is held by \"" + thread.threadInfo.getLockOwnerName() + '"').append(NEW_LINE);
   }

   private void printDeadLockReport(final Appendable out, final Map<Long, ThreadMeta> threadsById) throws IOException {

      final long[] deadlockedThreads = Threads.deadlockedIds();
      if (deadlockedThreads.length == 0)
         return;

      final var deadLocks = new HashSet<Tuple2<ThreadMeta, ThreadMeta>>();
      for (final long id : deadlockedThreads) {
         final ThreadMeta thread = threadsById.get(id);
         if (thread == null) {
            continue;
         }
         final ThreadMeta otherThread = threadsById.get(thread.threadInfo.getLockOwnerId());
         if (otherThread == null) {
            continue;
         }

         if (id < thread.threadInfo.getLockOwnerId()) {
            deadLocks.add(Tuple2.create(thread, otherThread));
         } else {
            deadLocks.add(Tuple2.create(otherThread, thread));
         }
      }

      if (deadLocks.isEmpty())
         return;

      for (final Tuple2<ThreadMeta, ThreadMeta> deadLock : deadLocks) {
         out.append(NEW_LINE) //
            .append("Found one Java-level deadlock:").append(NEW_LINE) //
            .append("=============================").append(NEW_LINE);

         printDeadLockedThread(out, deadLock.get1());
         printDeadLockedThread(out, deadLock.get2());

         out.append(NEW_LINE) //
            .append("Java stack information for the threads listed above:").append(NEW_LINE) //
            .append("===================================================").append(NEW_LINE);
         out.append('"' + deadLock.get1().getThreadName() + "\":").append(NEW_LINE);
         printStackTrace(out, deadLock.get1());
         out.append('"' + deadLock.get2().getThreadName() + "\":").append(NEW_LINE);
         printStackTrace(out, deadLock.get2());

      }

      out.append(NEW_LINE) //
         .append("Found " + deadLocks.size() + " deadlock" + (deadLocks.size() == 1 ? "" : "s") + '.').append(NEW_LINE);
   }

   private void printStackTrace(final Appendable out, final ThreadMeta thread) throws IOException {
      final StackTraceElement[] stackTrace = thread.threadInfo.getStackTrace();
      for (int stackTraceDepth = 0; stackTraceDepth < stackTrace.length; stackTraceDepth++) {
         out.append(TAB).append("at ") //
            .append(stackTrace[stackTraceDepth].toString()) //
            .append(NEW_LINE);
         final var lockInfo = thread.threadInfo.getLockInfo();
         if (stackTraceDepth == 0 && lockInfo != null) {
            switch (thread.getThreadState()) {
               case BLOCKED:
                  out.append(String.format("%s- waiting to lock %s%s", TAB, toString(lockInfo), NEW_LINE));
                  break;
               case WAITING:
               case TIMED_WAITING:
                  out.append(String.format("%s- parking to wait for %s%s", TAB, toString(lockInfo), NEW_LINE));
                  break;
               default:
            }
         }

         // print locks acquired via synchronized(lock)
         for (final MonitorInfo monitorInfo : thread.threadInfo.getLockedMonitors()) {
            if (monitorInfo.getLockedStackDepth() == stackTraceDepth) {
               out.append(String.format("%s- locked %s%s", TAB, toString(monitorInfo), NEW_LINE));
            }
         }
      }
   }

   private void printThreads(final Appendable out, final Map<Long, ThreadMeta> threadsById) throws IOException {

      // get threadInfo for access to lock objects etc.
      final var threadsSorted = new TreeSet<>(threadSorter);
      threadsSorted.addAll(threadsById.values());

      for (final ThreadMeta threadMeta : threadsSorted) {

         if (!threadFilter.test(threadMeta)) {
            continue;
         }

         out.append(NEW_LINE);

         // https://github.com/JetBrains/jdk8u_hotspot/blob/9db779113bfae4bb0853a5d13c6114133ada6683/src/share/vm/runtime/thread.cpp#L2863
         out.append('"' + threadMeta.getThreadName() + "\" #" + threadMeta.getThreadId());
         final Thread thread = threadMeta.getThread();
         if (thread != null) {
            if (thread.isDaemon()) {
               out.append(" daemon");
            }
            out.append(" prio=" + thread.getPriority());
            out.append(" os_prio=" + Threads.guessOSThreadPriority(thread));
         }

         // The Address of a C++-level Thread/JavaThread object
         // https://gist.github.com/rednaxelafx/843622#the-address-of-a-c-level-threadjavathread-object
         // https://github.com/JetBrains/jdk8u_hotspot/blob/9db779113bfae4bb0853a5d13c6114133ada6683/src/share/vm/runtime/thread.cpp#L864
         // TODO We simply use the Java Thread ID as hex string for now
         out.append(" tid=0x" + Strings.leftPad(Long.toHexString(threadMeta.getThreadId()), 16, '0'));

         // https://gist.github.com/rednaxelafx/843622#native-thread-id
         // https://github.com/JetBrains/jdk8u_hotspot/blob/9db779113bfae4bb0853a5d13c6114133ada6683/src/share/vm/runtime/osThread.cpp#L44
         // out.append(" nid=0x" + Long.toHexString(threadMeta.getThreadId())); // TODO

         // https://github.com/AdoptOpenJDK/openjdk-jdk11/blob/master/src/jdk.hotspot.agent/share/classes/sun/jvm/hotspot/runtime/ThreadState.java
         final State threadState = threadMeta.getThreadState();
         out.append(' ');
         switch (threadState) {
            case BLOCKED:
               out.append("waiting for monitor entry");
               break;
            case TIMED_WAITING:
            case WAITING:
               if (threadMeta.isAtObjectWait()) {
                  out.append("in Object.wait()");
               } else {
                  out.append("waiting on condition");
               }
               break;
            case NEW:
               out.append("initialized");
               break;
            case TERMINATED:
               out.append("zombie");
               break;
            default:
               out.append(threadState.name().toLowerCase());
         }

         // thread stack memory region https://gist.github.com/rednaxelafx/843622#the-valid-stack-memory-region-of-a-javathread
         if (threadMeta.threadInfo.getStackTrace().length == 0) {
            out.append(" [0x0000000000000000]");
         } else {
            // TODO
         }

         out.append(NEW_LINE);

         out.append("   java.lang.Thread.State: ").append(threadState.name());
         switch (threadState) {
            case BLOCKED:
               if (threadMeta.threadInfo.getLockInfo() != null) {
                  out.append(" (on object monitor)");
               }
               break;
            case TIMED_WAITING:
            case WAITING:
               if (threadMeta.isAtUnsafePark()) {
                  out.append(" (parking)");
               } else if (threadMeta.isAtThreadSleep()) {
                  out.append(" (sleeping)");
               }
               break;
            default:
         }
         out.append(NEW_LINE);

         printStackTrace(out, threadMeta);

         out.append(NEW_LINE) //
            .append("   Locked ownable synchronizers:").append(NEW_LINE);
         // print locks acquired via Lock.lock()
         if (threadMeta.threadInfo.getLockedSynchronizers().length == 0) {
            out.append(TAB).append("- None");
         } else {
            for (final LockInfo lockInfo : threadMeta.threadInfo.getLockedSynchronizers()) {
               out.append(String.format("%s- %s%s", TAB, toString(lockInfo), NEW_LINE));
            }
         }

         out.append(NEW_LINE);
      }
   }

   /**
    * @return "<0x000000066c388ed8> (a java.lang.ref.ReferenceQueue$Lock)"
    */
   private String toString(final LockInfo lock) {
      return "<" + getLockMemoryAddress(lock) + "> (a " + lock.getClassName() + ')';
   }
}
