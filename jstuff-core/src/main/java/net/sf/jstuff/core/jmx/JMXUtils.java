/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.jmx;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class JMXUtils {
   private static final Logger LOG = Logger.create();

   private static MBeanServer mbeanServer;

   public static synchronized MBeanServer getMBeanServer() {
      if (mbeanServer != null)
         return mbeanServer;

      try {
         // http://wiki.jboss.org/wiki/FindMBeanServer
         final ClassLoader cl = Thread.currentThread().getContextClassLoader();
         final Class<?> clazz = cl.loadClass("org.jboss.mx.util.MBeanServerLocator");
         final Method method = clazz.getMethod("locateJBoss", (Class[]) null);
         mbeanServer = (MBeanServer) method.invoke((Object[]) null, (Object[]) null);
         if (mbeanServer != null) {
            LOG.info("Located MBeanServer via org.jboss.mx.util.MBeanServerLocator#locateJBoss()");
            return mbeanServer;
         }
      } catch (final Exception | LinkageError ex) {
         LOG.debug("Locating MBeanServer via org.jboss.mx.util.MBeanServerLocator#locateJBoss() way failed.", ex);
      }

      try {
         mbeanServer = ManagementFactory.getPlatformMBeanServer();
         if (mbeanServer != null) {
            LOG.info("Located MBeanServer via java.lang.management.ManagementFactory#getPlatformMBeanServer()");
            return mbeanServer;
         }
      } catch (final Exception | LinkageError ex) {
         LOG.warn("Locating MBeanServer via java.lang.management.ManagementFactory#getPlatformMBeanServer() failed.", ex);
      }

      try {
         mbeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
         if (mbeanServer != null) {
            LOG.info("Located MBeanServer via MBeanServerFactory#findMBeanServer(null).get(0)");
            return mbeanServer;
         }
      } catch (final Exception | LinkageError ex) {
         LOG.warn("Locating MBeanServer via MBeanServerFactory#findMBeanServer(null).get(0) failed.", ex);
      }

      LOG.info("Creating new MBeanServer...");
      mbeanServer = MBeanServerFactory.createMBeanServer();
      return mbeanServer;
   }

   /**
    * @return if the JVM runs in debug mode
    */
   public static boolean isDebugModeEnabled() {
      final List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
      for (final String arg : args) {
         if (arg.startsWith("-agentlib:jdwp") && args.contains("-Xdebug"))
            return true;
         if (arg.startsWith("-Xrunjdwp"))
            return true;
      }
      return false;
   }
}
