/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry;

import java.util.Collection;

import org.eclipse.jdt.annotation.NonNull;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ServiceRegistry {
   /**
    * <i>This method is intended for troubleshooting/debugging purposes only</i>
    * <p>
    *
    * @return a snapshot list of all service end points with registered services.
    */
   Collection<ServiceEndpoint> getActiveServiceEndpoints();

   <@NonNull SERVICE_INTERFACE> ServiceProxy<SERVICE_INTERFACE> getService(String serviceEndpointId,
      Class<SERVICE_INTERFACE> serviceInterface);

   /**
    * Adds the given service using the fully qualified class name of the serviceInterface as service endpoint id.
    *
    * @return <code>true</code> if the serviceInstance was added, <code>false</code> if the serviceInstance was added already
    *
    * @throws IllegalStateException if another service was already registered for the given service Id
    */
   <@NonNull SERVICE_INTERFACE> boolean addService(Class<SERVICE_INTERFACE> serviceInterface, SERVICE_INTERFACE serviceInstance)
      throws IllegalArgumentException, IllegalStateException;

   /**
    * @return <code>true</code> if the serviceInstance was added, <code>false</code> if the serviceInstance was added already
    *
    * @throws IllegalArgumentException if serviceEndpointId == null or serviceInstance == null
    * @throws IllegalStateException if another service was already registered for the given service Id
    */
   <@NonNull SERVICE_INTERFACE> boolean addService(String serviceEndpointId, Class<SERVICE_INTERFACE> serviceInterface,
      SERVICE_INTERFACE serviceInstance) throws IllegalArgumentException, IllegalStateException;

   /**
    * @return <code>true</code> if the serviceInstance was removed successfully, <code>false</code> if the given serviceInstance was not registered
    *
    * @throws IllegalArgumentException if serviceEndpointId == null or serviceInstance == null
    */
   boolean removeService(String serviceEndpointId, Object serviceInstance) throws IllegalArgumentException;
}
