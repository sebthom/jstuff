/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.net;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class NetUtils {
   private static final Logger LOG = Logger.create();

   private static final Pattern TOPLEVEL_DOMAIN = Pattern.compile("([^.^/]+\\.[^.^/]+)/");

   public static void closeQuietly(final DatagramSocket socket) {
      if (socket == null)
         return;
      socket.close();
   }

   public static void closeQuietly(final ServerSocket socket) {
      if (socket == null)
         return;
      try {
         socket.close();
      } catch (final IOException ex) {
         LOG.debug(ex, "Exception occured while closing socket.");
      }
   }

   public static void closeQuietly(final Socket socket) {
      if (socket == null)
         return;
      try {
         if (!socket.isClosed()) {
            if (!socket.isOutputShutdown()) {
               socket.shutdownOutput();
            }
            if (!socket.isClosed()) {
               socket.close();
            }
         }
      } catch (final IOException ex) {
         LOG.debug(ex, "Exception occured while closing socket.");
      }
   }

   public static int getAvailableLocalPort() {
      try {
         final ServerSocket socket = new ServerSocket(0);
         socket.setReuseAddress(true);
         socket.close();
         return socket.getLocalPort();
      } catch (final IOException ex) {
         throw new IllegalStateException("Failed to determine an available local port.", ex);
      }
   }

   public static String getHostName(final String url) {
      try {
         final URL u = new URL(url);
         return u.getHost();
      } catch (final MalformedURLException ex) {
         throw new IllegalArgumentException("[url] is not a valid URL.", ex);
      }
   }

   /**
    * returns the modification date of the given resource.
    * Get the resource url via this.getClass().getResource("....").
    */
   public static long getLastModified(final URL resourceURL) throws IOException {
      Args.notNull("resourceURL", resourceURL);

      final URLConnection con = resourceURL.openConnection();

      if (con instanceof JarURLConnection)
         return ((JarURLConnection) con).getJarEntry().getTime();

      /*
       * Because of a bug in Suns VM regarding FileURLConnection, which for some reason causes 0 to be
       * returned if we try to open a connection and get the last modified date. So instead we use File
       * to open the file with the name given to us by the url.getFile(), and then use the File's
       * getLastmodified() method.
       * http://www.orionserver.com/docs/tutorials/taglibs/8.html
       */
      if ("file".equals(resourceURL.getProtocol()))
         return new File(resourceURL.getFile()).lastModified();

      return con.getLastModified();
   }

   public static String getLocalFQHostName() {
      try {
         return InetAddress.getLocalHost().getCanonicalHostName();
      } catch (final UnknownHostException ex) {
         LOG.warn(ex, "Cannot determine fully qualified hostname of local host, returning 'localhost' instead.");
         return "localhost";
      }
   }

   public static List<InetAddress> getLocalInetAddresses() {
      try {
         return Arrays.asList(InetAddress.getAllByName(InetAddress.getLocalHost().getCanonicalHostName()));
      } catch (final UnknownHostException ex) {
         LOG.warn(ex, "Cannot determine local IP Addresses.");
         return Collections.emptyList();
      }
   }

   public static String getLocalShortHostName() {
      try {
         // getHostName() does not reliable return only the short name therefore we extract it manually
         return Strings.substringBefore(InetAddress.getLocalHost().getHostName() + ".", ".");
      } catch (final UnknownHostException ex) {
         LOG.warn(ex, "Cannot determine short hostname of local host, returning 'localhost' instead.");
         return "localhost";
      }
   }

   public static String getTopLevelDomain(final String url) {
      CharSequence target = url;
      if (!url.endsWith("/")) {
         target = new StringBuilder(url).append('/');
      }
      final Matcher m = TOPLEVEL_DOMAIN.matcher(target);
      m.find();
      return m.group(1);
   }

   public static boolean isHostReachable(final String hostname, final int timeoutInMS) {
      Args.notNull("hostname", hostname);
      try {
         return InetAddress.getByName(hostname).isReachable(timeoutInMS);
      } catch (final IOException ex) {
         LOG.debug("Failed to reach host [%s].", ex, hostname);
         return false;
      }
   }

   public static boolean isKnownHost(final String hostname) {
      Args.notNull("hostname", hostname);
      try {
         return InetAddress.getByName(hostname) != null;
      } catch (final UnknownHostException ex) {
         LOG.debug("Host [%s] is unknown.", hostname);
         return false;
      }
   }

   public static boolean isLocalPortAvailable(final int port) {
      try (ServerSocket socket = new ServerSocket(port)) {
         socket.setReuseAddress(true);
         return true;
      } catch (final IOException ex) {
         return false;
      }
   }

   /**
    * @deprecated use {@link #isRemotePortOpen(String, int, int)}
    */
   @Deprecated
   public static boolean isRemotePortOpen(final String hostname, final int port) {
      Args.notNull("hostname", hostname);
      try (Socket socket = new Socket(hostname, port)) {
         return true;
      } catch (final IOException ex) {
         return false;
      }
   }

   public static boolean isRemotePortOpen(final String hostname, final int port, final int connectTimeoutInMS) {
      Args.notNull("hostname", hostname);
      try (Socket socket = new Socket()) {
         socket.setReuseAddress(true);
         socket.connect(new InetSocketAddress(hostname, port), connectTimeoutInMS);
         return true;
      } catch (final IOException ex) {
         return false;
      }
   }
}
