/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.serviceregistry.impl;

import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;

import net.sf.jstuff.integration.serviceregistry.ServiceEndpoint;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceEndpoint implements ServiceEndpoint, Comparable<DefaultServiceEndpoint> {
   protected final String serviceEndpointId;
   protected final Class<?> serviceInterface;

   public DefaultServiceEndpoint(final String serviceEndpointId, final Class<?> serviceInterface) {
      this.serviceEndpointId = serviceEndpointId;
      this.serviceInterface = serviceInterface;
   }

   @Override
   public int compareTo(final DefaultServiceEndpoint other) {
      if (other == null)
         return 1;
      final int res = ObjectUtils.compare(serviceEndpointId, other.serviceEndpointId, false);
      if (res != 0)
         return res;
      return ObjectUtils.compare(serviceInterface.getName(), other.serviceInterface.getName());
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final DefaultServiceEndpoint other = (DefaultServiceEndpoint) obj;
      if (!Objects.equals(serviceEndpointId, other.serviceEndpointId)) {
         return false;
      }
      if (serviceInterface == null) {
         if (other.serviceInterface != null)
            return false;
      } else if (serviceInterface != other.serviceInterface)
         return false;
      return true;
   }

   @Override
   public String getServiceEndpointId() {
      return serviceEndpointId;
   }

   @Override
   public Class<?> getServiceInterface() {
      return serviceInterface;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (serviceEndpointId == null ? 0 : serviceEndpointId.hashCode());
      result = prime * result + (serviceInterface == null ? 0 : serviceInterface.getName().hashCode());
      return result;
   }

   @Override
   public String toString() {
      return ServiceEndpoint.class.getName() + "[serviceEndpointId=" + serviceEndpointId + ", serviceInterface=" + serviceInterface + "]";
   }
}
