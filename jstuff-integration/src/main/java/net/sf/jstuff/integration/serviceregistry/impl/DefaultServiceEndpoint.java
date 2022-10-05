/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.impl;

import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.comparator.StringComparator;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.serviceregistry.ServiceEndpoint;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceEndpoint implements ServiceEndpoint, Comparable<DefaultServiceEndpoint> {
   protected final String serviceEndpointId;
   protected final Class<?> serviceInterface;

   public DefaultServiceEndpoint(final String serviceEndpointId, final Class<?> serviceInterface) {
      Args.notNull("serviceEndpointId", serviceEndpointId);
      Args.notNull("serviceInterface", serviceInterface);

      this.serviceEndpointId = serviceEndpointId;
      this.serviceInterface = serviceInterface;
   }

   @Override
   public int compareTo(final @Nullable DefaultServiceEndpoint other) {
      if (other == null)
         return 1;
      final int res = StringComparator.INSTANCE.compare(serviceEndpointId, other.serviceEndpointId);
      if (res != 0)
         return res;
      return StringComparator.INSTANCE.compare(serviceInterface.getName(), other.serviceInterface.getName());
   }

   @Override
   public boolean equals(final @Nullable Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      final var other = (DefaultServiceEndpoint) obj;
      if (!Objects.equals(serviceEndpointId, other.serviceEndpointId))
         return false;
      return serviceInterface == other.serviceInterface;
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
      result = prime * result + serviceEndpointId.hashCode();
      result = prime * result + serviceInterface.getName().hashCode();
      return result;
   }

   @Override
   public String toString() {
      return ServiceEndpoint.class.getName() + "[serviceEndpointId=" + serviceEndpointId + ", serviceInterface=" + serviceInterface + "]";
   }
}
