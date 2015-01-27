package net.sf.jstuff.integration.serviceregistry.impl;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.jstuff.core.collection.WeakHashSet;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.serviceregistry.ServiceListener;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;
import net.sf.jstuff.integration.serviceregistry.impl.DefaultServiceRegistry.ServiceEndpointState;

public class DefaultServiceProxyAdvice<SERVICE_INTERFACE> implements ServiceProxyInternal<SERVICE_INTERFACE>
{
	private final static Logger LOG = Logger.create();

	protected final Class<SERVICE_INTERFACE> serviceInterface;
	protected final String serviceEndpointId;
	private final ServiceEndpointState serviceEndpointState;

	private final Set<ServiceListener<SERVICE_INTERFACE>> listeners = new WeakHashSet<ServiceListener<SERVICE_INTERFACE>>();
	private final Lock listeners_READ;
	private final Lock listeners_WRITE;

	private ServiceProxy<SERVICE_INTERFACE> proxy;

	public DefaultServiceProxyAdvice(final ServiceEndpointState serviceEndpointState, final Class<SERVICE_INTERFACE> serviceInterface)
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

	public boolean addServiceListener(final ServiceListener<SERVICE_INTERFACE> listener)
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
	public SERVICE_INTERFACE get()
	{
		return (SERVICE_INTERFACE) proxy;
	}

	public String getServiceEndpointId()
	{
		return serviceEndpointId;
	}

	public Class<SERVICE_INTERFACE> getServiceInterface()
	{
		return serviceInterface;
	}

	public boolean isServiceAvailable()
	{
		return serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) != null;
	}

	public void onServiceAvailable()
	{
		listeners_READ.lock();
		try
		{
			if (serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) != null)
			{
				for (final ServiceListener<SERVICE_INTERFACE> listener : listeners)
				{
					try
					{
						listener.onServiceAvailable(proxy);
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

	public void onServiceUnavailable()
	{
		listeners_READ.lock();
		try
		{
			if (serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) == null)
			{
				for (final ServiceListener<SERVICE_INTERFACE> listener : listeners)
				{
					try
					{
						listener.onServiceUnavailable(proxy);
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

	public boolean removeServiceListener(final ServiceListener<SERVICE_INTERFACE> listener)
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

	public void setProxy(final ServiceProxy<SERVICE_INTERFACE> proxy)
	{
		this.proxy = proxy;
	}

	@Override
	public String toString()
	{
		return ServiceProxy.class.getSimpleName() //
				+ "[serviceEndpointId=" + serviceEndpointId //
				+ ", serviceInterface=" + serviceInterface //
				+ ", service=" + serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) + "]";
	}
}
