/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.jstuff.integration.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ServletUtils
{
	public static String getContextURL(final HttpServletRequest request)
	{
		final StringBuilder url = new StringBuilder();
		final String scheme = request.getScheme();
		final int port = request.getServerPort();
		final String urlPath = request.getContextPath();

		url.append(scheme); // http, https
		url.append("://");
		url.append(request.getServerName());
		if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443)
		{
			url.append(':');
			url.append(request.getServerPort());
		}
		url.append(urlPath);
		return url.toString();
	}
}
