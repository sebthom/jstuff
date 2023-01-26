/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.io.MoreFiles;
import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class SystemUtils extends org.apache.commons.lang3.SystemUtils {
   private static final Logger LOG = Logger.create();

   @Nullable
   private static Boolean isContainerized;

   @Nullable
   private static Boolean isDockerized;

   @Nullable
   private static Boolean isRunningAsAdmin;

   private static final Collection<String> WINDOWS_EXE_FILE_EXTENSIONS = List.of(Strings.splitByWholeSeparator( //
      IS_OS_WINDOWS //
         ? getEnvironmentVariable("PATHEXT", "") //
         : ".COM;.EXE;.BAT;.CMD;.VBS;.VBE;.JS;.JSE;.WSF;.WSH;.MSC", //
      ";" //
   ));

   /**
    * Searches for the given program on PATH
    */
   @Nullable
   public static Path findExecutable(final String program, final boolean resolveSymlinks) {
      if (Strings.isEmpty(program))
         return null;

      final var paths = Strings.splitByWholeSeparator(getEnvironmentVariable("PATH", ""), File.pathSeparator);

      if (IS_OS_WINDOWS) {
         boolean programHasExeFileExtension = false;
         for (final String ext : WINDOWS_EXE_FILE_EXTENSIONS) {
            if (Strings.endsWithIgnoreCase(program, ext)) {
               programHasExeFileExtension = true;
               break;
            }
         }

         if (!programHasExeFileExtension) {
            for (final String pathAsString : paths) {
               final Path path = Paths.get(pathAsString);
               for (final String ext : WINDOWS_EXE_FILE_EXTENSIONS) {
                  try {
                     Path programPath = path.resolve(program + ext);
                     if (!Files.exists(programPath)) {
                        continue;
                     }
                     programPath = resolveSymlinks ? programPath.toRealPath() : programPath.toRealPath(LinkOption.NOFOLLOW_LINKS);
                     if (MoreFiles.isExecutableFile(programPath))
                        return programPath;
                  } catch (final Exception ex) {
                     LOG.debug(ex);
                  }
               }
            }
         }
      }

      for (final String pathAsString : paths) {
         try {
            Path programPath = Paths.get(pathAsString).resolve(program);
            if (!Files.exists(programPath)) {
               continue;
            }
            programPath = resolveSymlinks ? programPath.toRealPath() : programPath.toRealPath(LinkOption.NOFOLLOW_LINKS);
            if (MoreFiles.isExecutableFile(programPath))
               return programPath;
         } catch (final Exception ex) {
            LOG.debug(ex);
         }
      }

      return null;
   }

   public static String getEnvironmentVariable(final String name, final String defaultValue) {
      try {
         final String value = System.getenv(name);
         return value == null ? defaultValue : value;
      } catch (final SecurityException ex) {
         return defaultValue;
      }
   }

   public static String getProperty(final String name, final String defaultValue) {
      final var val = System.getProperty(name);
      if (val == null)
         return defaultValue;
      return val;
   }

   /**
    * Determines if the current Java process is running with administrative permissions
    * (in Windows "elevated", in Unix as "root").
    */
   public static boolean isRunningAsAdmin() {
      if (isRunningAsAdmin != null)
         return isRunningAsAdmin;

      try {
         if (IS_OS_WINDOWS) {
            // https://stackoverflow.com/a/11995662/5116073

            final Process p = Runtime.getRuntime().exec(asNonNull("net", "session"));
            p.waitFor(10, TimeUnit.SECONDS);
            if (!p.isAlive() && p.exitValue() == 0) {
               isRunningAsAdmin = true;
               return true;
            }
            p.destroyForcibly();
            isRunningAsAdmin = false;
            return false;
         }

         if (IS_OS_LINUX) {
            final Process p = Runtime.getRuntime().exec(asNonNull("id", "-u"));
            p.waitFor(10, TimeUnit.SECONDS);
            if (!p.isAlive() && p.exitValue() == 0) {
               try (var is = p.getInputStream()) {
                  final var lines = IOUtils.readLines(is, Charset.defaultCharset());
                  isRunningAsAdmin = !lines.isEmpty() && "0".equals(lines.get(0));
                  return isRunningAsAdmin;
               }
            }
            p.destroyForcibly();
            isRunningAsAdmin = false;
            return false;
         }

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
            return true;
         } finally {
            prefsLogger.setLevel(Level.INFO);
         }
      } catch (final Exception ex) {
         LOG.debug(ex);
         isRunningAsAdmin = false;
         return false;
      }
   }

   public static boolean isRunningInsideContainer() {
      if (isContainerized != null)
         return isContainerized;

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
            try (var stream = Files.lines(cgroup)) {
               final @NonNull String[] searchFor = {"/docker", "/lxc", "/kubepods", "/garden"};
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

   public static boolean isRunningInsideDocker() {
      if (isDockerized != null)
         return isDockerized;

      if (IS_OS_UNIX) {
         // see https://stackoverflow.com/a/52581380
         final Path cgroup = Paths.get("/proc/1/cgroup");
         if (Files.exists(cgroup)) {
            try (var stream = Files.lines(cgroup)) {
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
      return false;
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

   public static List<String> splitCommandLine(final String commandLine) {
      if (commandLine.isBlank())
         return Collections.emptyList();

      final char escapeChar = '\\';
      boolean isEscaped = false;

      final var tokens = new ArrayList<String>();
      final var token = new StringBuilder();

      Character quotedWith = null;

      for (final var ch : commandLine.toCharArray()) {

         if (quotedWith == null) {

            if (isEscaped) {
               if (ch != '"' && ch != '\'' && !Character.isWhitespace(ch)) {
                  token.append(escapeChar);
               }
               token.append(ch);
               isEscaped = false;
            } else {
               switch (ch) {
                  case escapeChar:
                     isEscaped = true;
                     break;
                  case '\'':
                  case '"':
                     quotedWith = ch;
                     break;

                  default:
                     if (Character.isWhitespace(ch)) {
                        if (token.length() > 0) {
                           tokens.add(token.toString());
                           token.setLength(0);
                        }
                     } else {
                        token.append(ch);
                     }
               }
            }

         } else { // if (quotedWith != null)

            if (isEscaped) {
               token.append(ch);
               isEscaped = false;
            } else {
               if (ch == escapeChar) {
                  isEscaped = true;
               } else if (ch == quotedWith) {
                  tokens.add(token.toString());
                  token.setLength(0);
                  quotedWith = null;
               } else {
                  token.append(ch);
               }
            }
         }
      }

      if (quotedWith != null)
         throw new IllegalArgumentException("Unbalanced [" + quotedWith + "] quotes in " + commandLine);

      if (isEscaped) {
         token.append(escapeChar);
      }

      if (token.length() > 0) {
         tokens.add(token.toString());
      }

      return tokens;
   }
}
