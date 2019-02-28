/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.serviceregistry;

/**
 * Interface implemented by the service instances handed out to services consumers.
 * Service consumers can cast the received service reference to this type to access service meta-data provided by the service registry.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ServiceProxy<SERVICE_INTERFACE> {
   /**
    * @return itself casted as the service interface
    */
   SERVICE_INTERFACE get();

   String getServiceEndpointId();

   /**
    * the concrete class of the service instance that has been registered
    */
   Class<?> getServiceImplementationClass();

   Class<SERVICE_INTERFACE> getServiceInterface();

   /**
    * @return true if the represented service is available
    */
   boolean isServiceAvailable();

   /**
    * Adds the given listener with a weak reference
    */
   boolean addServiceListener(ServiceListener<SERVICE_INTERFACE> listener);

   boolean removeServiceListener(ServiceListener<SERVICE_INTERFACE> listener);

}
