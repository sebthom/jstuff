/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.concurrent;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.fluent.Fluent;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.Proxies;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@ThreadSafe
public class CrossThreadMethodInvoker {

   public interface CrossThreadProxy<T> {
      T get();

      CrossThreadMethodInvoker getCrossThreadMethodInvoker();
   }

   private static final class MethodInvocation {
      final @Nullable Object target;
      final Method method;
      final Object @Nullable [] args;
      volatile @Nullable Object result;
      volatile @Nullable Exception exception;
      final CountDownLatch isDone = new CountDownLatch(1);

      MethodInvocation(final @Nullable Object target, final Method method, final Object @Nullable [] args) {
         this.target = target;
         this.method = method;
         this.args = args;
      }

      void invoke() {
         try {
            result = method.invoke(target, args);
         } catch (final Exception ex) {
            exception = ex;
         } finally {
            isDone.countDown();
         }
      }
   }

   private final Object synchronizer = new Object();

   /**
    * queue of method invocations that shall be executed in another thread
    */
   private final ConcurrentLinkedQueue<MethodInvocation> invocations = new ConcurrentLinkedQueue<>();
   private volatile @Nullable Thread owner;
   private final long timeoutNS;
   private AtomicInteger backgroundThreadCount = new AtomicInteger(Integer.MIN_VALUE);

   public CrossThreadMethodInvoker(final int timeoutMS) {
      timeoutNS = TimeUnit.MILLISECONDS.toNanos(timeoutMS);
   }

   /**
    * signals that one background thread is done.
    */
   public void backgroundThreadDone() {
      ensureStarted();

      backgroundThreadCount.decrementAndGet();
   }

   /**
    * Creates a JDK proxy executing all method in the {@link CrossThreadMethodInvoker}'s owner thread
    */
   public <@NonNull INTERFACE, IMPL extends INTERFACE> CrossThreadProxy<INTERFACE> createProxy(final IMPL target,
         final @NonNull Class<?>... targetInterfaces) {

      return Proxies.create((proxy, method, args) -> {
         if (method.getDeclaringClass() == CrossThreadProxy.class) {
            final String mName = method.getName();
            if ("get".equals(mName))
               return proxy;
            return CrossThreadMethodInvoker.this;
         }
         return CrossThreadMethodInvoker.this.invokeInOwnerThread(target, method, args);
      }, ArrayUtils.add(targetInterfaces, CrossThreadProxy.class));
   }

   /**
    * Creates a JDK proxy executing all method in the {@link CrossThreadMethodInvoker}'s owner thread
    */
   public <@NonNull INTERFACE, IMPL extends INTERFACE> CrossThreadProxy<INTERFACE> createProxy(final IMPL target,
         final Function<@Nullable Object, ?> resultTransformer, final @NonNull Class<?>... targetInterfaces) {

      return Proxies.create(Thread.currentThread().getContextClassLoader(), (proxy, method, args) -> {
         if (method.getDeclaringClass() == CrossThreadProxy.class) {
            final String mName = method.getName();
            if ("get".equals(mName))
               return proxy;
            return CrossThreadMethodInvoker.this;
         }
         return resultTransformer.apply(CrossThreadMethodInvoker.this.invokeInOwnerThread(target, method, args));
      }, ArrayUtils.add(targetInterfaces, CrossThreadProxy.class));
   }

   private void ensureStarted() {
      if (owner == null)
         throw new IllegalStateException(this + " is not started!");
   }

   public @Nullable Thread getOwner() {
      return owner;
   }

   public int getTimeout() {
      return (int) TimeUnit.NANOSECONDS.toMillis(timeoutNS);
   }

   /**
    * performs the given method invocation in the owner thread
    */
   public @Nullable Object invokeInOwnerThread(final @Nullable Object target, final Method method, final Object @Nullable [] args)
         throws Exception {
      if (Thread.currentThread() == owner) //
         return Methods.invoke(target, method, args);

      ensureStarted();

      final var m = new MethodInvocation(target, method, args);
      invocations.add(m);

      if (!m.isDone.await(timeoutNS, TimeUnit.NANOSECONDS)) //
         throw new IllegalStateException("Method invocation timed out. " + method);

      if (m.exception != null)
         throw m.exception;

      return m.result;
   }

   @Fluent
   public CrossThreadMethodInvoker start(final int numberOfBackgroundThreads) {
      synchronized (synchronizer) {
         Assert.isNull(owner, "Already running!");
         backgroundThreadCount = new AtomicInteger(numberOfBackgroundThreads);
         owner = Thread.currentThread();
         invocations.clear();
      }
      return this;
   }

   @Fluent
   public CrossThreadMethodInvoker stop() {
      synchronized (synchronizer) {
         owner = null;
         invocations.clear();
      }
      return this;
   }

   /**
    * Executes the queued method invocations in the current thread until N background threads signal via {@link #backgroundThreadDone()}
    * that they are finished or the {@link #getTimeout()} is reached.
    * N = numberOfBackgroundThreads provided to the {@link #start(int)} method.
    *
    * @return <code>true</code> if all background threads finished within time, <code>false</code> if a timeout occurred
    */
   public boolean waitForBackgroundThreads() {
      synchronized (synchronizer) {
         ensureStarted();

         if (Thread.currentThread() != owner)
            throw new IllegalStateException("Can only be invoked by owning thread " + owner);

         try {
            final long startedAt = System.nanoTime();
            while (backgroundThreadCount.get() > 0 // still background threads alive?
                  && System.nanoTime() - startedAt < timeoutNS) { // timeout not yet reached?
               final MethodInvocation m = invocations.poll();
               if (m != null) {
                  m.invoke();
               }
               Thread.yield();
            }
            return backgroundThreadCount.get() < 1;
         } finally {
            stop();
         }
      }
   }
}
