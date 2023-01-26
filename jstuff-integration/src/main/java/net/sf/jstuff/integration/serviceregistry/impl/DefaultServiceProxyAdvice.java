/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.impl;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.WeakHashSet;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.serviceregistry.ServiceListener;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;
import net.sf.jstuff.integration.serviceregistry.impl.DefaultServiceRegistry.ServiceEndpointState;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceProxyAdvice<SERVICE_INTERFACE> implements ServiceProxyInternal<SERVICE_INTERFACE> {
   private static final Logger LOG = Logger.create();

   protected final Class<SERVICE_INTERFACE> serviceInterface;
   protected final String serviceEndpointId;
   private final ServiceEndpointState serviceEndpointState;

   private final Set<ServiceListener<SERVICE_INTERFACE>> listeners = new WeakHashSet<>();
   private final Lock listeners_READ;
   private final Lock listeners_WRITE;

   private ServiceProxyInternal<SERVICE_INTERFACE> proxy = lazyNonNull();

   public DefaultServiceProxyAdvice(final ServiceEndpointState serviceEndpointState, final Class<SERVICE_INTERFACE> serviceInterface) {
      Args.notNull("serviceEndpointState", serviceEndpointState);
      Args.notNull("serviceInterface", serviceInterface);

      this.serviceEndpointState = serviceEndpointState;
      this.serviceInterface = serviceInterface;
      serviceEndpointId = serviceEndpointState.getServiceEndpointId();

      final var lock = new ReentrantReadWriteLock();
      listeners_READ = lock.readLock();
      listeners_WRITE = lock.writeLock();
   }

   @Override
   public boolean addServiceListener(final ServiceListener<SERVICE_INTERFACE> listener) {
      Args.notNull("listener", listener);

      listeners_WRITE.lock();
      try {
         final boolean rc = listeners.add(listener);
         serviceEndpointState.onListenerAdded(proxy);
         return rc;
      } finally {
         listeners_WRITE.unlock();
      }
   }

   @Override
   @SuppressWarnings("unchecked")
   public SERVICE_INTERFACE get() {
      return (SERVICE_INTERFACE) proxy;
   }

   @Override
   public int getListenerCount() {
      listeners_READ.lock();
      try {
         return listeners.size();
      } finally {
         listeners_READ.unlock();
      }
   }

   @Override
   public String getServiceEndpointId() {
      return serviceEndpointId;
   }

   @Override
   public @Nullable Class<?> getServiceImplementationClass() {
      final Object service = serviceEndpointState.getActiveServiceIfCompatible(serviceInterface);
      return service == null ? null : service.getClass();
   }

   @Override
   public Class<SERVICE_INTERFACE> getServiceInterface() {
      return serviceInterface;
   }

   @Override
   public boolean isServiceAvailable() {
      return serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) != null;
   }

   @Override
   public void onServiceAvailable() {
      if (serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) != null) {
         final ServiceListener<SERVICE_INTERFACE>[] listeners;
         listeners_READ.lock();
         try {
            if (this.listeners.isEmpty())
               return;
            listeners = this.listeners.toArray(new ServiceListener[this.listeners.size()]);
         } finally {
            listeners_READ.unlock();
         }

         for (final ServiceListener<SERVICE_INTERFACE> listener : listeners) {
            try {
               listener.onServiceAvailable(proxy);
            } catch (final Exception ex) {
               LOG.error(ex, "Failed to notify listener %s", listener);
            }
         }
      }
   }

   @Override
   public void onServiceUnavailable() {
      if (serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) == null) {
         final ServiceListener<SERVICE_INTERFACE>[] listeners;
         listeners_READ.lock();
         try {
            if (this.listeners.isEmpty())
               return;
            listeners = this.listeners.toArray(new ServiceListener[this.listeners.size()]);
         } finally {
            listeners_READ.unlock();
         }

         for (final ServiceListener<SERVICE_INTERFACE> listener : listeners) {
            try {
               listener.onServiceUnavailable(proxy);
            } catch (final Exception ex) {
               LOG.error(ex, "Failed to notify listener %s", listener);
            }
         }
      }
   }

   @Override
   public boolean removeServiceListener(final ServiceListener<SERVICE_INTERFACE> listener) {
      Args.notNull("listener", listener);

      listeners_WRITE.lock();
      try {
         return listeners.remove(listener);
      } finally {
         listeners_WRITE.unlock();
      }
   }

   public void setProxy(final ServiceProxyInternal<SERVICE_INTERFACE> proxy) {
      this.proxy = proxy;
   }

   @Override
   public String toString() {
      return ServiceProxy.class.getSimpleName() //
         + "[serviceEndpointId=" + serviceEndpointId //
         + ", serviceInterface=" + serviceInterface //
         + ", service=" + serviceEndpointState.getActiveServiceIfCompatible(serviceInterface) + "]";
   }
}
