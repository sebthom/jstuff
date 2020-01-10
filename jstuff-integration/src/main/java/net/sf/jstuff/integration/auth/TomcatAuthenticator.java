/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.auth;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Server;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TomcatAuthenticator implements Authenticator {
   private static final Logger LOG = Logger.create();

   public TomcatAuthenticator() {
      LOG.infoNew(this);
   }

   /*
    * based on http://wiki.apache.org/tomcat/HowTo#head-42e95596753a1fa4a4aa396d53010680e3d509b5
    */
   @Override
   public boolean authenticate(final String logonName, final String password) {
      try {
         // https://stackoverflow.com/questions/6833947/org-apache-catalina-serverfactory-getserver-equivalent-in-tomcat-7
         // https://stackoverflow.com/questions/34657248/tomcat-8-webapp-dynamically-add-postresources
         final MBeanServer mBeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
         final ObjectName name = new ObjectName("Catalina", "type", "Server");
         final Server server = (Server) mBeanServer.getAttribute(name, "managedResource");
         final Engine engine = server.findService("Catalina").getContainer();
         final Context context = (Context) engine.findChild(engine.getDefaultHost()).findChild(SecurityFilter.HTTP_SERVLET_REQUEST_HOLDER.get()
            .getContextPath());
         return context.getRealm().authenticate(logonName, password) != null;
      } catch (final Exception ex) {
         LOG.error(ex);
         return false;
      }
   }
}
