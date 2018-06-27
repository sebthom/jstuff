/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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
package net.sf.jstuff.integration.example.guestbook;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class StartServer {

   public static void main(final String[] args) throws Exception { // CHECKSTYLE:IGNORE UncommentedMain
      final Server server = new Server();
      final SocketConnector connector = new SocketConnector();

      // Set some timeout options to make debugging easier.
      connector.setMaxIdleTime(10000);
      connector.setSoLingerTime(-1);
      connector.setPort(8080);
      server.addConnector(connector);

      final WebAppContext bb = new WebAppContext();
      bb.setServer(server);
      bb.setContextPath("/");
      bb.setWar("src/test/resources/net/sf/jstuff/integration/example/guestbook/webapp");
      server.setHandler(bb);

      try {
         System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
         server.start();
         System.in.read();
         System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
         server.stop();
         server.join();
      } catch (final Exception e) {
         e.printStackTrace();
         System.exit(1);
      }
   }

   private StartServer() {
      super();
   }
}
