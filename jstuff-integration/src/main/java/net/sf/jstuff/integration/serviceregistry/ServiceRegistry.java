/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import java.util.Collection;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ServiceRegistry
{
	/**
	 * <i>This method is intended for troubleshooting/debugging purposes only</i>
	 * <p>
	 * @return a snapshot list of all service end points with registered services.
	 */
	Collection<ServiceEndpoint> getActiveServiceEndpoints();

	<SERVICE_INTERFACE> ServiceProxy<SERVICE_INTERFACE> getService(final String serviceEndpointId,
			final Class<SERVICE_INTERFACE> serviceInterface);

	/**
	 * Adds the given service using the fully qualified class name of the serviceInterface as service endpoint id.
	 *
	 * @return <code>true</code> if the serviceInstance was added, <code>false</code> if the serviceInstance was added already
	 *
	 * @throws IllegalStateException if another service was already registered for the given service Id
	 */
	<SERVICE_INTERFACE> boolean addService(final Class<SERVICE_INTERFACE> serviceInterface, final SERVICE_INTERFACE serviceInstance)
			throws IllegalArgumentException, IllegalStateException;

	/**
	 * @return <code>true</code> if the serviceInstance was added, <code>false</code> if the serviceInstance was added already
	 *
	 * @throws IllegalArgumentException if serviceEndpointId == null or serviceInstance == null
	 * @throws IllegalStateException if another service was already registered for the given service Id
	 */
	<SERVICE_INTERFACE> boolean addService(final String serviceEndpointId, final Class<SERVICE_INTERFACE> serviceInterface,
			final SERVICE_INTERFACE serviceInstance) throws IllegalArgumentException, IllegalStateException;

	/**
	 * @return <code>true</code> if the serviceInstance was removed successfully, <code>false</code> if the given serviceInstance was not registered
	 *
	 * @throws IllegalArgumentException if serviceEndpointId == null or serviceInstance == null
	 */
	boolean removeService(final String serviceEndpointId, final Object serviceInstance) throws IllegalArgumentException;
}
