/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.net;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.junit.Test;

import net.sf.jstuff.core.concurrent.Threads;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class NetUtilsTest {

   @Test
   public void testIsKnownHost() {
      assertThat(NetUtils.isKnownHost("localhost")).isTrue();
      assertThat(NetUtils.isKnownHost("qwerwerdfsdfwer")).isFalse();
   }

   @Test
   public void testIsPortOpen() throws IOException {
      try (ServerSocket serverSocket = new ServerSocket(NetUtils.getAvailableLocalPort(), 5, InetAddress.getLoopbackAddress())) {
         Threads.sleep(100);
         assertThat(NetUtils.isRemotePortOpen(serverSocket.getInetAddress().getCanonicalHostName(), serverSocket.getLocalPort(), 5_000)).isTrue();

         NetUtils.closeQuietly(serverSocket);
         Threads.sleep(1000);
         assertThat(NetUtils.isRemotePortOpen(serverSocket.getInetAddress().getCanonicalHostName(), serverSocket.getLocalPort(), 5_000)).isFalse();
      }
   }

   @Test
   public void testIsReachable() {
      assertThat(NetUtils.isHostReachable("localhost", 2000)).isTrue();
      assertThat(NetUtils.isHostReachable("qwerwerdfsdfwer", 2000)).isFalse();
   }
}
