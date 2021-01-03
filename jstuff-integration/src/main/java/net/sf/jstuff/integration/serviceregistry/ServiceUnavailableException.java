/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.serviceregistry;

/**
 * This exception is thrown when a service method on a {@link ServiceProxy} object is invoked whose backing service instance is currently not available.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServiceUnavailableException extends IllegalStateException {
   private static final long serialVersionUID = 1L;

   private final String serviceEndpointId;
   private final Class<?> serviceInterface;

   public ServiceUnavailableException(final String serviceEndpointId, final Class<?> serviceInterface) {
      super("A service of type [" + serviceInterface.getName() + "] at endpoint [" + serviceEndpointId + "] is currently not available.");
      this.serviceInterface = serviceInterface;
      this.serviceEndpointId = serviceEndpointId;
   }

   public String getServiceEndpointId() {
      return serviceEndpointId;
   }

   public Class<?> getServiceInterface() {
      return serviceInterface;
   }
}
