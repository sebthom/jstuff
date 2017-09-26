/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.functional.Accept;
import net.sf.jstuff.core.io.IOUtils;
import net.sf.jstuff.core.logging.Logger;
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

    private static enum ClassPathEntryType {
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

    private static Logger LOG = Logger.create();

    private static final List<ClassLoaderHandler> CLASS_LOADER_HANDLERS = new ArrayList<ClassLoaderHandler>();
    static {
        /*
         * URL Classloader
         */
        CLASS_LOADER_HANDLERS.add(new ClassLoaderHandler() {
            public boolean handle(final Accept<String> nameFilter, final ClassLoader cl, final Set<Resource> result) {
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
            }
        });

        /*
         * IBM WebSphere Classloader
         */
        final Class<?> websphereClassLoader = Types.find("com.ibm.ws.classloader.WsClassLoader");
        if (websphereClassLoader != null) {
            LOG.info("IBM WebSphere Classloaders detected.");
            CLASS_LOADER_HANDLERS.add(new ClassLoaderHandler() {
                public boolean handle(final Accept<String> nameFilter, final ClassLoader cl, final Set<Resource> result) {
                    if (!Types.isAssignableTo(cl.getClass(), websphereClassLoader))
                        return false;

                    final String cp = Methods.invoke(cl, "getClassPath");
                    if (cp == null)
                        return true;

                    for (final String path : Strings.split(cp, File.pathSeparatorChar)) {
                        try {
                            _scanClassPathEntry(new File(path).toURI().toURL(), nameFilter, cl, result);
                        } catch (final Exception ex) {
                            LOG.error(ex);
                        }
                    }

                    return true;
                }
            });
        }

        /*
         * Eclipse Classloader
         */
        final Class<?> eclipseClassLoader = Types.find("org.eclipse.osgi.baseadaptor.loader.BaseClassLoader");
        if (eclipseClassLoader != null) {
            CLASS_LOADER_HANDLERS.add(new ClassLoaderHandler() {
                public boolean handle(final Accept<String> nameFilter, final ClassLoader cl, final Set<Resource> result) {
                    if (!Types.isAssignableTo(cl.getClass(), eclipseClassLoader))
                        return false;

                    final Object cpMgr = Methods.invoke(cl, "getClasspathManager");
                    final /*ClasspathEntry*/Object[] cp = (Object[]) Methods.invoke(cpMgr, "getHostClasspathEntries");
                    if (cp == null)
                        return true;

                    for (final /*ClasspathEntry*/Object entry : cp) {

                        final /*BundleFile*/Object bundleFile = Methods.invoke(entry, "getBundleFile");
                        if (bundleFile == null) {
                            continue;
                        }

                        final File baseFile = Methods.invoke(bundleFile, "getBaseFile");
                        if (baseFile == null) {
                            continue;
                        }

                        try {
                            _scanClassPathEntry(baseFile.toURI().toURL(), nameFilter, cl, result);
                        } catch (final Exception ex) {
                            LOG.error(ex);
                        }
                    }

                    return true;
                }
            });
        }
    }

    private static final Method ECLIPSE_BUNDLE_RESOLVER;
    static {
        Method eclipseBundleResolve = null;
        try {
            final Class<?> fileLocatorClass = Types.find("org.eclipse.core.runtime.FileLocator");
            eclipseBundleResolve = fileLocatorClass.getMethod("resolve", URL.class);
        } catch (final Throwable ex) {
            LOG.debug(ex);
        }
        ECLIPSE_BUNDLE_RESOLVER = eclipseBundleResolve;
    }

    private static void _scanClassPathEntry(final URL url, final Accept<String> nameFilter, final ClassLoader cl, final Set<Resource> result)
            throws URISyntaxException, IOException {
        LOG.debug("Scanning classpath entry [%s] of classloader [%s]...", url, cl);

        ClassPathEntryType type = ClassPathEntryType.NOT_SUPPORTED;
        if ("jar".equals(url.getProtocol())) {
            type = ClassPathEntryType.JAR;
        } else if ("file".equals(url.getProtocol())) {
            final File file = new File(url.getPath());
            if (!file.exists()) {
                type = ClassPathEntryType.NOT_EXISTING;
            } else if (file.isDirectory()) {
                type = ClassPathEntryType.DIRECTORY;
            } else if (url.getPath().endsWith(".jar")) {
                type = ClassPathEntryType.JAR;
            }
        } else if (url.getProtocol().startsWith("bundle")) {
            // bundle://         --> Felix, Knopflerfish
            // bundleentry://    --> Equinox
            // bundleresource:// --> Equinox
            type = ClassPathEntryType.BUNDLE;
        }

        switch (type) {
            case JAR: {
                String filePath = null;
                if ("jar".equals(url.getProtocol())) {
                    try {
                        filePath = ((JarURLConnection) url.openConnection()).getJarFileURL().getFile();
                    } catch (final Exception ex) {
                        LOG.debug(ex);
                        filePath = url.getPath();
                        if (filePath.startsWith("file:///")) {
                            filePath = filePath.substring(7);
                        } else if (filePath.startsWith("file://")) {
                            filePath = filePath.substring(6);
                        } else if (filePath.startsWith("file:/")) {
                            filePath = filePath.substring(5);
                        }
                        filePath = Strings.substringBefore(filePath, "!/");
                    }
                } else {
                    filePath = url.getPath();
                }

                for (final JarEntry entry : Enumerations.toIterable(new JarFile(filePath).entries())) {
                    if (!entry.isDirectory() && nameFilter.accept(entry.getName())) {
                        result.add(new Resource(entry.getName(), new URL("jar", "", url.toURI() + "!/" + entry.getName()), cl));
                    }
                }
                break;
            }
            case BUNDLE: {
                if (ECLIPSE_BUNDLE_RESOLVER == null) {
                    LOG.warn("Unsupported classpath entry type [%s]", url);
                    break;
                }
                final URL resolvedURL = (URL) Methods.invoke(null, ECLIPSE_BUNDLE_RESOLVER, url.getPath());
                _scanClassPathEntry(resolvedURL, nameFilter, cl, result);
                break;
            }
            case DIRECTORY: {
                final File rootDir = new File(url.getPath());
                final URI rootDirURI = rootDir.toURI();
                final Queue<File> toScan = new LinkedList<File>();
                toScan.add(rootDir);
                while (!toScan.isEmpty()) {
                    final File file = toScan.poll();
                    if (file.isDirectory()) {
                        toScan.addAll(Arrays.asList(file.listFiles()));
                    } else {
                        final String name = rootDirURI.relativize(file.toURI()).toString();
                        if (nameFilter.accept(name)) {
                            try {
                                result.add(new Resource(name, file.toURI().toURL(), cl));
                            } catch (final Exception ex) {
                                LOG.error(ex);
                            }
                        }
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
            JarFile surefirebooterJar = null;
            InputStream manifestIS = null;
            try {
                surefirebooterJar = new JarFile(surefireRealCP);
                manifestIS = surefirebooterJar.getInputStream(surefirebooterJar.getEntry("META-INF/MANIFEST.MF"));
                final String[] classPath = Strings.split(new Manifest(manifestIS).getMainAttributes().getValue("Class-Path"), ' ');
                for (final String classPathEntry : classPath) {
                    _scanClassPathEntry(new URL(classPathEntry), nameFilter, ClassLoader.getSystemClassLoader(), result);
                }
                surefirebooterJar.close();
            } catch (final Exception ex) {
                LOG.error(ex);
            } finally {
                IOUtils.closeQuietly(manifestIS);
                IOUtils.closeQuietly(surefirebooterJar);
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

        final SortedSet<Resource> result = new TreeSet<Resource>();

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
        return findResources(new Accept<String>() {
            public boolean accept(final String name) {
                return namePattern.matcher(name).matches();
            }
        }, searchScope);
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
