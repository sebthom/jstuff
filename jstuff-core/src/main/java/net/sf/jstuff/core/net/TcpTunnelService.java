package net.sf.jstuff.core.net;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.fluent.Fluent;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class TcpTunnelService extends Thread {

   protected class TcpTunnel extends Thread {

      protected class CopyDataThread extends Thread {

         protected final InputStream in;
         protected final OutputStream out;

         protected CopyDataThread(final InputStream in, final OutputStream out) {
            this.in = in;
            this.out = out;
         }

         @Override
         public void run() {
            try {
               final var buff = new byte[32 * 1024];
               while (!interrupted()) {
                  try {
                     final int bytesRead = in.read(buff);
                     if (bytesRead == -1) {
                        break;
                     }
                     out.write(buff, 0, bytesRead);
                  } catch (final SocketTimeoutException ex) {
                     LOG.warn(ex);
                  }
               }
            } catch (final IOException ex) {
               // ignore
            }

            onConnectionBroken();
         }
      }

      protected final Socket clientSocket;
      protected final Socket targetSocket;
      protected final String tunnelName;

      protected TcpTunnel(final Socket clientSocket) throws SocketException {
         this.clientSocket = clientSocket;
         tunnelName = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " > " //
            + clientSocket.getLocalAddress().getHostName() + ":" + clientSocket.getLocalPort() + " > " //
            + (Strings.isBlank(proxyAddress) ? "" : proxyAddress + ":" + proxyPort + " > ") //
            + targetAddress + ":" + targetPort;

         if (Strings.isBlank(proxyAddress)) {
            targetSocket = new Socket();
         } else {
            targetSocket = new Socket(new Proxy(proxyType, new InetSocketAddress(proxyAddress, proxyPort)));
         }
         targetSocket.setKeepAlive(false);
         targetSocket.setReuseAddress(true);
         targetSocket.setTcpNoDelay(true);

         // throw a SocketTimeoutException every five seconds,
         // prevents potential endless blocking and allows checking if thread was interrupted
         targetSocket.setSoTimeout(5_000);
      }

      protected synchronized void onConnectionBroken() {
         IOUtils.closeQuietly(clientSocket);

         if (!targetSocket.isClosed()) {
            IOUtils.closeQuietly(targetSocket);
            connectionCount.decrementAndGet();
            LOG.info("TCP tunnel stopped: %s", tunnelName);
         }
      }

      @SuppressWarnings("resource")
      @Override
      public void run() {
         try {
            connectionCount.incrementAndGet();

            targetSocket.connect(new InetSocketAddress(targetAddress, targetPort), targetConnectTimeout);

            LOG.info("TCP tunnel started: %s", tunnelName);
            new CopyDataThread(clientSocket.getInputStream(), targetSocket.getOutputStream()).start();
            new CopyDataThread(targetSocket.getInputStream(), clientSocket.getOutputStream()).start();
         } catch (final IOException ex) {
            LOG.error(ex);
            onConnectionBroken();
         }
      }
   }

   public interface TcpProxyServerBuilder<THIS extends TcpProxyServerBuilder<THIS, T>, T extends TcpTunnelService> extends
      net.sf.jstuff.core.builder.Builder<TcpTunnelService> {

      /**
       * Default is -1, i.e. unlimited
       */
      @Fluent
      @net.sf.jstuff.core.builder.Builder.Property(required = false)
      THIS withMaxConnections(int value);

      /**
       * @param address can be null
       */
      @Fluent
      @net.sf.jstuff.core.builder.Builder.Property(required = true)
      THIS withListener(String address, int port);

      @Fluent
      @net.sf.jstuff.core.builder.Builder.Property(required = false)
      THIS withProxy(Proxy.Type proxyType, String address, int port);

      @Fluent
      @net.sf.jstuff.core.builder.Builder.Property(required = true)
      THIS withTarget(String address, int port);

      /**
       * Default is 10.000 milliseconds
       */
      @Fluent
      @net.sf.jstuff.core.builder.Builder.Property(required = false)
      THIS withTargetConnectTimeout(int valueMS);
   }

   private static final Logger LOG = Logger.create();

   @SuppressWarnings("unchecked")
   public static TcpProxyServerBuilder<?, TcpTunnelService> builder() {
      return (TcpProxyServerBuilder<?, TcpTunnelService>) BuilderFactory.of(TcpProxyServerBuilder.class).create();
   }

   protected final AtomicInteger connectionCount = new AtomicInteger();

   protected int maxConnections = -1;

   protected @Nullable String listenerAddress;
   protected int listenerPort;

   protected Proxy.@Nullable Type proxyType;
   protected @Nullable String proxyAddress;
   protected int proxyPort;

   protected String targetAddress = lazyNonNull();
   protected int targetPort;

   protected int targetConnectTimeout = 10_000;

   protected TcpTunnelService() {
   }

   public int getConnectionCount() {
      return connectionCount.get();
   }

   @SuppressWarnings("resource")
   @Override
   public void run() {
      final ServerSocket serverSocket;
      try {
         if (Strings.isBlank(listenerAddress)) {
            serverSocket = new ServerSocket(listenerPort);
         } else {
            serverSocket = new ServerSocket(listenerPort, 0, InetAddress.getByName(asNonNull(listenerAddress)));
         }
      } catch (final IOException ex) {
         throw new IllegalStateException("Unable to bind to port " + listenerPort, ex);
      }

      LOG.info("Listening on [%s:%s]...", listenerAddress, listenerPort);

      while (!interrupted()) {
         try {
            final Socket clientSocket = serverSocket.accept();
            if (maxConnections >= 0 && getConnectionCount() >= maxConnections) {
               IOUtils.closeQuietly(clientSocket);
               LOG.warn("Client from [%s:%s] denied. Max connections [%s] reached.", clientSocket.getInetAddress().getHostAddress(),
                  clientSocket.getPort(), maxConnections);
               continue;
            }
            LOG.info("Accepting client from [%s:%s]...", clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
            startTunnel(clientSocket);
         } catch (final IOException ex) {
            LOG.error(ex);
         }
      }
      IOUtils.closeQuietly(serverSocket);
      LOG.info("Stopped listening on [%s:%s].", listenerAddress, listenerPort);
   }

   protected void startTunnel(final Socket clientSocket) throws SocketException {
      new TcpTunnel(clientSocket).start();
   }

   protected void setListener(final String address, final int port) {
      Args.inRange("listenerPort", port, 1, 65535);
      listenerAddress = address;
      listenerPort = port;
   }

   protected void setProxy(final Proxy.Type type, final String address, final int port) {
      Args.inRange("proxyPort", port, 1, 65535);
      proxyType = type;
      proxyAddress = address;
      proxyPort = port;
   }

   protected void setTarget(final String address, final int port) {
      Args.notBlank("targetAddress", address);
      Args.inRange("targetPort", port, 1, 65535);
      targetAddress = address;
      targetPort = port;
   }

   @Override
   public String toString() {
      return Strings.toString(this);
   }
}
