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
package net.sf.jstuff.core.net;

import java.io.IOException;
import java.net.ServerSocket;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class NetUtilsTest extends TestCase {
    public void testIsKnownHost() {
        assertTrue(NetUtils.isKnownHost("localhost"));
        assertFalse(NetUtils.isKnownHost("qwerwerdfsdfwer"));
    }

    public void testIsPortOpen() throws IOException {
        final ServerSocket serverSocket = new ServerSocket(NetUtils.getAvailableLocalPort());
        assertTrue(NetUtils.isRemotePortOpen(serverSocket.getInetAddress().getCanonicalHostName(), serverSocket.getLocalPort()));

        NetUtils.closeQuietly(serverSocket);
        assertFalse(NetUtils.isRemotePortOpen(serverSocket.getInetAddress().getCanonicalHostName(), serverSocket.getLocalPort()));
    }

    public void testIsReachable() {
        assertTrue(NetUtils.isHostReachable("localhost", 2000));
        assertFalse(NetUtils.isHostReachable("qwerwerdfsdfwer", 2000));
    }
}
