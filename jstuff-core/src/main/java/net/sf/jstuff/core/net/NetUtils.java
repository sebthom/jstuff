/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
package net.sf.jstuff.core.net;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
                if (!socket.isOutputShutdown())
                    socket.shutdownOutput();
                if (!socket.isClosed())
                    socket.close();
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
     * 
     * @param resourceURL
     * @throws IOException
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

    /**
     * TODO should return all the bound IP addresses, but returns currently all local IP addresses
     */
    public static List<String> getLocalIPAddresses() {
        try {
            final ArrayList<String> ipAddresses = new ArrayList<String>();
            for (final NetworkInterface nic : Enumerations.toIterable(NetworkInterface.getNetworkInterfaces()))
                for (final InetAddress ia : Enumerations.toIterable(nic.getInetAddresses()))
                    if (!ia.isLoopbackAddress())
                        ipAddresses.add(ia.getHostAddress());
            return ipAddresses;
        } catch (final SocketException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getLocalShortHostName() {
        try {
            // getHostName() does not reliable return only the short name therefore we extract it manually
            return StringUtils.substringBefore(InetAddress.getLocalHost().getHostName() + ".", ".");
        } catch (final UnknownHostException ex) {
            LOG.warn(ex, "Cannot determine short hostname of local host, returning 'localhost' instead.");
            return "localhost";
        }
    }

    public static String getTopLevelDomain(final String url) {
        CharSequence target = url;
        if (!url.endsWith("/"))
            target = new StringBuilder(url).append('/');
        final Matcher m = TOPLEVEL_DOMAIN.matcher(target);
        m.find();
        return m.group(1);
    }

    public static boolean isHostReachable(final String hostname, final int timeoutInMS) {
        Args.notNull("hostname", hostname);
        try {
            return InetAddress.getByName(hostname).isReachable(timeoutInMS);
        } catch (final IOException ex) {
            LOG.trace("Failed to reach host [%s].", ex, hostname);
            return false;
        }
    }

    public static boolean isKnownHost(final String hostname) {
        Args.notNull("hostname", hostname);
        try {
            InetAddress.getByName(hostname);
            return true;
        } catch (final UnknownHostException ex) {
            LOG.trace("Host [%s] is unknown.", hostname);
            return false;
        }
    }

    public static boolean isLocalPortAvailable(final int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            socket.setReuseAddress(true);
            return true;
        } catch (final IOException ex) {
            return false;
        } finally {
            closeQuietly(socket);
        }
    }

    public static boolean isRemotePortOpen(final String hostname, final int port) {
        Args.notNull("hostname", hostname);
        try {
            closeQuietly(new Socket(hostname, port));
            return true;
        } catch (final IOException ex) {
            return false;
        }
    }
}
