/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.serviceregistry.impl;

import java.util.Set;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface DefaultServiceRegistryMBean {
   Set<String> getServiceEndpointIds();
}
