/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.net;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractServer {
   private static final Logger LOG = Logger.create();

   protected final Executor executor;
   protected volatile boolean isRunning;
   protected final int portNumber;
   protected @Nullable ServerSocket socketListener;

   protected AbstractServer(final int portNumber, final int numberOfThreads) {
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
                  final var socketListener = AbstractServer.this.socketListener = new ServerSocket(portNumber);

                  while (true) {
                     @SuppressWarnings("resource")
                     final Socket socket = socketListener.accept();

                     if (isRunning) {
                        executor.execute(() -> {
                           try {
                              handleConnection(socket);
                              socket.close();
                           } catch (final IOException ex) {
                              LOG.error(ex);
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
         asNonNull(socketListener).close();
      }
   }
}
