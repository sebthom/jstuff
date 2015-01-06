/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.integration.auth;

import net.sf.jstuff.core.logging.Logger;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.ServerFactory;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class Tomcat6Authenticator implements Authenticator
{
	private static final Logger LOG = Logger.create();

	public Tomcat6Authenticator()
	{
		LOG.infoNew(this);
	}

	/*
	 * based on http://wiki.apache.org/tomcat/HowTo#head-42e95596753a1fa4a4aa396d53010680e3d509b5
	 */
	public boolean authenticate(final String logonName, final String password)
	{
		//Note: this assumes the Container is "Catalina"
		final Engine engine = (Engine) ServerFactory.getServer().findService("Catalina").getContainer();
		final Context context = (Context) engine.findChild(engine.getDefaultHost()).findChild(
				SecurityFilter.HTTP_SERVLET_REQUEST_HOLDER.get().getContextPath());
		return context.getRealm().authenticate(logonName, password) != null;
	}
}