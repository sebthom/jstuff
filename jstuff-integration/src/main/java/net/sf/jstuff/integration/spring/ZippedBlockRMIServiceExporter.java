/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.rmi.RmiServiceExporter;

import net.sf.jstuff.core.logging.Logger;

/**
 * @deprecated as of Spring 5.3 (phasing out serialization-based remoting)
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Deprecated
public class ZippedBlockRMIServiceExporter implements InitializingBean, DisposableBean {
   private static final Logger LOG = Logger.create();

   private final RmiServiceExporter normalExporter = new RmiServiceExporter();
   private final RmiServiceExporter compressedExporter = new RmiServiceExporter();
   private boolean serviceNameSet = false;

   public ZippedBlockRMIServiceExporter() {
      compressedExporter.setRegistryPort(1100);

      LOG.infoNew(this);
   }

   @Override
   public void afterPropertiesSet() throws Exception {
      if (!serviceNameSet) {
         setServiceName(normalExporter.getServiceInterface().getSimpleName());
      }

      compressedExporter.setRegistryClientSocketFactory(new ZippedBlockRMISocketFactory());
      compressedExporter.afterPropertiesSet();
      normalExporter.afterPropertiesSet();
   }

   @Override
   public void destroy() throws Exception {
      compressedExporter.destroy();
      normalExporter.destroy();
   }

   /**
    * @param registryPort default is 1100
    */
   public void setRegistryCompressedPort(final int registryPort) {
      compressedExporter.setRegistryPort(registryPort);
   }

   public void setRegistryHost(final String registryHost) {
      compressedExporter.setRegistryHost(registryHost);
      normalExporter.setRegistryHost(registryHost);
   }

   /**
    * @param registryPort default is 1099
    */
   public void setRegistryPort(final int registryPort) {
      normalExporter.setRegistryPort(registryPort);
   }

   public void setService(final Object service) {
      compressedExporter.setService(service);
      normalExporter.setService(service);
   }

   public void setServiceInterface(final Class<?> serviceInterface) {
      compressedExporter.setServiceInterface(serviceInterface);
      normalExporter.setServiceInterface(serviceInterface);
   }

   /**
    * Defaults to the simple class name of the service interface class
    */
   public void setServiceName(final String serviceName) {
      compressedExporter.setServiceName(serviceName);
      normalExporter.setServiceName(serviceName);
      serviceNameSet = true;
   }
}
