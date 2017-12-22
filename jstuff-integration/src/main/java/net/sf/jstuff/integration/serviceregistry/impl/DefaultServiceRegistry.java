/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.MBeanServer;
import javax.management.ObjectName;

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
public class DefaultServiceRegistry implements ServiceRegistry, DefaultServiceRegistryMBean {
    public final class ServiceEndpointState {
        private final String serviceEndpointId;

        protected Object activeService;
        protected Class<?> activeServiceInterface;

        /**
         * All service proxy instances handed out to service consumers for the given end point and still referenced somewhere in the JVM
         */
        private final WeakHashSet<ServiceProxyInternal<?>> issuedServiceProxiesWeak = WeakHashSet.create();
        private final HashSet<ServiceProxyInternal<?>> issuedServiceProxiesStrong = new HashSet<ServiceProxyInternal<?>>();

        private ServiceEndpointState(final String serviceEndpointId) {
            this.serviceEndpointId = serviceEndpointId;
        }

        private void checkStrongProxies() {
            for (final Iterator<ServiceProxyInternal<?>> it = issuedServiceProxiesStrong.iterator(); it.hasNext();) {
                final ServiceProxyInternal<?> serviceProxy = it.next();
                if (serviceProxy.getListenerCount() == 0) {
                    it.remove();
                    issuedServiceProxiesWeak.add(serviceProxy);
                }
            }
        }

        private <T> ServiceProxyInternal<T> findOrCreateServiceProxy(final Class<T> serviceInterface) {
            ServiceProxyInternal<T> proxy = findServiceProxy(serviceInterface);
            if (proxy == null) {
                proxy = createServiceProxy(this, serviceInterface);
                issuedServiceProxiesWeak.add(proxy);
            }
            return proxy;
        }

        @SuppressWarnings("unchecked")
        private <T> ServiceProxyInternal<T> findServiceProxy(final Class<T> serviceInterface) {
            for (final ServiceProxyInternal<?> serviceProxy : issuedServiceProxiesStrong) {
                if (serviceProxy.getServiceInterface() == serviceInterface) //
                    return (ServiceProxyInternal<T>) serviceProxy;
            }

            for (final ServiceProxyInternal<?> serviceProxy : issuedServiceProxiesWeak) {
                if (serviceProxy.getServiceInterface() == serviceInterface) //
                    return (ServiceProxyInternal<T>) serviceProxy;
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        public <T> T getActiveServiceIfCompatible(final Class<T> serviceInterface) {
            if (activeService != null && serviceInterface.isAssignableFrom(activeService.getClass())) //
                return (T) activeService;
            return null;
        }

        public Class<?> getActiveServiceInterface() {
            return activeServiceInterface;
        }

        public String getServiceEndpointId() {
            return serviceEndpointId;
        }

        public void onListenerAdded(final ServiceProxyInternal<?> proxy) {
            serviceEndpoints_WRITE.lock();
            try {
                issuedServiceProxiesWeak.remove(proxy);
                issuedServiceProxiesStrong.add(proxy);
            } finally {
                serviceEndpoints_WRITE.unlock();
            }
        }

        private void removeActiveService() {
            LOG.info("Removing service:\n  serviceEndpointId : %s\n  serviceInterface  : %s [%s]\n  serviceInstance   : %s [%s]", serviceEndpointId, //
                activeServiceInterface.getName(), toString(activeServiceInterface.getClassLoader()), //
                toString(activeService), toString(activeService.getClass().getClassLoader()));
            activeService = null;
            activeServiceInterface = null;

            for (final ServiceProxyInternal<?> proxy : issuedServiceProxiesStrong) {
                proxy.onServiceUnavailable();
            }
            for (final ServiceProxyInternal<?> proxy : issuedServiceProxiesWeak) {
                proxy.onServiceUnavailable();
            }
        }

        private <SERVICE_INTERFACE> void setActiveService(final Class<SERVICE_INTERFACE> serviceInterface, final SERVICE_INTERFACE service) {
            LOG.info("Registering service:\n  serviceEndpointId : %s\n  serviceInterface  : %s [%s]\n  serviceInstance   : %s [%s]", serviceEndpointId, //
                serviceInterface.getName(), toString(serviceInterface.getClassLoader()), //
                toString(service), toString(service.getClass().getClassLoader()));
            activeServiceInterface = serviceInterface;
            activeService = service;
            findOrCreateServiceProxy(serviceInterface);
            for (final ServiceProxyInternal<?> proxy : issuedServiceProxiesStrong) {
                proxy.onServiceAvailable();
            }
            for (final ServiceProxyInternal<?> proxy : issuedServiceProxiesWeak) {
                proxy.onServiceAvailable();
            }
        }

        private String toString(final Object obj) {
            if (obj == null)
                return "null";
            return obj.getClass().getName() + "@" + System.identityHashCode(obj);
        }
    }

    private static final Logger LOG = Logger.create();

    private final Map<String, ServiceEndpointState> serviceEndpoints = new HashMap<String, ServiceEndpointState>();
    private final Lock serviceEndpoints_READ;

    private final Lock serviceEndpoints_WRITE;

    public DefaultServiceRegistry() {
        LOG.infoNew(this);

        final ReadWriteLock lock = new ReentrantReadWriteLock();
        serviceEndpoints_READ = lock.readLock();
        serviceEndpoints_WRITE = lock.writeLock();
    }

    private void _cleanup() {
        LOG.debug("Cleaning up service endpoints...");
        for (final Iterator<Entry<String, ServiceEndpointState>> it = serviceEndpoints.entrySet().iterator(); it.hasNext();) {
            final Entry<String, ServiceEndpointState> entry = it.next();
            final ServiceEndpointState cfg = entry.getValue();
            if (cfg.activeService == null) {
                cfg.checkStrongProxies();
                if (cfg.issuedServiceProxiesWeak.size() == 0 && cfg.issuedServiceProxiesStrong.size() == 0) {
                    LOG.debug("Purging endpoint config for [%s]", cfg.serviceEndpointId);
                    it.remove();
                }
            }
        }
    }

    @Override
    public <SERVICE_INTERFACE> boolean addService(final Class<SERVICE_INTERFACE> serviceInterface, final SERVICE_INTERFACE serviceInstance)
            throws IllegalArgumentException, IllegalStateException {
        Args.notNull("serviceInterface", serviceInterface);
        Args.notNull("serviceInstance", serviceInstance);

        return addService(serviceInterface.getName(), serviceInterface, serviceInstance);
    }

    @Override
    public <SERVICE_INTERFACE> boolean addService(final String serviceEndpointId, final Class<SERVICE_INTERFACE> serviceInterface,
            final SERVICE_INTERFACE serviceInstance) throws IllegalArgumentException, IllegalStateException {
        Args.notNull("serviceEndpointId", serviceEndpointId);
        Args.notNull("serviceInterface", serviceInterface);
        Args.notNull("serviceInstance", serviceInstance);

        if (!serviceInterface.isInterface())
            throw new IllegalArgumentException("[serviceInterface] must be an interface");
        serviceEndpoints_WRITE.lock();
        try {
            ServiceEndpointState srvConfig = serviceEndpoints.get(serviceEndpointId);
            if (srvConfig == null) {
                srvConfig = new ServiceEndpointState(serviceEndpointId);
                serviceEndpoints.put(serviceEndpointId, srvConfig);
            }

            if (srvConfig.activeService == null) {
                srvConfig.setActiveService(serviceInterface, serviceInstance);

                _cleanup();

                return true;
            }

            if (srvConfig.activeService == serviceInstance)
                return false;

            throw new IllegalStateException("Cannot register service [" + serviceInstance + "] at endpoint [" + serviceEndpointId + "] because service ["
                    + srvConfig.activeService + "] is already registered.");
        } finally {
            serviceEndpoints_WRITE.unlock();
        }
    }

    /**
     * This method is intended for sub-classing
     */
    protected <SERVICE_INTERFACE> ServiceProxyInternal<SERVICE_INTERFACE> createServiceProxy(final ServiceEndpointState serviceEndpointState,
            final Class<SERVICE_INTERFACE> serviceInterface) {
        final DefaultServiceProxyAdvice<SERVICE_INTERFACE> advice = new DefaultServiceProxyAdvice<SERVICE_INTERFACE>(serviceEndpointState, serviceInterface);
        final ServiceProxyInternal<SERVICE_INTERFACE> proxy = Proxies.create(new InvocationHandler() {
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                final String methodName = method.getName();
                if (method.getDeclaringClass() == ServiceProxy.class)
                    return method.invoke(advice, args);
                if (method.getDeclaringClass() == ServiceProxyInternal.class)
                    return method.invoke(advice, args);

                final int methodParamCount = method.getParameterTypes().length;
                if (methodParamCount == 0) {
                    if ("hashCode".equals(methodName))
                        return advice.hashCode();
                    if ("toString".equals(methodName))
                        return advice.toString();
                } else if (methodParamCount == 1) {
                    if ("equals".equals(methodName))
                        return proxy == args[0];
                }

                final Object service = serviceEndpointState.getActiveServiceIfCompatible(serviceInterface);
                if (service == null)
                    throw new ServiceUnavailableException(advice.serviceEndpointId, serviceInterface);
                try {
                    return method.invoke(service, args);
                } catch (final InvocationTargetException ex) {
                    throw ex.getTargetException();
                }
            }
        }, ServiceProxyInternal.class, serviceInterface);
        advice.setProxy(proxy);
        return proxy;
    }

    @Override
    public List<ServiceEndpoint> getActiveServiceEndpoints() {
        serviceEndpoints_READ.lock();
        try {
            final List<ServiceEndpoint> result = new ArrayList<ServiceEndpoint>();
            for (final ServiceEndpointState sep : serviceEndpoints.values()) {
                if (sep.activeService != null) {
                    result.add(new DefaultServiceEndpoint(sep.serviceEndpointId, sep.activeServiceInterface));
                }
            }
            return result;
        } finally {
            serviceEndpoints_READ.unlock();
        }
    }

    @Override
    public <SERVICE_INTERFACE> ServiceProxy<SERVICE_INTERFACE> getService(final String serviceEndpointId, final Class<SERVICE_INTERFACE> serviceInterface) {
        Args.notNull("serviceEndpointId", serviceEndpointId);
        Args.notNull("serviceInterface", serviceInterface);

        serviceEndpoints_READ.lock();
        try {
            final ServiceEndpointState srvConfig = serviceEndpoints.get(serviceEndpointId);
            if (srvConfig != null) {
                final ServiceProxy<SERVICE_INTERFACE> proxy = srvConfig.findServiceProxy(serviceInterface);
                if (proxy != null)
                    return proxy;
            }
        } finally {
            serviceEndpoints_READ.unlock();
        }

        serviceEndpoints_WRITE.lock();
        try {
            ServiceEndpointState srvConfig = serviceEndpoints.get(serviceEndpointId);
            if (srvConfig == null) {
                srvConfig = new ServiceEndpointState(serviceEndpointId);
                serviceEndpoints.put(serviceEndpointId, srvConfig);
            }
            final ServiceProxy<SERVICE_INTERFACE> proxy = srvConfig.findOrCreateServiceProxy(serviceInterface);
            return proxy;
        } finally {
            serviceEndpoints_WRITE.unlock();
        }
    }

    @Override
    public Set<String> getServiceEndpointIds() {
        serviceEndpoints_READ.lock();
        try {
            final TreeSet<String> result = new TreeSet<String>();
            for (final ServiceEndpointState sep : serviceEndpoints.values()) {
                if (sep.activeService != null) {
                    result.add(sep.serviceEndpointId);
                }
            }
            return result;
        } finally {
            serviceEndpoints_READ.unlock();
        }
    }

    /**
     * for testing garbage collection
     */
    protected int getServiceEndpointsCount() {
        return serviceEndpoints.size();
    }

    /**
     * @param mbeanServer if null, the platform mbeanServer is used
     * @param mbeanName if null, an mbean name based on the package and class name of the registry is used
     */
    public void registerAsMBean(MBeanServer mbeanServer, String mbeanName) {
        try {
            if (mbeanName == null) {
                mbeanName = getClass().getPackage().getName() + ":type=" + getClass().getSimpleName();
            }
            final ObjectName mbeanObjectName = new ObjectName(mbeanName);
            LOG.info("Registering MBean %s", mbeanName);
            if (mbeanServer == null) {
                mbeanServer = ManagementFactory.getPlatformMBeanServer();
            }
            mbeanServer.registerMBean(this, mbeanObjectName);
        } catch (final Exception ex) {
            LOG.error(ex);
        }
    }

    @Override
    public boolean removeService(final String serviceEndpointId, final Object serviceInstance) throws IllegalArgumentException {
        Args.notNull("serviceEndpointId", serviceEndpointId);
        Args.notNull("serviceInstance", serviceInstance);

        serviceEndpoints_WRITE.lock();
        try {
            final ServiceEndpointState srvConfig = serviceEndpoints.get(serviceEndpointId);
            if (srvConfig == null)
                return false;

            if (srvConfig.activeService != serviceInstance)
                return false;

            srvConfig.removeActiveService();

            _cleanup();

            return true;
        } finally {
            serviceEndpoints_WRITE.unlock();
        }
    }
}
