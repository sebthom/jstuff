/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ServiceListener<SERVICE_INTERFACE> {
   void onServiceAvailable(ServiceProxy<SERVICE_INTERFACE> service);

   void onServiceUnavailable(ServiceProxy<SERVICE_INTERFACE> service);
}
