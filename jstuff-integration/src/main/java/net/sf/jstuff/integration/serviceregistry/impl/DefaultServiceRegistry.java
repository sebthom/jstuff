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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.jstuff.core.collection.WeakHashSet;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Proxies;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.serviceregistry.ServiceEndpoint;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;
import net.sf.jstuff.integration.serviceregistry.ServiceRegistry;
import net.sf.jstuff.integration.serviceregistry.ServiceUnavailableException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceRegistry implements ServiceRegistry, DefaultServiceRegistryMBean
{
	protected final class ServiceEndpointConfig
	{
		protected final String serviceEndpointId;
		protected Object activeService;
		protected Class< ? > activeServiceInterface;

		/**
		 * All service proxy instances handed out to service consumers for the given end point and still referenced somewhere in the JVM
		 */
		private final WeakHashSet<ServiceProxy< ? >> issuedServiceProxies = WeakHashSet.create();

		private ServiceEndpointConfig(final String serviceEndpointId)
		{
			this.serviceEndpointId = serviceEndpointId;
		}

		@SuppressWarnings("unchecked")
		private <SERVICE_INTERFACE> ServiceProxy<SERVICE_INTERFACE> findIssuedServiceProxy(final Class<SERVICE_INTERFACE> serviceInterface)
		{
			for (final ServiceProxy< ? > serviceProxy : issuedServiceProxies)
			{
				if (serviceProxy.getServiceEndpoint().getServiceInterface() == serviceInterface) //
					return (ServiceProxy<SERVICE_INTERFACE>) serviceProxy;
			}
			return null;
		}

		private <SERVICE_INTERFACE> ServiceProxy<SERVICE_INTERFACE> issueServiceProxy(final Class<SERVICE_INTERFACE> serviceInterface)
		{
			ServiceProxy<SERVICE_INTERFACE> proxy = findIssuedServiceProxy(serviceInterface);
			if (proxy == null)
			{
				proxy = createServiceProxy(this, serviceInterface);
				issuedServiceProxies.add(proxy);
			}
			return proxy;
		}

		private <SERVICE_INTERFACE> void setActiveService(final Class<SERVICE_INTERFACE> serviceInterface, final SERVICE_INTERFACE service)
		{
			activeServiceInterface = serviceInterface;
			activeService = service;
			issueServiceProxy(serviceInterface);
		}
	}

	private static final class ServiceProxyInvocationHandler<SERVICE_INTERFACE> implements InvocationHandler
	{
		private final DefaultServiceEndpoint serviceEndpoint;
		private final ServiceEndpointConfig serviceEndpointConfig;

		public ServiceProxyInvocationHandler(final ServiceEndpointConfig serviceEndpointConfig, final Class<SERVICE_INTERFACE> serviceInterface)
		{
			this.serviceEndpointConfig = serviceEndpointConfig;
			this.serviceEndpoint = new DefaultServiceEndpoint(serviceEndpointConfig.serviceEndpointId, serviceInterface);
		}

		private Object getActiveServiceIfCompatible()
		{
			final Object activeService = serviceEndpointConfig.activeService;
			if (activeService != null && serviceEndpoint.serviceInterface.isAssignableFrom(serviceEndpointConfig.activeServiceInterface)) return activeService;
			return null;
		}

		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
		{
			final Object service = getActiveServiceIfCompatible();
			final String methodName = method.getName();
			if (method.getDeclaringClass() == ServiceProxy.class)
			{
				if ("isServiceAvailable".equals(methodName)) return service != null;
				if ("get".equals(methodName)) return proxy;
				if ("getServiceEndpoint".equals(methodName)) return serviceEndpoint;
			}

			final int methodParamCount = method.getParameterTypes().length;
			if (methodParamCount == 0)
			{
				if ("hashCode".equals(methodName)) //
					return hashCode();
				if ("toString".equals(methodName)) //
					return ServiceProxy.class.getName() //
							+ "[serviceEndpointId=" + serviceEndpoint.serviceEndpointId //
							+ ", serviceInterface=" + serviceEndpoint.serviceInterface //
							+ ", service=" + service + "]";
			}
			else if (methodParamCount == 1)
			{
				if ("equals".equals(methodName)) return proxy == args[0];
			}

			if (service == null) throw new ServiceUnavailableException(serviceEndpoint);

			return method.invoke(serviceEndpointConfig.activeService, args);
		}
	}

	private final static Logger LOG = Logger.create();

	private final Map<String, ServiceEndpointConfig> serviceEndpoints = new HashMap<String, ServiceEndpointConfig>();

	private final ReadWriteLock lockRW = new ReentrantReadWriteLock();
	private final Lock lockRead = lockRW.readLock();
	private final Lock lockWrite = lockRW.writeLock();

	public DefaultServiceRegistry()
	{
		LOG.infoNew(this);
	}

	private void _cleanup()
	{
		LOG.debug("Cleaning up service endpoints...");
		for (final Iterator<Entry<String, ServiceEndpointConfig>> it = serviceEndpoints.entrySet().iterator(); it.hasNext();)
		{
			final Entry<String, ServiceEndpointConfig> entry = it.next();
			final ServiceEndpointConfig cfg = entry.getValue();
			if (cfg.activeService == null && cfg.issuedServiceProxies.size() == 0)
			{
				LOG.debug("Purging endpoint config for [%s]", cfg.serviceEndpointId);
				it.remove();
			}
		}
	}

	public <SERVICE_INTERFACE> boolean addService(final Class<SERVICE_INTERFACE> serviceInterface, final SERVICE_INTERFACE serviceInstance)
			throws IllegalArgumentException, IllegalStateException
	{
		Args.notNull("serviceInterface", serviceInterface);
		Args.notNull("serviceInstance", serviceInstance);

		return addService(serviceInterface.getName(), serviceInterface, serviceInstance);
	}

	public <SERVICE_INTERFACE> boolean addService(final String serviceEndpointId, final Class<SERVICE_INTERFACE> serviceInterface,
			final SERVICE_INTERFACE serviceInstance) throws IllegalArgumentException, IllegalStateException
	{
		Args.notNull("serviceEndpointId", serviceEndpointId);
		Args.notNull("serviceInterface", serviceInterface);
		Args.notNull("serviceInstance", serviceInstance);

		if (!serviceInterface.isInterface()) throw new IllegalArgumentException("[serviceInterface] must be an interface");
		lockRead.lock();
		try
		{
			ServiceEndpointConfig srvConfig = serviceEndpoints.get(serviceEndpointId);
			if (srvConfig == null)
			{
				srvConfig = new ServiceEndpointConfig(serviceEndpointId);
				serviceEndpoints.put(serviceEndpointId, srvConfig);
			}

			if (srvConfig.activeService == null)
			{
				LOG.info("Registering service:\n  serviceEndpointId: %s\n  serviceInterface : %s\n  serviceInstance  : %s", serviceEndpointId,
						serviceInterface.getName(), serviceInstance);
				srvConfig.setActiveService(serviceInterface, serviceInstance);

				_cleanup();

				return true;
			}
			if (srvConfig.activeService == serviceInstance) return false;

			throw new IllegalStateException("Cannot register service [" + serviceInstance + "] at endpoint [" + serviceEndpointId + "] because service ["
					+ srvConfig.activeService + "] is already registered.");
		}
		finally
		{
			lockRead.unlock();
		}
	}

	protected <SERVICE_INTERFACE> ServiceProxy<SERVICE_INTERFACE> createServiceProxy(final ServiceEndpointConfig serviceEndpointConfig,
			final Class<SERVICE_INTERFACE> serviceInterface)
	{
		return Proxies.create(new ServiceProxyInvocationHandler<SERVICE_INTERFACE>(serviceEndpointConfig, serviceInterface), ServiceProxy.class,
				serviceInterface);
	}

	public List<ServiceEndpoint> getActiveServiceEndpoints()
	{
		lockRead.lock();
		try
		{
			final List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
			for (final ServiceEndpointConfig sep : serviceEndpoints.values())
			{
				if (sep.activeService != null)
				{
					result.add(new DefaultServiceEndpoint(sep.serviceEndpointId, sep.activeServiceInterface));
				}
			}
			return result;
		}
		finally
		{
			lockRead.unlock();
		}
	}

	public <SERVICE_INTERFACE> ServiceProxy<SERVICE_INTERFACE> getService(final String serviceEndpointId, final Class<SERVICE_INTERFACE> serviceInterface)
	{
		Args.notNull("serviceEndpointId", serviceEndpointId);
		Args.notNull("serviceInterface", serviceInterface);

		lockRead.lock();
		try
		{
			final ServiceEndpointConfig srvConfig = serviceEndpoints.get(serviceEndpointId);
			if (srvConfig != null)
			{
				final ServiceProxy<SERVICE_INTERFACE> proxy = srvConfig.findIssuedServiceProxy(serviceInterface);
				if (proxy != null) return proxy;
			}
		}
		finally
		{
			lockRead.unlock();
		}

		lockWrite.lock();
		try
		{
			ServiceEndpointConfig srvConfig = serviceEndpoints.get(serviceEndpointId);
			if (srvConfig == null)
			{
				srvConfig = new ServiceEndpointConfig(serviceEndpointId);
				serviceEndpoints.put(serviceEndpointId, srvConfig);
			}
			return srvConfig.issueServiceProxy(serviceInterface);
		}
		finally
		{
			lockWrite.unlock();
		}
	}

	public Set<String> getServiceEndpointIds()
	{
		lockRead.lock();
		try
		{
			final TreeSet<String> result = new TreeSet<String>();
			for (final ServiceEndpointConfig sep : serviceEndpoints.values())
			{
				if (sep.activeService != null)
				{
					result.add(sep.serviceEndpointId);
				}
			}
			return result;
		}
		finally
		{
			lockRead.unlock();
		}
	}

	/**
	 * for testing garbage collection
	 */
	protected int getServieEndpointsCount()
	{
		return serviceEndpoints.size();
	}

	public boolean removeService(final String serviceEndpointId, final Object serviceInstance) throws IllegalArgumentException
	{
		Args.notNull("serviceEndpointId", serviceEndpointId);
		Args.notNull("serviceInstance", serviceInstance);

		lockWrite.lock();
		try
		{
			final ServiceEndpointConfig srvConfig = serviceEndpoints.get(serviceEndpointId);
			if (srvConfig == null) return false;

			if (srvConfig.activeService != serviceInstance) return false;

			LOG.info("Removing service:\n  serviceEndpointId: %s\n  serviceInterface : %s\n  serviceInstance  : %s", serviceEndpointId,
					srvConfig.activeServiceInterface.getName(), serviceInstance);
			srvConfig.activeService = null;
			srvConfig.activeServiceInterface = null;

			_cleanup();

			return true;
		}
		finally
		{
			lockWrite.unlock();
		}
	}
}
