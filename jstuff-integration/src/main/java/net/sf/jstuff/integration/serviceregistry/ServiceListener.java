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
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface ServiceListener<SERVICE_INTERFACE> {
   void onServiceAvailable(ServiceProxy<SERVICE_INTERFACE> service);

   void onServiceUnavailable(ServiceProxy<SERVICE_INTERFACE> service);
}
