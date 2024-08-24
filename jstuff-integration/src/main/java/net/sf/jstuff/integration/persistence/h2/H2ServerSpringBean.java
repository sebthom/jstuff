/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.persistence.h2;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.sql.SQLException;

import org.eclipse.jdt.annotation.Nullable;
import org.h2.tools.Server;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class H2ServerSpringBean {
   private static final Logger LOG = Logger.create();

   private String dataDir = lateNonNull();
   private boolean enabled = true;
   private int tcpPort = 10040;
   private @Nullable Server tcpServer;
   private boolean webAllowOthers;
   private boolean webEnabled;
   private int webPort = 8040;
   private @Nullable Server webServer;

   public H2ServerSpringBean() {
      LOG.infoNew(this);
   }

   @PreDestroy
   public void destroy() {
      if (webServer != null) {
         webServer.stop();
      }
      if (tcpServer != null) {
         tcpServer.stop();
      }
   }

   public String getDataDir() {
      return dataDir;
   }

   public int getTcpPort() {
      return tcpPort;
   }

   public int getWebPort() {
      return webPort;
   }

   @PostConstruct
   public void initialize() {
      if (enabled) {
         try {
            final var tcpServer = this.tcpServer = asNonNullUnsafe(Server.createTcpServer( //
               "-tcpPort", Integer.toString(tcpPort), //
               "-baseDir", dataDir //
            ));
            tcpServer.start();
            LOG.info("Embedded H2 Databases are now available via: jdbc:h2:tcp://localhost:" + tcpPort + "/<DATABASE_NAME>");

            if (webEnabled) {
               final var webServer = this.webServer = asNonNullUnsafe(Server.createWebServer( //
                  "-webPort", Integer.toString(webPort), //
                  "-webAllowOthers", Boolean.toString(webAllowOthers) //
               ));
               webServer.start();
               LOG.info("H2 UI is now available via: http://localhost:" + webPort);
            }
         } catch (final SQLException ex) {
            throw new IllegalStateException(ex);
         }
      }
   }

   public boolean isEnabled() {
      return enabled;
   }

   public boolean isWebAllowOthers() {
      return webAllowOthers;
   }

   public boolean isWebEnabled() {
      return webEnabled;
   }

   @Inject
   public void setDataDir(final String dataDir) {
      this.dataDir = dataDir;
   }

   public void setEnabled(final boolean enabled) {
      this.enabled = enabled;
   }

   public void setTcpPort(final int tcpPort) {
      this.tcpPort = tcpPort;
   }

   public void setWebAllowOthers(final boolean webAllowOthers) {
      this.webAllowOthers = webAllowOthers;
   }

   public void setWebEnabled(final boolean webEnabled) {
      this.webEnabled = webEnabled;
   }

   public void setWebPort(final int webPort) {
      this.webPort = webPort;
   }
}
