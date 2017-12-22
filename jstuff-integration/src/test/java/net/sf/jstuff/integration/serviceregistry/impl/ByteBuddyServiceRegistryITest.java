/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.serviceregistry.impl;

import net.sf.jstuff.core.concurrent.Threads;
import net.sf.jstuff.integration.serviceregistry.ServiceProxy;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ByteBuddyServiceRegistryITest extends AbstractServiceRegistryTest<ByteBuddyServiceRegistry> {

    @Override
    protected void setUp() throws Exception {
        registry = new ByteBuddyServiceRegistry();
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
