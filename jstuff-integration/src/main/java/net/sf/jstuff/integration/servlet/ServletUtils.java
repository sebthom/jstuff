/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.integration.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class ServletUtils {

    public static String getContextURL(final HttpServletRequest request) {

        final String scheme = request.getScheme();
        final int port = request.getServerPort();
        final String urlPath = request.getContextPath();

        final StringBuilder url = new StringBuilder();
        url.append(scheme); // http, https
        url.append("://");
        url.append(request.getServerName());
        if ("http".equals(scheme) && port != 80 || "https".equals(scheme) && port != 443) {
            url.append(':');
            url.append(request.getServerPort());
        }
        url.append(urlPath);
        return url.toString();
    }

    public static void preventCaching(final HttpServletResponse res) {
        //http://www.onjava.com/pub/a/onjava/excerpt/jebp_3/index2.html
        // Set to expire far in the past.
        res.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
        // Set standard HTTP/1.1 no-cache headers.
        res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        res.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        res.setHeader("Pragma", "no-cache");
    }
}
