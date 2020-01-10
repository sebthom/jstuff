/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.reflection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.functional.Accept;
import net.sf.jstuff.core.io.FileUtils;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.ogn.ObjectGraphNavigatorDefaultImpl;
import net.sf.jstuff.core.validation.Args;

/**
 * Locates resources in the classpath
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Resources {

   private interface ClassLoaderHandler {
      boolean handle(Accept<String> nameFilter, ClassLoader cl, Set<Resource> result) throws Exception;
   }

   private enum ClassPathEntryType {
      BUNDLE,
      DIRECTORY,
      JAR,
      NOT_SUPPORTED,
      NOT_EXISTING;
   }

   /**
    * Represents a resource on the classpath
    */
   public static final class Resource implements Comparable<Resource> {
      public final String name;
      public final URL url;
      public final URI uri;
      public final ClassLoader cl;

      protected Resource(final String name, final URL url, final ClassLoader cl) throws URISyntaxException {
         Args.notNull("name", name);
         Args.notNull("url", url);
         this.name = name;
         this.url = url;
         uri = url.toURI();
         this.cl = cl;
      }

      @Override
      public int compareTo(final Resource o) {
         return name.compareTo(o.name);
      }

      /**
       * Equality is only calculated based on resource name and URI to prevent duplicate listing in case
       * the same exact same JAR or folder is referenced by two classloaders.
       */
      @Override
      public boolean equals(final Object o) {
         if (this == o)
            return true;
         if (o == null || !(o instanceof Resource))
            return false;
         final Resource other = (Resource) o;
         return name.equals(other.name) && uri.equals(other.uri);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + (name == null ? 0 : name.hashCode());
         result = prime * result + (uri == null ? 0 : uri.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return "Resource [name=" + name + ", uri=" + uri + "]";
      }
   }

   private static final Logger LOG = Logger.create();

   private static final List<ClassLoaderHandler> CLASS_LOADER_HANDLERS = new ArrayList<>();
   static {
      /*
       * URL Classloader
       */
      CLASS_LOADER_HANDLERS.add((nameFilter, cl, result) -> {
         if (!(cl instanceof URLClassLoader))
            return false;

         final URL[] urls = ((URLClassLoader) cl).getURLs();
         if (urls != null) {
            for (final URL url : urls) {
               try {
                  _scanClassPathEntry(url, nameFilter, cl, result);
               } catch (final Exception ex) {
                  LOG.error(ex);
               }
            }
         }
         return true;
      });

      /*
       * IBM WebSphere Classloader
       */
      final Class<?> websphereClassLoader = Types.find("com.ibm.ws.classloader.WsClassLoader", false);
      if (websphereClassLoader != null) {
         LOG.info("IBM WebSphere Classloaders detected.");
         CLASS_LOADER_HANDLERS.add((nameFilter, cl, result) -> {
            if (!Types.isAssignableTo(cl.getClass(), websphereClassLoader))
               return false;

            final String cp = Methods.invoke(cl, "getClassPath");
            if (cp == null)
               return true;

            for (final String classPathEntry : Strings.split(cp, File.pathSeparatorChar)) {
               try {
                  _scanClassPathEntry(new File(classPathEntry).toURI().toURL(), nameFilter, cl, result);
               } catch (final Exception ex) {
                  LOG.error(ex);
               }
            }
            return true;
         });
      }

      /*
       * IBM WebSphere Liberty Classloader
       */
      final Class<?> websphereLibertyClassLoader;
      {
         Class<?> clazz = Types.find("com.ibm.ws.classloading.internal.ContainerClassLoader", false);
         if (clazz == null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
               cl = cl.getClass().getClassLoader();
               if (cl != null) {
                  try {
                     clazz = Class.forName("com.ibm.ws.classloading.internal.ContainerClassLoader", false, cl);
                  } catch (final ClassNotFoundException ex) {
                     // ignore
                  } catch (final NoClassDefFoundError ex) {
                     LOG.debug(ex);
                  }
               }
            }
         }
         websphereLibertyClassLoader = clazz;
      }

      if (websphereLibertyClassLoader != null) {
         LOG.info("IBM WebSphere Liberty Classloaders detected.");
         CLASS_LOADER_HANDLERS.add((nameFilter, cl, result) -> {
            if (!Types.isAssignableTo(cl.getClass(), websphereLibertyClassLoader))
               return false;

            final List<?/*ContainerClassLoader.UniversalContainer*/> classPathEntries = (List<?>) ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(cl,
               "smartClassPath.classPath");
            if (classPathEntries != null) {
               for (final Object classPathEntry : classPathEntries) {
                  final Collection<URL> urls = ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(classPathEntry,
                     "container.URLs" /*com.ibm.wsspi.adaptable.module.Container#getURLs()*/);

                  if (urls != null) {
                     for (final URL url : urls) {
                        try {
                           _scanClassPathEntry(url, nameFilter, cl, result);
                        } catch (final Exception ex) {
                           LOG.error(ex);
                        }
                     }
                  }
               }
            }
            return true;
         });
      }

      /*
       * Eclipse Classloader
       */
      final Class<?> eclipseBaseClassLoaderClass = Types.find("org.eclipse.osgi.baseadaptor.loader.BaseClassLoader", false);
      if (eclipseBaseClassLoaderClass != null) {
         CLASS_LOADER_HANDLERS.add((nameFilter, cl, result) -> {
            if (!Types.isAssignableTo(cl.getClass(), eclipseBaseClassLoaderClass))
               return false;

            final Object/*ClasspathEntry*/[] classPathEntry = (Object[]) ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(cl,
               "classpathManager.hostClasspathEntries");

            if (classPathEntry != null) {
               for (final Object/*ClasspathEntry*/ entry : classPathEntry) {
                  final Object baseFile = ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(entry, "bundleFile.baseFile");
                  if (baseFile instanceof File) {
                     try {
                        _scanClassPathEntry(((File) baseFile).toURI().toURL(), nameFilter, cl, result);
                     } catch (final Exception ex) {
                        LOG.error(ex);
                     }
                  }
               }
            }
            return true;
         });
      }
   }

   private static final Method ECLIPSE_BUNDLE_RESOLVER;
   static {
      Method eclipseBundleResolve = null;
      try {
         final Class<?> fileLocatorClass = Types.find("org.eclipse.core.runtime.FileLocator", true);
         if (fileLocatorClass != null) {
            eclipseBundleResolve = fileLocatorClass.getMethod("resolve", URL.class);
         }
      } catch (final Throwable ex) { // CHECKSTYLE:IGNORE IllegalCatch
         LOG.debug(ex);
      }
      ECLIPSE_BUNDLE_RESOLVER = eclipseBundleResolve;
   }

   private static void _scanClassPathEntry(final URL url, final Accept<String> nameFilter, final ClassLoader cl, final Set<Resource> result)
      throws URISyntaxException, IOException {
      LOG.debug("Scanning classpath entry [%s] of classloader [%s]...", url, cl);

      final ClassPathEntryType classPathEntryType;
      if ("jar".equals(url.getProtocol())) {
         classPathEntryType = ClassPathEntryType.JAR;
      } else if ("file".equals(url.getProtocol())) {
         final File file = new File(url.getPath());
         if (!file.exists()) {
            classPathEntryType = ClassPathEntryType.NOT_EXISTING;
         } else if (file.isDirectory()) {
            classPathEntryType = ClassPathEntryType.DIRECTORY;
         } else if (url.getPath().endsWith(".jar")) {
            classPathEntryType = ClassPathEntryType.JAR;
         } else {
            classPathEntryType = ClassPathEntryType.NOT_SUPPORTED;
         }
      } else if (url.getProtocol().startsWith("bundle")) {
         // bundle://         --> Felix, Knopflerfish
         // bundleentry://    --> Equinox
         // bundleresource:// --> Equinox
         classPathEntryType = ClassPathEntryType.BUNDLE;
      } else {
         classPathEntryType = ClassPathEntryType.NOT_SUPPORTED;
      }

      switch (classPathEntryType) {
         case BUNDLE: {
            if (ECLIPSE_BUNDLE_RESOLVER == null) {
               LOG.warn("Unsupported classpath entry type [%s] of classloader [%s]... ", url, cl);
               break;
            }
            final URL resolvedURL = (URL) Methods.invoke(null, ECLIPSE_BUNDLE_RESOLVER, url.getPath());
            _scanClassPathEntry(resolvedURL, nameFilter, cl, result);
            break;
         }

         case DIRECTORY: {
            // prevent bogus classpath entry screwing up the scanning process
            if ("/".equals(url.getPath())) {
               LOG.warn("Ignoring classpath entry [%s] of classloader [%s] pointing to filesystem root.", url, cl);
               break;
            }

            final File rootDir = new File(url.getPath());
            final URI rootDirURI = rootDir.toURI();
            final Queue<File> toScan = new LinkedList<>();

            // to prevent potential endless recursions
            final Set<File> visitedSymlinks = new HashSet<>();

            toScan.add(rootDir);
            while (!toScan.isEmpty()) {
               final File file = toScan.poll();
               if (file.isDirectory()) {
                  if (visitedSymlinks.contains(file)) {
                     continue;
                  }
                  if (Files.isSymbolicLink(file.toPath())) {
                     visitedSymlinks.add(file);
                  }

                  final File[] entries = file.listFiles();
                  if (entries != null && entries.length > 0) {
                     CollectionUtils.addAll(toScan, entries);
                  }
               } else {
                  final String name = rootDirURI.relativize(file.toURI()).toString();
                  if (nameFilter.accept(name)) {
                     try {
                        result.add(new Resource(name, cl.getResource(name), cl));
                     } catch (final Exception ex) {
                        LOG.error(ex);
                     }
                  }
               }
            }
            break;
         }

         case JAR: {
            if ("file".equals(url.getProtocol())) {
               final File jarFile = new File(url.getPath());
               final JarFile jar = new JarFile(jarFile);
               try {
                  for (final JarEntry entry : Enumerations.toIterable(jar.entries())) {
                     if (!entry.isDirectory() && nameFilter.accept(entry.getName())) {
                        result.add(new Resource(entry.getName(), cl.getResource(entry.getName()), cl));
                     }
                  }
               } finally {
                  jar.close();
               }
            } else {
               try {
                  JarURLConnection jarConn = (JarURLConnection) url.openConnection();
                  final String[] jarURLParts = Strings.splitByWholeSeparator(jarConn.getURL().toExternalForm(), "!/");
                  if (jarURLParts[1].length() == 0) {
                     // handle: jar:file:/C:/apps/myapp.jar!/
                     _scanClassPathEntry(jarConn.getJarFileURL(), nameFilter, cl, result);
                  } else {
                     // handle:
                     //   jar:file:/C:/apps/myapp.jar!/BOOT-INF/classes"
                     //   jar:file:/C:/apps/myapp.jar!/BOOT-INF/classes/"
                     //   jar:file:/C:/apps/myapp.jar!/BOOT-INF/classes!/"
                     //   jar:file:/C:/apps/myapp.jar!/BOOT-INF/classes/!/"
                     //   jar:file:/C:/apps/myapp.jar!/BOOT-INF/lib/myutils-3.0.1.jar"
                     //   jar:file:/C:/apps/myapp.jar!/BOOT-INF/lib/myutils-3.0.1.jar!/"

                     String entryToExtract = jarURLParts[1];

                     // open the URL connection without trailing entry,
                     // i.e. instead of "jar:file:c:/apps/myapp.jar!/BOOT-INF/classes!/" use "jar:file:c:/apps/myapp.jar!"
                     jarConn = (JarURLConnection) new URL(jarURLParts[0] + "!/").openConnection();

                     final JarFile jarFile = jarConn.getJarFile();
                     try {
                        JarEntry jarEntry = jarFile.getJarEntry(entryToExtract);

                        if (!jarEntry.isDirectory()) { // not reliable, we test again by adding / to the entry name
                           final String entryNameTmp = entryToExtract + "/";
                           final JarEntry jarEntryTmp = jarFile.getJarEntry(entryNameTmp);
                           if (jarEntryTmp != null) {
                              entryToExtract = entryNameTmp;
                              jarEntry = jarEntryTmp;
                           }
                        }

                        final File tmpDir = FileUtils.createTempDirectory("jstuff-", ".tmp");
                        try {
                           if (jarEntry.isDirectory()) {
                              // extract the referenced directory
                              for (final JarEntry entry : Enumerations.toIterable(jarFile.entries())) {
                                 if (!entry.isDirectory() && entry.getName().startsWith(jarEntry.getName())) {
                                    final File tmpFile = new java.io.File(tmpDir, entry.getName());
                                    tmpFile.getParentFile().mkdirs();
                                    FileUtils.writeAndClose(tmpFile, jarFile.getInputStream(entry));
                                 }
                              }
                              _scanClassPathEntry(new File(tmpDir, jarEntry.getName()).toURI().toURL(), nameFilter, cl, result);
                           } else {
                              // extract the referenced file
                              final File tmpFile = new File(tmpDir, StringUtils.replaceChars(jarEntry.getName(), "/", "_"));
                              FileUtils.writeAndClose(tmpFile, jarFile.getInputStream(jarEntry));
                              _scanClassPathEntry(tmpFile.toURI().toURL(), nameFilter, cl, result);
                           }
                        } finally {
                           FileUtils.deleteQuietly(tmpDir);
                        }
                     } finally {
                        jarFile.close();
                     }
                  }
               } catch (final Exception ex) {
                  LOG.warn(ex);
               }
            }
            break;
         }

         case NOT_EXISTING: {
            LOG.debug("Not existing classpath entry [%s]", url);
            break;
         }

         default:
            LOG.warn("Unsupported classpath entry type [%s]", url);
      }
   }

   /**
    * Handling Maven Surefire classpaths
    *
    * http://maven.apache.org/plugins-archives/maven-surefire-plugin-2.11/examples/class-loading.html
    */
   private static void _scanSurefireClasspath(final Accept<String> nameFilter, final Set<Resource> result) {

      /*
       * when forkCount > 0 and useSystemClassLoader = true and useManifestOnlyJar=true (which is the default)
       */
      final String surefireRealCP = System.getProperty("surefire.real.class.path");
      if (surefireRealCP != null) {
         LOG.debug("Maven Surefire run (forked + manifest-only-jar) detected.");
         final String[] classPath = Strings.split(surefireRealCP, File.pathSeparatorChar);
         for (final String classPathEntry : classPath) {
            if (classPathEntry.contains("surefirebooter")) {
               JarFile surefirebooterJar = null;
               InputStream manifestIS = null;
               try {
                  surefirebooterJar = new JarFile(classPathEntry);
                  manifestIS = surefirebooterJar.getInputStream(surefirebooterJar.getEntry("META-INF/MANIFEST.MF"));
                  final String[] surefireBooterClassPath = Strings.split(new Manifest(manifestIS).getMainAttributes().getValue("Class-Path"), ' ');
                  for (final String surefireBooterClassPathEntry : surefireBooterClassPath) {
                     _scanClassPathEntry(new URL(surefireBooterClassPathEntry), nameFilter, ClassLoader.getSystemClassLoader(), result);
                  }
                  surefirebooterJar.close();
               } catch (final Exception ex) {
                  LOG.error(ex);
               } finally {
                  IOUtils.closeQuietly(manifestIS);
                  IOUtils.closeQuietly(surefirebooterJar);
               }
            } else {
               try {
                  _scanClassPathEntry(new File(classPathEntry).toURI().toURL(), nameFilter, ClassLoader.getSystemClassLoader(), result);
               } catch (final Exception ex) {
                  LOG.error(ex);
               }
            }
         }

         return;
      }

      /*
       * when forkCount = 0
       */
      final String surefireTestCP = System.getProperty("surefire.test.class.path");
      if (surefireTestCP != null && System.getProperty("maven.home") != null) {
         LOG.debug("Maven Surefire run (non-forked) detected.");
         final String[] classPath = Strings.split(surefireTestCP, File.pathSeparatorChar);
         for (final String classPathEntry : classPath) {
            try {
               _scanClassPathEntry(new File(classPathEntry).toURI().toURL(), nameFilter, ClassLoader.getSystemClassLoader(), result);
            } catch (final Exception ex) {
               LOG.error(ex);
            }
         }
      }
   }

   public static SortedSet<Resource> findResources(final Accept<String> nameFilter) {
      return findResources(nameFilter, Thread.currentThread().getContextClassLoader());
   }

   public static SortedSet<Resource> findResources(final Accept<String> nameFilter, ClassLoader searchScope) {
      Args.notNull("searchScope", searchScope);
      Args.notNull("nameFilter", nameFilter);

      final SortedSet<Resource> result = new TreeSet<>();

      _scanSurefireClasspath(nameFilter, result);

      /*
       * regular classloader scan
       */
      while (searchScope != null) {

         boolean handled = false;
         for (final ClassLoaderHandler clHandler : CLASS_LOADER_HANDLERS) {
            try {
               if (clHandler.handle(nameFilter, searchScope, result)) {
                  handled = true;
                  break;
               }
            } catch (final Exception ex) {
               LOG.error(ex);
            }
         }

         if (!handled) {
            LOG.warn("Unsupported classloader type [%s]", searchScope);
         }

         searchScope = searchScope.getParent();
      }

      return result;

   }

   public static Collection<Resource> findResources(final Pattern namePattern) {
      return findResources(namePattern, Thread.currentThread().getContextClassLoader());
   }

   public static Collection<Resource> findResources(final Pattern namePattern, final ClassLoader searchScope) {
      Args.notNull("namePattern", namePattern);
      return findResources(name -> namePattern.matcher(name).matches(), searchScope);
   }

   /**
    * To recursively get all ".xml" files, use "**&#47;*.xml".
    *
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<Resource> findResourcesByGlobPattern(final String globPattern) {
      return findResourcesByGlobPattern(globPattern, Thread.currentThread().getContextClassLoader());
   }

   /**
    * To recursively get all ".xml" files, use "**&#47;*.xml".
    *
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<Resource> findResourcesByGlobPattern(final String globPattern, final ClassLoader searchScope) {
      Args.notNull("globPattern", globPattern);

      return findResourcesByRegExPattern(Strings.globToRegex(globPattern).toString(), searchScope);
   }

   public static Collection<Resource> findResourcesByRegExPattern(final String regExPattern) {
      return findResourcesByRegExPattern(regExPattern, Thread.currentThread().getContextClassLoader());
   }

   public static Collection<Resource> findResourcesByRegExPattern(final String regExPattern, final ClassLoader searchScope) {
      Args.notNull("regExPattern", regExPattern);

      return findResources(Pattern.compile(regExPattern), searchScope);
   }
}
