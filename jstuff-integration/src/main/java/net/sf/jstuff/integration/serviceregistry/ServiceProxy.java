/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry;

import org.eclipse.jdt.annotation.Nullable;

/**
 * Interface implemented by the service instances handed out to services consumers.
 * Service consumers can cast the received service reference to this type to access service meta-data provided by the service registry.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
   @Nullable
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
