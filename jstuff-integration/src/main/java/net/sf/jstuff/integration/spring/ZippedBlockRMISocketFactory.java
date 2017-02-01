/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
    public ServerSocket createServerSocket(final int port) throws IOException {
        return new ZippedBlockServerSocket(port);
    }

    public Socket createSocket(final String host, final int port) throws IOException {
        return new ZippedBlockSocket(host, port);
    }
}