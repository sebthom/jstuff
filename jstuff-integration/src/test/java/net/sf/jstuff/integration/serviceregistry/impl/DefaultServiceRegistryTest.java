/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.serviceregistry.impl;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceRegistryTest extends AbstractServiceRegistryTest<DefaultServiceRegistry> {

   @Override
   protected void setUp() throws Exception {
      registry = new DefaultServiceRegistry();
   }

   public void testGarbageCollection() {
      assertEquals(0, registry.getServiceEndpointsCount());
      @SuppressWarnings("unused")
      final ServiceProxy<Service1> srv1Proxy = registry.getService(Service1.ENDPOINT_ID, Service1.class);
      ServiceProxy<Service2> srv2Proxy = registry.getService(Service2.ENDPOINT_ID, Service2.class);
      final int srv2ProxyHashCode = srv2Proxy.hashCode();
      assertTrue(srv2ProxyHashCode == registry.getService(Service2.ENDPOINT_ID, Service2.class).hashCode());
      assertEquals(2, registry.getServiceEndpointsCount());
      System.gc();
      Threads.sleep(100);
      assertEquals(2, registry.getServiceEndpointsCount());
      srv2Proxy = null;
      System.gc();
      Threads.sleep(100);
      registry.addService(Service1.class, new DefaultService1()); // this will trigger execution of _cleanup method
      assertEquals(1, registry.getServiceEndpointsCount());

      registry.addService(Service2.class, new DefaultService2());
      assertFalse(srv2ProxyHashCode == registry.getService(Service2.ENDPOINT_ID, Service2.class).hashCode());
   }
}
