/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.integration.jmx;

import java.lang.reflect.Method;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JMXUtils
{
	private static Logger LOG = Logger.get();

	public static MBeanServer getMBeanServer()
	{
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();

		// try JBoss way
		try
		{
			// http://wiki.jboss.org/wiki/FindMBeanServer
			final Class< ? > clazz = cl.loadClass("org.jboss.mx.util.MBeanServerLocator");
			final Method method = clazz.getMethod("locateJBoss", (Class[]) null);
			final MBeanServer server = (MBeanServer) method.invoke((Object[]) null, (Object[]) null);
			if (server != null)
			{
				LOG.info("Found MBeanServer via org.jboss.mx.util.MBeanServerLocator.locateJBoss()");
				return server;
			}
		}
		catch (final Exception ex)
		{
			LOG.debug("Locating MBeanServer the JBoss way failed.", ex);
		}

		// try the JDK 1.5 way
		try
		{
			final Class< ? > clazz = cl.loadClass("java.lang.management.ManagementFactory");
			final Method method = clazz.getMethod("getPlatformMBeanServer", (Class[]) null);
			final MBeanServer server = (MBeanServer) method.invoke((Object[]) null, (Object[]) null);
			if (server != null)
			{
				LOG.info("Found MBeanServer via java.lang.management.ManagementFactory.getPlatformMBeanServer()");
				return server;
			}
		}
		catch (final Exception ex)
		{
			LOG.debug("Locating MBeanServer the brute force way failed.", ex);
		}

		// try MBeanServerFactory.findMBeanServer(null).get(0) way
		try
		{
			final MBeanServer server = (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
			if (server != null)
			{
				LOG.info("Found MBeanServer via MBeanServerFactory.findMBeanServer(null).get(0)");
				return server;
			}
		}
		catch (final Exception ex)
		{
			LOG.debug("Locating MBeanServer via MBeanServerFactory.findMBeanServer(null).get(0) failed.", ex);
		}

		// create a new MBeanServer
		final MBeanServer server = MBeanServerFactory.createMBeanServer();
		LOG.info("Created new MBeanServer");
		return server;
	}
}
