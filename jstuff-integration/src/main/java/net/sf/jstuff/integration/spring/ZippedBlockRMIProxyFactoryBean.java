/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * @deprecated as of Spring 5.3 (phasing out serialization-based remoting)
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Deprecated
public class ZippedBlockRMIProxyFactoryBean extends RmiProxyFactoryBean {
   private boolean useCompressedRMISocketFactory = true;

   public ZippedBlockRMIProxyFactoryBean() {
      setLookupStubOnStartup(false);
      setRefreshStubOnConnectFailure(true);
   }

   @Override
   public void afterPropertiesSet() {
      if (useCompressedRMISocketFactory) {
         setRegistryClientSocketFactory(new ZippedBlockRMISocketFactory());
      }

      super.afterPropertiesSet();
   }

   public boolean isUseCompressedRMISocketFactory() {
      return useCompressedRMISocketFactory;
   }

   public void setUseCompressedRMISocketFactory(final boolean useCompressedRMISocketFactory) {
      this.useCompressedRMISocketFactory = useCompressedRMISocketFactory;
   }
}
