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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockServerSocket extends ServerSocket {
   public ZippedBlockServerSocket(final int port) throws IOException {
      super(port);
   }

   @Override
   public Socket accept() throws IOException {
      final Socket s = new ZippedBlockSocket();
      implAccept(s);
      return s;
   }
}
