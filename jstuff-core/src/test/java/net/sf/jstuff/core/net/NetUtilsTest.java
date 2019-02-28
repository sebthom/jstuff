/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
      //assertFalse(NetUtils.isKnownHost("qwerwerdfsdfwer"));
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
