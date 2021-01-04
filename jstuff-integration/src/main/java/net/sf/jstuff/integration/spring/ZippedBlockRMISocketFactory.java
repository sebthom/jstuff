/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

import net.sf.jstuff.core.net.ZippedBlockServerSocket;
import net.sf.jstuff.core.net.ZippedBlockSocket;

/**
 * @deprecated as of Spring 5.3 (phasing out serialization-based remoting)
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
@Deprecated
public class ZippedBlockRMISocketFactory implements RMIServerSocketFactory, RMIClientSocketFactory {

   @Override
   public ServerSocket createServerSocket(final int port) throws IOException {
      return new ZippedBlockServerSocket(port);
   }

   @Override
   public Socket createSocket(final String host, final int port) throws IOException {
      return new ZippedBlockSocket(host, port);
   }
}
