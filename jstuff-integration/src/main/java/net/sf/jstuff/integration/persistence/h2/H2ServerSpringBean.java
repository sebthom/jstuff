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
package net.sf.jstuff.integration.persistence.h2;

import net.sf.jstuff.core.Logger;

import org.h2.tools.Server;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class H2ServerSpringBean implements InitializingBean, DisposableBean
{
	private final static Logger LOG = Logger.make();

	private String dataDir;

	private boolean enabled = true;
	private int tcpPort = 10040;
	private Server tcpServer;
	private boolean webAllowOthers = false;
	private boolean webEnabled = false;
	private int webPort = 8040;
	private Server webServer;

	public void afterPropertiesSet() throws Exception
	{
		if (enabled)
		{
			tcpServer = Server.createTcpServer(new String[]{ //
					"-tcpPort", Integer.toString(tcpPort), //
							"-baseDir", dataDir});
			tcpServer.start();
			LOG.info("Embedded H2 Databases are now available via: jdbc:h2:tcp://localhost:" + tcpPort
					+ "/<DATABASE_NAME>");

			if (webEnabled)
			{
				webServer = Server.createWebServer(new String[]{ //
						"-webPort", Integer.toString(webPort), //
								"-webAllowOthers", Boolean.toString(webAllowOthers) //
						});
				webServer.start();
				LOG.info("H2 UI is now available via: http://localhost:" + webPort);
			}
		}
	}

	public void destroy() throws Exception
	{
		if (webServer != null) webServer.stop();
		if (tcpServer != null) tcpServer.stop();
	}

	/**
	 * @return the dataDir
	 */
	public String getDataDir()
	{
		return dataDir;
	}

	/**
	 * @return the tcpPort
	 */
	public int getTcpPort()
	{
		return tcpPort;
	}

	/**
	 * @return the webPort
	 */
	public int getWebPort()
	{
		return webPort;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @return the webAllowOthers
	 */
	public boolean isWebAllowOthers()
	{
		return webAllowOthers;
	}

	/**
	 * @return the webEnabled
	 */
	public boolean isWebEnabled()
	{
		return webEnabled;
	}

	/**
	 * @param dataDir the dataDir to set
	 */
	@Required
	public void setDataDir(final String dataDir)
	{
		this.dataDir = dataDir;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(final boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
	 * @param tcpPort the tcpPort to set
	 */
	public void setTcpPort(final int tcpPort)
	{
		this.tcpPort = tcpPort;
	}

	/**
	 * @param webAllowOthers the webAllowOthers to set
	 */
	public void setWebAllowOthers(final boolean webAllowOthers)
	{
		this.webAllowOthers = webAllowOthers;
	}

	/**
	 * @param webEnabled the webEnabled to set
	 */
	public void setWebEnabled(final boolean webEnabled)
	{
		this.webEnabled = webEnabled;
	}

	/**
	 * @param webPort the webPort to set
	 */
	public void setWebPort(final int webPort)
	{
		this.webPort = webPort;
	}
}
