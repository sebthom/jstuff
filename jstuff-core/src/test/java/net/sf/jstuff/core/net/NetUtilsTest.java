/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.net;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;

import org.junit.Test;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NetUtilsTest {

   @Test
   public void testIsKnownHost() {
      assertThat(NetUtils.isKnownHost("localhost")).isTrue();
      //assertThat(NetUtils.isKnownHost("qwerwerdfsdfwer")).isFalse();
   }

   @Test
   public void testIsPortOpen() throws IOException {
      try (ServerSocket serverSocket = new ServerSocket(NetUtils.getAvailableLocalPort())) {
         assertThat(NetUtils.isRemotePortOpen(serverSocket.getInetAddress().getCanonicalHostName(), serverSocket.getLocalPort())).isTrue();

         NetUtils.closeQuietly(serverSocket);
         assertThat(NetUtils.isRemotePortOpen(serverSocket.getInetAddress().getCanonicalHostName(), serverSocket.getLocalPort())).isFalse();
      }
   }

   @Test
   public void testIsReachable() {
      assertThat(NetUtils.isHostReachable("localhost", 2000)).isTrue();
      assertThat(NetUtils.isHostReachable("qwerwerdfsdfwer", 2000)).isFalse();
   }
}
