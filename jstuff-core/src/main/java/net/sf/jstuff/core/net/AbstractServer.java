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
package net.sf.jstuff.core.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractServer {
   private static final Logger LOG = Logger.create();

   protected final Executor executor;
   protected volatile boolean isRunning;
   protected final int portNumber;
   protected ServerSocket socketListener;

   public AbstractServer(final int portNumber, final int numberOfThreads) {
      this.portNumber = portNumber;
      executor = Executors.newFixedThreadPool(numberOfThreads);
   }

   /**
    * Gets port on which server is listening.
    */
   public int getPortNumber() {
      return portNumber;
   }

   protected abstract void handleConnection(Socket clientConnection) throws IOException;

   /**
    *
    * @return if the server is currently listening to a socket
    */
   public boolean isRunning() {
      return isRunning;
   }

   /**
    * Starts the server in a background thread.
    */
   public synchronized void startServer() {
      if (!isRunning) {
         isRunning = true;

         new Thread() {
            @Override
            public void run() {
               try {
                  socketListener = new ServerSocket(portNumber);

                  while (true) {
                     final Socket socket = socketListener.accept();

                     if (isRunning) {
                        executor.execute(new Runnable() {
                           public void run() {
                              try {
                                 handleConnection(socket);
                                 socket.close();
                              } catch (final IOException ex) {
                                 ex.printStackTrace();
                              }
                           }
                        });
                     } else {
                        socket.close();
                        break;
                     }
                  }
               } catch (final IOException ex) {
                  isRunning = false;
                  LOG.error(ex);
               }
            }
         }.start();
      }
   }

   /**
    * Stops the server from listening to the socket.
    */
   public synchronized void stopServer() throws IOException {
      if (isRunning) {
         isRunning = false;
         socketListener.close();
      }
   }
}
