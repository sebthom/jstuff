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
package net.sf.jstuff.integration.serviceregistry.impl;

import net.sf.jstuff.integration.serviceregistry.ServiceProxy;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ServiceProxyInternal<T> extends ServiceProxy<T> {
    /**
     * called if any service has been made available for the respective serviceEndpointId
     */
    void onServiceAvailable();

    /**
     * called if a service has been removed from the respective serviceEndpointId
     */
    void onServiceUnavailable();

    int getListenerCount();
}
