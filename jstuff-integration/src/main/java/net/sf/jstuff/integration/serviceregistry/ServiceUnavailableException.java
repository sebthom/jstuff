/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
 * This exception is thrown when a method on a {@link ServiceProxy} object is invoked whose backing service instance is currently not available.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ServiceUnavailableException extends IllegalStateException
{
	private static final long serialVersionUID = 1L;

	private final ServiceEndpoint serviceEndpoint;

	public ServiceUnavailableException(final ServiceEndpoint serviceEndpoint)
	{
		super(serviceEndpoint + " is currently not available.");

		this.serviceEndpoint = serviceEndpoint;
	}

	public ServiceEndpoint getServiceEndpoint()
	{
		return serviceEndpoint;
	}
}
