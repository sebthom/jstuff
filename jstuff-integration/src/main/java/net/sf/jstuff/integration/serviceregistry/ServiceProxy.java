/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
