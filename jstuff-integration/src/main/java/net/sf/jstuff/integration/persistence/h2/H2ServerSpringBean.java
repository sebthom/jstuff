/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.persistence.h2;

import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.h2.tools.Server;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class H2ServerSpringBean {
    private static final Logger LOG = Logger.create();

    private String dataDir;
    private boolean enabled = true;
    private int tcpPort = 10040;
    private Server tcpServer;
    private boolean webAllowOthers = false;
    private boolean webEnabled = false;
    private int webPort = 8040;
    private Server webServer;

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
                tcpServer = Server.createTcpServer(new String[] { //
                        "-tcpPort", Integer.toString(tcpPort), //
                        "-baseDir", dataDir });
                tcpServer.start();
                LOG.info("Embedded H2 Databases are now available via: jdbc:h2:tcp://localhost:" + tcpPort + "/<DATABASE_NAME>");

                if (webEnabled) {
                    webServer = Server.createWebServer(new String[] { //
                            "-webPort", Integer.toString(webPort), //
                            "-webAllowOthers", Boolean.toString(webAllowOthers) //
                    });
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
