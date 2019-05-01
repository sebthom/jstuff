/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.spring;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;

import net.sf.jstuff.core.net.ZippedBlockServerSocket;
import net.sf.jstuff.core.net.ZippedBlockSocket;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
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
