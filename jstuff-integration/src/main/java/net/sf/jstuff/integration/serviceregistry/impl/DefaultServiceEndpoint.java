/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.serviceregistry.impl;

import org.apache.commons.lang3.ObjectUtils;

import net.sf.jstuff.integration.serviceregistry.ServiceEndpoint;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceEndpoint implements ServiceEndpoint, Comparable<DefaultServiceEndpoint> {
   protected final String serviceEndpointId;
   protected final Class<?> serviceInterface;

   public DefaultServiceEndpoint(final String serviceEndpointId, final Class<?> serviceInterface) {
      this.serviceEndpointId = serviceEndpointId;
      this.serviceInterface = serviceInterface;
   }

   public int compareTo(final DefaultServiceEndpoint other) {
      if (other == null)
         return 1;
      final int res = ObjectUtils.compare(serviceEndpointId, other.serviceEndpointId, false);
      if (res != 0)
         return res;
      return ObjectUtils.compare(serviceInterface.getName(), other.serviceInterface.getName());
   }

   public String getServiceEndpointId() {
      return serviceEndpointId;
   }

   public Class<?> getServiceInterface() {
      return serviceInterface;
   }

   @Override
   public String toString() {
      return ServiceEndpoint.class.getName() + "[serviceEndpointId=" + serviceEndpointId + ", serviceInterface=" + serviceInterface + "]";
   }
}
