/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.spring;

import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
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
