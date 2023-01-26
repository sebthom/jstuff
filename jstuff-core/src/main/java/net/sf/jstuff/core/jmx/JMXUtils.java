/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.jmx;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class JMXUtils {
   private static final Logger LOG = Logger.create();

   @Nullable
   private static MBeanServer mbeanServer;

   public static synchronized MBeanServer getMBeanServer() {
      var mbeanServer = JMXUtils.mbeanServer;
      if (mbeanServer != null)
         return mbeanServer;

      try {
         // http://wiki.jboss.org/wiki/FindMBeanServer
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         if (cl == null) {
            cl = JMXUtils.class.getClassLoader();
         }
         if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
         }
         final Class<?> clazz = cl.loadClass("org.jboss.mx.util.MBeanServerLocator");
         final Method method = clazz.getMethod("locateJBoss", (Class[]) null);
         mbeanServer = JMXUtils.mbeanServer = (MBeanServer) method.invoke((Object[]) null, (Object[]) null);
         if (mbeanServer != null) {
            LOG.info("Located MBeanServer via org.jboss.mx.util.MBeanServerLocator#locateJBoss()");
            return mbeanServer;
         }
      } catch (final Exception | LinkageError ex) {
         LOG.debug("Locating MBeanServer via org.jboss.mx.util.MBeanServerLocator#locateJBoss() way failed.", ex);
      }

      try {
         mbeanServer = JMXUtils.mbeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
         LOG.info("Located MBeanServer via MBeanServerFactory#findMBeanServer(null).get(0)");
         return mbeanServer;
      } catch (final Exception | LinkageError ex) {
         LOG.warn("Locating MBeanServer via MBeanServerFactory#findMBeanServer(null).get(0) failed.", ex);
      }

      try {
         mbeanServer = JMXUtils.mbeanServer = ManagementFactory.getPlatformMBeanServer();
         LOG.info("Located MBeanServer via java.lang.management.ManagementFactory#getPlatformMBeanServer()");
         return mbeanServer;
      } catch (final Exception | LinkageError ex) {
         LOG.warn("Locating MBeanServer via java.lang.management.ManagementFactory#getPlatformMBeanServer() failed.", ex);
      }

      LOG.info("Creating new MBeanServer...");
      mbeanServer = JMXUtils.mbeanServer = MBeanServerFactory.createMBeanServer();
      return mbeanServer;
   }

   /**
    * @return if the JVM runs in debug mode
    */
   public static boolean isDebugModeEnabled() {
      final List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
      for (final String arg : args) {
         if (arg.startsWith("-agentlib:jdwp") && args.contains("-Xdebug") //
            || arg.startsWith("-Xrunjdwp"))
            return true;
      }
      return false;
   }
}
