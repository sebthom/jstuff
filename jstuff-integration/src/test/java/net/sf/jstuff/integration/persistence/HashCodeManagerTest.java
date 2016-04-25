/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.jstuff.integration.persistence;

import junit.framework.TestCase;
import net.sf.jstuff.core.io.SerializationUtils;

public class HashCodeManagerTest extends TestCase {
    public static void testManagedHashCode() throws InterruptedException {
        /*
         * #1 client: new()
         */
        Entity client_e = new Entity().setLabel("client_e");
        final int client_HC = client_e.hashCode();

        assertEquals(0, HashCodeManager.getManagedIdsCount());
        assertEquals(1, HashCodeManager.getManagedTrackingIdsCount());

        /*
         * #2 server: deserialize()
         */
        Entity server_e = SerializationUtils.clone(client_e);
        server_e.setLabel("server_e");
        final int server_HC = server_e.hashCode();

        assertEquals(0, HashCodeManager.getManagedIdsCount());
        assertEquals(1, HashCodeManager.getManagedTrackingIdsCount());

        /*
         * #3 server: persist()
         */
        server_e.setId(22);
        assertEquals(server_e.hashCode(), server_HC);

        assertEquals(1, HashCodeManager.getManagedIdsCount());
        assertEquals(1, HashCodeManager.getManagedTrackingIdsCount());

        /*
         * #4 client: applyId()
         */
        client_e.setId(22);
        assertEquals(server_e.hashCode(), client_HC);

        assertEquals(1, HashCodeManager.getManagedIdsCount());
        assertEquals(1, HashCodeManager.getManagedTrackingIdsCount());

        /*
         * #5 server: loadFromDB()
         */
        Entity server_e2 = new Entity().setLabel("server_e2");
        server_e2.setId(22);
        assertEquals(server_e.hashCode(), server_e2.hashCode());

        assertEquals(1, HashCodeManager.getManagedIdsCount());
        assertEquals(2, HashCodeManager.getManagedTrackingIdsCount());

        /*
         * #6 client: receive()
         */
        Entity client_e2 = SerializationUtils.clone(server_e2);
        client_e2.setLabel("client_e2");
        assertEquals(client_e.hashCode(), client_e2.hashCode());

        assertEquals(1, HashCodeManager.getManagedIdsCount());
        assertEquals(2, HashCodeManager.getManagedTrackingIdsCount());

        client_e = null;
        client_e2 = null;
        server_e = null;
        server_e2 = null;

        System.gc();
        Thread.sleep(500);
        System.gc();
        Thread.sleep(500);

        assertEquals(0, HashCodeManager.getManagedIdsCount());
        assertEquals(0, HashCodeManager.getManagedTrackingIdsCount());

        /*
         * #7 server: loadFromDB()
         */
        final Entity server_e3 = new Entity().setLabel("server_e3");
        server_e3.setId(22);
        assertTrue(server_e3.hashCode() != server_HC);

        /* 
         * #8 client: receive()
         */
        final Entity client_e3 = SerializationUtils.clone(server_e3);
        assertFalse(client_e3.hashCode() == client_HC);

        assertEquals(1, HashCodeManager.getManagedIdsCount());
        assertEquals(1, HashCodeManager.getManagedTrackingIdsCount());
    }
}
