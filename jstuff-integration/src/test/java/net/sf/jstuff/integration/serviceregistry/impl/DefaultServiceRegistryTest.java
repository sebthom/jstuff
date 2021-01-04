/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */

package net.sf.jstuff.integration.serviceregistry.impl;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultServiceRegistryTest extends AbstractServiceRegistryTest<DefaultServiceRegistry> {

   @Before
   public void setUp() throws Exception {
      registry = new DefaultServiceRegistry();
   }

   @Test
   public void testGarbageCollection() {
      assertThat(registry.getServiceEndpointsCount()).isZero();
      @SuppressWarnings("unused")
      final ServiceProxy<Service1> srv1Proxy = registry.getService(Service1.ENDPOINT_ID, Service1.class);
      ServiceProxy<Service2> srv2Proxy = registry.getService(Service2.ENDPOINT_ID, Service2.class);
      assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class)).hasSameHashCodeAs(srv2Proxy);
      assertThat(registry.getServiceEndpointsCount()).isEqualTo(2);
      System.gc();
      Threads.sleep(100);
      assertThat(registry.getServiceEndpointsCount()).isEqualTo(2);

      final int srv2ProxyHashCode = srv2Proxy.hashCode();
      srv2Proxy = null;
      System.gc();
      Threads.sleep(100);

      registry.addService(Service1.class, new DefaultService1()); // this will trigger execution of _cleanup method
      assertThat(registry.getServiceEndpointsCount()).isEqualTo(1);

      registry.addService(Service2.class, new DefaultService2());
      assertThat(registry.getService(Service2.ENDPOINT_ID, Service2.class).hashCode()).isNotEqualTo(srv2ProxyHashCode);
   }
}
