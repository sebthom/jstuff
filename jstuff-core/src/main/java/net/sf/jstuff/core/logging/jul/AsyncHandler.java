/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.logging.jul;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import net.sf.jstuff.core.collection.tuple.Tuple2;
import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AsyncHandler extends DelegatingHandler {

   public enum State {
      RUNNING,
      CLOSING,
      CLOSED
   }

   private final BlockingQueue<Tuple2<String, LogRecord>> backlog = new LinkedBlockingQueue<>();
   private volatile State state;
   private final Object stateChangeLock = backlog;
   private final Thread thread;

   public AsyncHandler(final Handler handler) {
      super(handler);
      thread = new Thread(() -> {
         final Thread currentThread = Thread.currentThread();
         while (true) {
            try {
               final Tuple2<String, LogRecord> entry = backlog.poll(1000, TimeUnit.SECONDS);
               if (entry == null) {
                  if (state == State.CLOSING) {
                     handler.close();
                     synchronized (stateChangeLock) {
                        state = State.CLOSED;
                        break;
                     }
                  }
               } else {
                  currentThread.setName(entry.get1());
                  handler.publish(entry.get2());
                  currentThread.setName("AsyncHandler");
               }
            } catch (final InterruptedException ex) {
               Thread.currentThread().interrupt();
            }
         }
      }, "AsyncHandler");
      thread.start();
      state = State.RUNNING;
   }

   @Override
   public void close() {
      synchronized (stateChangeLock) {
         if (state != State.RUNNING)
            return;
         state = State.CLOSING;
      }
      while (state != State.CLOSED) {
         Threads.sleep(100);
      }
   }

   public void closeAsync() {
      synchronized (stateChangeLock) {
         if (state != State.RUNNING)
            return;
         state = State.CLOSING;
      }
   }

   @Override
   public void flush() {
      if (state != State.CLOSED)
         return;
      wrapped.flush();
   }

   public State getState() {
      return state;
   }

   @Override
   public void publish(final LogRecord entry) {
      if (state != State.RUNNING) {
         reportError("Not in required state [" + State.RUNNING + "] but [" + state + "]", null, ErrorManager.WRITE_FAILURE);
         return;
      }

      if (!isLoggable(entry))
         return;

      entry.getSourceMethodName(); // force execution of inferCaller
      backlog.add(Tuple2.create(Thread.currentThread().getName(), entry));
   }
}
