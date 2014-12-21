/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.integration.serviceregistry.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.jstuff.core.collection.WeakHashSet;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.serviceregistry.ServiceListener;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;
import net.sf.jstuff.integration.serviceregistry.ServiceUnavailableException;
import net.sf.jstuff.integration.serviceregistry.impl.DefaultServiceRegistry.ServiceEndpointState;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceProxyInvocationHandler<SERVICE_INTERFACE> implements InvocationHandler
{
	private final static Logger LOG = Logger.create();

	protected final Class<SERVICE_INTERFACE> serviceInterface;
	protected final String serviceEndpointId;
	private final ServiceEndpointState serviceEndpointState;

	private final Set<ServiceListener<SERVICE_INTERFACE>> listeners = new WeakHashSet<ServiceListener<SERVICE_INTERFACE>>();
	private final Lock listeners_READ;
	private final Lock listeners_WRITE;

	public DefaultServiceProxyInvocationHandler(final ServiceEndpointState serviceEndpointState, final Class<SERVICE_INTERFACE> serviceInterface)
	{
		Args.notNull("serviceEndpointState", serviceEndpointState);
		Args.notNull("serviceInterface", serviceInterface);

		this.serviceEndpointState = serviceEndpointState;
		this.serviceInterface = serviceInterface;
		serviceEndpointId = serviceEndpointState.getServiceEndpointId();

		final ReadWriteLock lock = new ReentrantReadWriteLock();
		listeners_READ = lock.readLock();
		listeners_WRITE = lock.writeLock();
	}

	protected boolean addServiceListener(final ServiceListener<SERVICE_INTERFACE> listener)
	{
		Args.notNull("listener", listener);

		listeners_WRITE.lock();
		try
		{
			return listeners.add(listener);
		}
		finally
		{
			listeners_WRITE.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	protected SERVICE_INTERFACE getActiveServiceIfCompatible()
	{
		final Object activeService = serviceEndpointState.activeService;
		if (activeService != null && serviceInterface.isAssignableFrom(serviceEndpointState.activeServiceInterface)) //
			return (SERVICE_INTERFACE) activeService;
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
	{
		final String methodName = method.getName();
		if (method.getDeclaringClass() == ServiceProxy.class)
		{
			if ("get".equals(methodName)) return proxy;
			if ("isServiceAvailable".equals(methodName)) return getActiveServiceIfCompatible() != null;
			if ("getServiceEndpointId".equals(methodName)) return serviceEndpointId;
			if ("getServiceInterface".equals(methodName)) return serviceInterface;
			if ("addServiceListener".equals(methodName)) return addServiceListener((ServiceListener<SERVICE_INTERFACE>) args[0]);
			if ("removeServiceListener".equals(methodName)) return removeServiceListener((ServiceListener<SERVICE_INTERFACE>) args[0]);
		}
		else if (method.getDeclaringClass() == ServiceProxyInternal.class)
		{
			if ("onServiceAvailable".equals(methodName))
			{
				onServiceAvailable((ServiceProxy<SERVICE_INTERFACE>) proxy);
				return null;
			}
			if ("onServiceUnavailable".equals(methodName))
			{
				onServiceUnavailable((ServiceProxy<SERVICE_INTERFACE>) proxy);
				return null;
			}
		}

		final int methodParamCount = method.getParameterTypes().length;
		if (methodParamCount == 0)
		{
			if ("hashCode".equals(methodName)) return hashCode();
			if ("toString".equals(methodName)) return ServiceProxy.class.getSimpleName() //
					+ "[serviceEndpointId=" + serviceEndpointId //
					+ ", serviceInterface=" + serviceInterface //
					+ ", service=" + getActiveServiceIfCompatible() + "]";
		}
		else if (methodParamCount == 1)
		{
			if ("equals".equals(methodName)) return proxy == args[0];
		}

		final Object service = getActiveServiceIfCompatible();
		if (service == null) throw new ServiceUnavailableException(serviceEndpointId, serviceInterface);
		return method.invoke(service, args);
	}

	protected void onServiceAvailable(final ServiceProxy<SERVICE_INTERFACE> service)
	{
		listeners_READ.lock();
		try
		{
			if (getActiveServiceIfCompatible() != null)
			{
				for (final ServiceListener<SERVICE_INTERFACE> listener : listeners)
				{
					try
					{
						listener.onServiceAvailable(service);
					}
					catch (final Exception ex)
					{
						LOG.error(ex, "Failed to notify listener %s", listener);
					}
				}
			}
		}
		finally
		{
			listeners_READ.unlock();
		}
	}

	protected void onServiceUnavailable(final ServiceProxy<SERVICE_INTERFACE> service)
	{
		listeners_READ.lock();
		try
		{
			if (getActiveServiceIfCompatible() == null)
			{
				for (final ServiceListener<SERVICE_INTERFACE> listener : listeners)
				{
					try
					{
						listener.onServiceUnavailable(service);
					}
					catch (final Exception ex)
					{
						LOG.error(ex, "Failed to notify listener %s", listener);
					}
				}
			}
		}
		finally
		{
			listeners_READ.unlock();
		}
	}

	protected boolean removeServiceListener(final ServiceListener<SERVICE_INTERFACE> listener)
	{
		Args.notNull("listener", listener);

		listeners_WRITE.lock();
		try
		{
			return listeners.remove(listener);
		}
		finally
		{
			listeners_WRITE.unlock();
		}
	}
}