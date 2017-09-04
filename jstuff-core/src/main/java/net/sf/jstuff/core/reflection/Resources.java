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
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.functional.Accept;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Locates resources in the classpath
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Resources {

    private static enum ClassPathEntryType {
        UNSUPPORTED,
        JAR,
        BUNDLE,
        DIRECTORY;
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
        public boolean equals(final Object obj) {
            if (this == obj)
                return true;
            if (obj == null || !(obj instanceof Resource))
                return false;
            final Resource other = (Resource) obj;
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

    private static Method eclipseBundleResolveMethod;

    static {
        try {
            final Class<?> fileLocatorClass = Types.find("org.eclipse.core.runtime.FileLocator");
            eclipseBundleResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
        } catch (final Throwable ex) {
            eclipseBundleResolveMethod = null;
        }
    }

    private static Logger LOG = Logger.create();

    private static void _scanClassPathEntry(final URL url, final SortedSet<Resource> result, final ClassLoader cl, final Accept<String> nameFilter)
            throws URISyntaxException, IOException {
        ClassPathEntryType type = ClassPathEntryType.UNSUPPORTED;
        if ("jar".equals(url.getProtocol())) {
            type = ClassPathEntryType.JAR;
        } else if ("file".equals(url.getProtocol())) {
            if (new File(url.getPath()).isDirectory()) {
                type = ClassPathEntryType.DIRECTORY;
            } else if (url.getPath().endsWith(".jar")) {
                type = ClassPathEntryType.JAR;
            }
        } else if (url.getProtocol().startsWith("bundle")) {
            // bundle://         --> Felix, Knopflerfish
            // bundleresource:// --> Equinox
            // bundleentry://    --> Equinox
            type = ClassPathEntryType.BUNDLE;
        }

        switch (type) {
            case JAR: {
                for (final JarEntry entry : Enumerations.toIterable(new JarFile(url.getPath()).entries())) {
                    if (!entry.isDirectory() && nameFilter.accept(entry.getName())) {
                        result.add(new Resource(entry.getName(), new URL("jar", "", url.toURI() + "!/" + entry.getName()), cl));
                    }
                }
                break;
            }
            case BUNDLE: {
                if (eclipseBundleResolveMethod == null) {
                    LOG.warn("Unsupported classpath entry type [%s]", url);
                    break;
                }
                final URL resolvedURL = (URL) Methods.invoke(null, eclipseBundleResolveMethod, url.getPath());
                _scanClassPathEntry(resolvedURL, result, cl, nameFilter);
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
            default:
                LOG.warn("Unsupported classpath entry type [%s]", url);
        }
    }

    public static SortedSet<Resource> findResources(final Accept<String> nameFilter) {
        return findResources(nameFilter, Thread.currentThread().getContextClassLoader());
    }

    public static SortedSet<Resource> findResources(final Accept<String> nameFilter, ClassLoader searchScope) {
        Args.notNull("searchScope", searchScope);
        Args.notNull("nameFilter", nameFilter);

        final SortedSet<Resource> result = new TreeSet<Resource>();
        while (searchScope != null) {
            if (searchScope instanceof URLClassLoader) {
                final URLClassLoader cl = (URLClassLoader) searchScope;
                final URL[] urls = cl.getURLs();
                if (urls != null) {
                    for (final URL url : urls) {
                        try {
                            _scanClassPathEntry(url, result, cl, nameFilter);
                        } catch (final Exception ex) {
                            LOG.error(ex);
                        }
                    }
                }
            } else {
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
