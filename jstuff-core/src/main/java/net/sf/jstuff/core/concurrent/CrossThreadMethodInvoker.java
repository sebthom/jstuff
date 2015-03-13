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

import net.sf.jstuff.core.reflection.Methods;
import net.sf.jstuff.core.reflection.Proxies;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class CrossThreadMethodInvoker
{
	private static final class MethodInvocation
	{
		final Object target;
		final Method method;
		final Object[] args;
		volatile Object result;
		volatile Exception exception;
		final CountDownLatch isDone = new CountDownLatch(1);

		MethodInvocation(final Object target, final Method method, final Object[] args)
		{
			this.target = target;
			this.method = method;
			this.args = args;
		}

		void invoke()
		{
			try
			{
				result = method.invoke(target, args);
			}
			catch (final Exception ex)
			{
				exception = ex;
			}
			finally
			{
				isDone.countDown();
			}
		}
	}

	/**
	 * queue of method invocations that shall be executed in another thread
	 */
	private final ConcurrentLinkedQueue<MethodInvocation> invocations = new ConcurrentLinkedQueue<MethodInvocation>();
	private final Thread owner = Thread.currentThread();
	private final int timeout;
	private AtomicInteger threadsWorking = new AtomicInteger(Integer.MIN_VALUE);

	public CrossThreadMethodInvoker(final int timeout)
	{
		this.timeout = timeout;
	}

	/**
	 * Creates a JDK proxy executing all method in the {@link CrossThreadMethodInvoker}'s owner thread
	 */
	@SuppressWarnings("unchecked")
	public <T> T createProxy(final T target, final Class< ? >... targetInterfaces)
	{
		return (T) Proxies.create(new InvocationHandler()
			{
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					return CrossThreadMethodInvoker.this.invokeInOwnerThread(target, method, args);
				}
			}, targetInterfaces);
	}

	private void ensureStarted()
	{
		if (threadsWorking.get() == Integer.MIN_VALUE) throw new IllegalStateException(this + " is not started!");
	}

	public Thread getOwner()
	{
		return owner;
	}

	/**
	 * performs the given method invocation in the owner thread
	 */
	public Object invokeInOwnerThread(final Object target, final Method method, final Object[] args) throws Exception
	{
		if (Thread.currentThread() == owner) //
			return Methods.invoke(target, method, args);

		ensureStarted();

		final MethodInvocation m = new MethodInvocation(target, method, args);
		invocations.add(m);

		if (!m.isDone.await(timeout, TimeUnit.MILLISECONDS)) //
			throw new IllegalStateException("Method invocation timed out. " + method);

		if (m.exception != null) throw m.exception;

		return m.result;
	}

	/**
	 * signals that on thread is done
	 */
	public void markThreadDone()
	{
		ensureStarted();

		threadsWorking.decrementAndGet();
	}

	public synchronized void start(final int numberOfBackgroundOfThreads)
	{
		threadsWorking = new AtomicInteger(numberOfBackgroundOfThreads);
		invocations.clear();
	}

	/**
	 * executes the queued method invocations in the current thread and waits for the background threads to finish
	 */
	public synchronized void waitForBackgroundThreads()
	{
		if (owner != Thread.currentThread()) throw new IllegalStateException("Can only be invoked by owning thread " + owner);

		ensureStarted();

		final long startedAt = System.currentTimeMillis();

		while (threadsWorking.get() > 0 && System.currentTimeMillis() - startedAt < timeout)
		{
			final MethodInvocation m = invocations.poll();
			if (m != null) m.invoke();
			Thread.yield();
		}
		threadsWorking.set(Integer.MIN_VALUE);
	}
}