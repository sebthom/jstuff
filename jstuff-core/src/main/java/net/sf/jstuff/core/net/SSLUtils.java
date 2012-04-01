/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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
package net.sf.jstuff.core.net;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import net.sf.jstuff.core.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SSLUtils
{
	private static final Logger LOG = Logger.make();

	public static void installAllTrustManager()
	{
		final TrustManager[] trustAllCerts = {new javax.net.ssl.X509TrustManager()
			{
				public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
				{
					//
				}

				public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType)
				{
					//
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
			}};

		// Install the all-trusting trust manager
		try
		{
			// create the factory where we can set some parameters for the connection
			final SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
				{
					public boolean verify(final String hostname, final SSLSession session)
					{
						return true;
					}
				});
		}
		catch (final Exception ex)
		{
			LOG.error("Failed to install all-trusting trust manager", ex);
		}
	}

	protected SSLUtils()
	{
		super();
	}
}
