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
package net.sf.jstuff.core;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class OSUtils
{
	private static final Logger LOG = Logger.get();

	public static final String OS_NAME = System.getProperty("os.name");

	public static boolean isLinux()
	{
		return OS_NAME != null && OS_NAME.toLowerCase().contains("linux");
	}

	public static boolean isMac()
	{
		return OS_NAME != null && OS_NAME.toLowerCase().contains("mac");
	}

	public static boolean isSun()
	{
		return OS_NAME != null && OS_NAME.toLowerCase().contains("sun");
	}

	public static boolean isUnix()
	{
		return File.separatorChar == '/';
	}

	public static boolean isWindows()
	{
		return OS_NAME != null && OS_NAME.toLowerCase().contains("windows");
	}

	/**
	 * opens the given file with the default application handler
	 * 
	 * @param absoluteFilePath
	 */
	public static void launchWithDefaultApplication(final File file) throws IOException
	{
		if (isWindows())
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler \"" + file.getAbsolutePath() + "\"");
		//"rundll32 SHELL32.DLL,ShellExec_RunDLL " + absoluteFilePath
		else if (isMac()) Runtime.getRuntime().exec("open \"" + file.getAbsolutePath() + "\"");

		throw new UnsupportedOperationException("Not supported on platform " + OS_NAME);
	}

	public static boolean openURLInBrowser(final String url)
	{
		try
		{
			if (isWindows())
				Runtime.getRuntime()
						.exec("rundll32 url.dll,FileProtocolHandler javascript:location.href='" + url + "'");
			else if (isMac())
				Runtime.getRuntime().exec("open " + url);
			else if (isSun())
				Runtime.getRuntime().exec("/usr/dt/bin/sdtwebclient " + url);
			else
			{
				Process p = Runtime.getRuntime().exec("firefox -remote \"openURL(" + url + ")\"");
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("firefox " + url);
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("mozilla -remote \"openURL(" + url + ")\"");
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("mozilla " + url);
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("netscape -remote \"openURL(" + url + ")\"");
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("netscape " + url);
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("opera -remote \"openURL(" + url + ")\"");
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("galeon " + url);
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("konqueror " + url);
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("opera " + url);
				if (p.waitFor() != 0) p = Runtime.getRuntime().exec("xterm -e lynx " + url);
			}
		}
		catch (final Exception ex)
		{
			LOG.error("Failed to launch browser", ex);
			return false;
		}
		return true;
	}

	protected OSUtils()
	{
		super();
	}
}
