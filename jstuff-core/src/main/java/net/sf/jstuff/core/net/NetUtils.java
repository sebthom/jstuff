/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class NetUtils
{
	private static final Logger LOG = Logger.get();

	/**
	 * TODO should return all the bound IP addresses, but returns currently all local ip addresses
	 */
	public static List<String> getIpAddresses()
	{
		final ArrayList<String> ipAddresses = new ArrayList<String>();
		try
		{
			for (final Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				final NetworkInterface nic = en.nextElement();

				for (final Enumeration<InetAddress> en2 = nic.getInetAddresses(); en2.hasMoreElements();)
				{
					final InetAddress ia = en2.nextElement();
					if (!ia.isLoopbackAddress()) ipAddresses.add(ia.getHostAddress());
				}
			}
		}
		catch (final Exception ex)
		{
			LOG.warn("Unexpected exception", ex);
		}
		return ipAddresses;
	}

	/**
	 * returns the modification date of the given resource.
	 * Get the resource url via this.getClass().getResource("....").
	 * @param resourceURL
	 * @throws IOException
	 */
	public static long getLastModified(final URL resourceURL) throws IOException
	{
		Assert.argumentNotNull("resourceURL", resourceURL);

		final URLConnection con = resourceURL.openConnection();

		if (con instanceof JarURLConnection) return ((JarURLConnection) con).getJarEntry().getTime();

		/*
		 * Because of a bug in Suns VM regarding FileURLConnection, which for some reason causes 0 to be
		 * returned if we try to open a connection and get the last modified date. So instead we use File
		 * to open the file with the name given to us by the url.getFile(), and then use the File's 
		 * getLastmodified() method.
		 * http://www.orionserver.com/docs/tutorials/taglibs/8.html
		 */
		if (resourceURL.getProtocol().equals("file")) return new File(resourceURL.getFile()).lastModified();

		return con.getLastModified();
	}

	public static String getLocalFQHostName()
	{
		try
		{
			return InetAddress.getLocalHost().getCanonicalHostName();
		}
		catch (final UnknownHostException ex)
		{
			LOG.warn("Cannot determine fully qualified hostname of local host, returning 'localhost' instead.", ex);
			return "localhost";
		}
	}

	public static String getLocalShortHostName()
	{
		try
		{
			// getHostName() does not reliable return only the short name therefore we extract it manually
			return StringUtils.substringBefore(InetAddress.getLocalHost().getHostName() + ".", ".");
		}
		catch (final UnknownHostException ex)
		{
			LOG.warn("Cannot determine short hostname of local host, returning 'localhost' instead.", ex);
			return "localhost";
		}
	}

	public static boolean isKnownHost(final String hostname)
	{
		Assert.argumentNotNull("hostname", hostname);
		try
		{
			InetAddress.getByName(hostname);
			return true;
		}
		catch (final UnknownHostException ex)
		{
			LOG.trace("Host [%s] is unknown.", hostname);
			return false;
		}
	}

	protected NetUtils()
	{
		super();
	}
}
