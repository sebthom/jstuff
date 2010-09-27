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
package net.sf.jstuff.integration.auth;

import java.security.Principal;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Realm;
import org.apache.catalina.Server;
import org.apache.catalina.ServerFactory;
import org.apache.catalina.Service;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AuthenticatorTomcatImpl implements Authenticator
{
	/*
	 * based on http://wiki.apache.org/tomcat/HowTo#head-42e95596753a1fa4a4aa396d53010680e3d509b5
	 */
	public boolean authenticate(final String logonName, final String password)
	{
		final Server server = ServerFactory.getServer();

		//Note: this assumes the Container is "Catalina"
		final Service service = server.findService("Catalina");
		final Engine engine = (Engine) service.getContainer();
		final Host host = (Host) engine.findChild(engine.getDefaultHost());
		final Context context = (Context) host.findChild(SecurityFilter.HTTP_SERVLET_REQUEST_HOLDER.get()
				.getContextPath());
		final Realm realm = context.getRealm();
		final Principal p = realm.authenticate(logonName, password);
		return p != null;
	}
}