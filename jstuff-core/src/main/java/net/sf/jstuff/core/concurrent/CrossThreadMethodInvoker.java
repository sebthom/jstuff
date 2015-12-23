/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.concurrent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jstuff.core.functional.Function;
import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.Proxies;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CrossThreadMethodInvoker {
    public interface CrossThreadProxy<T> {
        CrossThreadMethodInvoker getCrossThreadMethodInvoker();

        T get();
    }

    private static final class MethodInvocation {
        final Object target;
        final Method method;
        final Object[] args;
        volatile Object result;
        volatile Exception exception;
        final CountDownLatch isDone = new CountDownLatch(1);

        MethodInvocation(final Object target, final Method method, final Object[] args) {
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

    /**
     * queue of method invocations that shall be executed in another thread
     */
    private final ConcurrentLinkedQueue<MethodInvocation> invocations = new ConcurrentLinkedQueue<MethodInvocation>();
    private volatile Thread owner;
    private final int timeout;
    private AtomicInteger backgroundThreadCount = new AtomicInteger(Integer.MIN_VALUE);

    public CrossThreadMethodInvoker(final int timeout) {
        this.timeout = timeout;
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
    @SuppressWarnings("unchecked")
    public <INTERFACE, IMPL extends INTERFACE> CrossThreadProxy<INTERFACE> createProxy(final IMPL target, final Class<?>... targetInterfaces) {
        return (CrossThreadProxy<INTERFACE>) Proxies.create(new InvocationHandler() {
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.getDeclaringClass() == CrossThreadProxy.class) {
                    final String mName = method.getName();
                    if ("get".equals(mName))
                        return proxy;
                    return CrossThreadMethodInvoker.this;
                }
                return CrossThreadMethodInvoker.this.invokeInOwnerThread(target, method, args);
            }
        }, ArrayUtils.add(targetInterfaces, CrossThreadProxy.class));
    }

    /**
     * Creates a JDK proxy executing all method in the {@link CrossThreadMethodInvoker}'s owner thread
     */
    @SuppressWarnings("unchecked")
    public <INTERFACE, IMPL extends INTERFACE> CrossThreadProxy<INTERFACE> createProxy(final IMPL target, final Function<Object, Object> resultTransformer,
            final Class<?>... targetInterfaces) {
        return (CrossThreadProxy<INTERFACE>) Proxies.create(Thread.currentThread().getContextClassLoader(), new InvocationHandler() {
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                if (method.getDeclaringClass() == CrossThreadProxy.class) {
                    final String mName = method.getName();
                    if ("get".equals(mName))
                        return proxy;
                    return CrossThreadMethodInvoker.this;
                }
                return resultTransformer.apply(CrossThreadMethodInvoker.this.invokeInOwnerThread(target, method, args));
            }
        }, ArrayUtils.add(targetInterfaces, CrossThreadProxy.class));
    }

    private void ensureStarted() {
        if (owner == null)
            throw new IllegalStateException(this + " is not started!");
    }

    public Thread getOwner() {
        return owner;
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * performs the given method invocation in the owner thread
     */
    public Object invokeInOwnerThread(final Object target, final Method method, final Object[] args) throws Exception {
        if (Thread.currentThread() == owner) //
            return Methods.invoke(target, method, args);

        ensureStarted();

        final MethodInvocation m = new MethodInvocation(target, method, args);
        invocations.add(m);

        if (!m.isDone.await(timeout, TimeUnit.MILLISECONDS)) //
            throw new IllegalStateException("Method invocation timed out. " + method);

        if (m.exception != null)
            throw m.exception;

        return m.result;
    }

    /**
     * @return this
     */
    public synchronized CrossThreadMethodInvoker start(final int numberOfBackgroundThreads) {
        backgroundThreadCount = new AtomicInteger(numberOfBackgroundThreads);
        owner = Thread.currentThread();
        invocations.clear();
        return this;
    }

    public synchronized void stop() {
        owner = null;
        invocations.clear();
    }

    /**
     * Executes the queued method invocations in the current thread until N background threads signal via {@link #backgroundThreadDone()} that they are
     * finished.
     * or the {@link #getTimeout()} is reached. N = numberOfBackgroundThreads provided to the {@link #start(int)} method.
     *
     * @return <code>true</code> if all background threads finished within time, <code>false</code> if a timeout occured
     */
    public synchronized boolean waitForBackgroundThreads() {
        ensureStarted();

        if (owner != Thread.currentThread())
            throw new IllegalStateException("Can only be invoked by owning thread " + owner);

        try {
            final long startedAt = System.currentTimeMillis();
            while (backgroundThreadCount.get() > 0 // still background threads alive?
                    && System.currentTimeMillis() - startedAt < timeout) // timeout not yet reached?
            {
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