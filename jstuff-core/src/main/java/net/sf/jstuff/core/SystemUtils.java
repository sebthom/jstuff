/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core;

import java.io.File;
import java.io.IOException;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SystemUtils extends org.apache.commons.lang3.SystemUtils {
   private static final Logger LOG = Logger.create();

   /**
    * opens the given file with the default application handler
    */
   public static void launchWithDefaultApplication(final File file) throws IOException {
      if (IS_OS_WINDOWS) {
         Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler \"" + file.getAbsolutePath() + "\"");
      } else if (IS_OS_MAC) {
         Runtime.getRuntime().exec("open \"" + file.getAbsolutePath() + "\"");
      }

      throw new UnsupportedOperationException("Not supported on platform " + OS_NAME);
   }

   public static boolean openURLInBrowser(final String url) {
      try {
         if (IS_OS_WINDOWS) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler javascript:location.href='" + url + "'");
         } else if (IS_OS_MAC) {
            Runtime.getRuntime().exec("open " + url);
         } else if (IS_OS_SUN_OS) {
            Runtime.getRuntime().exec("/usr/dt/bin/sdtwebclient " + url);
         } else {
            Process p = Runtime.getRuntime().exec("firefox -remote \"openURL(" + url + ")\"");
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("firefox " + url);
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("mozilla -remote \"openURL(" + url + ")\"");
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("mozilla " + url);
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("netscape -remote \"openURL(" + url + ")\"");
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("netscape " + url);
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("opera -remote \"openURL(" + url + ")\"");
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("galeon " + url);
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("konqueror " + url);
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("opera " + url);
            }
            if (p.waitFor() != 0) {
               p = Runtime.getRuntime().exec("xterm -e lynx " + url);
            }
         }
      } catch (final Exception ex) {
         LOG.error(ex, "Failed to launch browser");
         return false;
      }
      return true;
   }
}
