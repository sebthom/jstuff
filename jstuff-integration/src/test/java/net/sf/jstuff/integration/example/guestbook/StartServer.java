/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.example.guestbook;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * http://localhost:8080/services/
 * http://localhost:8080/services/guestbook.json
 * http://localhost:8080/services/feed/atom/guestbook.xml
 * http://localhost:8080/services/rest/xml/guestbook?explain
 * http://localhost:8080/services/rest/xml/guestbook?explainAsHTML
 * http://localhost:8080/services/rest/json/guestbook?explain
 * http://localhost:8080/services/rest/json/guestbook?explainAsHTML
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public final class StartServer {

   public static void main(final String[] args) throws Exception {
      final Server server = new Server();

      try (ServerConnector connector = new ServerConnector(server)) {
         connector.setIdleTimeout(10_000); // set timeout to make debugging easier
         connector.setPort(8080);
         server.addConnector(connector);

         final WebAppContext bb = new WebAppContext();
         bb.setServer(server);
         bb.setContextPath("/");
         bb.setWar("src/test/resources/net/sf/jstuff/integration/example/guestbook/webapp");
         server.setHandler(bb);

         System.out.println(">>> STARTING EMBEDDED JETTY SERVER, PRESS ANY KEY TO STOP");
         server.start();
         System.in.read();
         System.out.println(">>> STOPPING EMBEDDED JETTY SERVER");
      } finally {
         server.stop();
         server.join();
      }
   }

   private StartServer() {
   }
}
