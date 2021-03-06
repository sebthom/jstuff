/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SystemUtils extends org.apache.commons.lang3.SystemUtils {
   private static final Logger LOG = Logger.create();

   private static Boolean isContainerized;
   private static Boolean isDockerized;
   private static Boolean isRunningAsAdmin;

   /**
    * Determines if the current Java process is running with administrative permissions
    * (in Windows "elevated", in Unix as "root").
    */
   public static boolean isRunningAsAdmin() {
      if (isRunningAsAdmin == null) {

         try {
            if (IS_OS_WINDOWS) {
               // https://stackoverflow.com/a/11995662/5116073
               final Process p = Runtime.getRuntime().exec(new String[] {"net", "session"});
               p.waitFor(10, TimeUnit.SECONDS);
               if (!p.isAlive() && p.exitValue() == 0) {
                  isRunningAsAdmin = true;
               } else {
                  p.destroyForcibly();
                  isRunningAsAdmin = false;
               }

            } else if (IS_OS_LINUX) {
               final Process p = Runtime.getRuntime().exec(new String[] {"id", "-u"});
               p.waitFor(10, TimeUnit.SECONDS);
               if (!p.isAlive() && p.exitValue() == 0) {
                  try (InputStream is = p.getInputStream()) {
                     final List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
                     isRunningAsAdmin = !lines.isEmpty() && "0".equals(lines.get(0));
                  }
               } else {
                  p.destroyForcibly();
                  isRunningAsAdmin = false;
               }

            } else {
               final java.util.logging.Logger prefsLogger = LogManager.getLogManager().getLogger("java.util.prefs.WindowsPreferences");
               prefsLogger.setLevel(Level.SEVERE);
               try {
                  // https://stackoverflow.com/a/23538961/5116073
                  final Preferences prefs = Preferences.systemRoot();
                  synchronized (prefs) {
                     prefs.put("foo", "bar"); // SecurityException on Windows
                     prefs.remove("foo");
                     prefs.flush(); // BackingStoreException on Linux
                  }
                  isRunningAsAdmin = true;
               } finally {
                  prefsLogger.setLevel(Level.INFO);
               }
            }
         } catch (final Exception ex) {
            LOG.debug(ex);
            isRunningAsAdmin = false;
         }
      }
      return isRunningAsAdmin;
   }

   public static boolean isRunningInsideContainer() {
      if (isContainerized == null) {
         if (IS_OS_UNIX) {
            // see http://docs.podman.io/en/latest/markdown/podman-run.1.html#description
            final Path containerenv = Paths.get("/run/.containerenv");
            if (Files.exists(containerenv)) {
               isContainerized = true;
               return true;
            }
            final Path cgroup = Paths.get("/proc/1/cgroup");
            if (Files.exists(cgroup)) {
               // see https://stackoverflow.com/a/52581380
               try (Stream<String> stream = Files.lines(cgroup)) {
                  final String[] searchFor = {"/docker", "/lxc", "/kubepods", "/garden"};
                  isContainerized = stream.anyMatch(line -> Strings.containsAny(line, searchFor));
                  return isContainerized;
               } catch (final IOException ex) {
                  LOG.debug(ex);
               }
            }
         }
         isContainerized = false;
         return false;
      }
      return isContainerized;
   }

   public static boolean isRunningInsideDocker() {
      if (isDockerized == null) {
         if (IS_OS_UNIX) {
            // see https://stackoverflow.com/a/52581380
            final Path cgroup = Paths.get("/proc/1/cgroup");
            if (Files.exists(cgroup)) {
               try (Stream<String> stream = Files.lines(cgroup)) {
                  if (stream.anyMatch(line -> line.contains("/docker"))) {
                     isContainerized = true;
                     isDockerized = true;
                     return true;
                  }
               } catch (final IOException ex) {
                  LOG.debug(ex);
               }
            }
         }
         isDockerized = false;
      }
      return isDockerized;
   }

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
