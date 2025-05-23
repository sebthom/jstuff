/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.classloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Extended {@link URLClassLoader} that supports parent-last class-loading strategy and
 * the recursive loading of JARs from directories.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class URLClassLoaderExt extends URLClassLoader {

   private static final Logger LOG = Logger.create();

   private boolean parentLast;

   public URLClassLoaderExt() {
      super(new URL[0]);
   }

   public URLClassLoaderExt(final ClassLoader parent) {
      super(new URL[0], parent);
   }

   public URLClassLoaderExt(final URL[] urls) {
      super(urls);
   }

   public URLClassLoaderExt(final URL[] urls, final ClassLoader parent) {
      super(urls, parent);
   }

   public URLClassLoaderExt(final URL[] urls, final ClassLoader parent, final URLStreamHandlerFactory factory) {
      super(urls, parent, factory);
   }

   public void addJAR(final File jarFile) throws IOException {
      Args.isFileReadable("jarFile", jarFile);

      LOG.trace("Adding file [%s] to classpath", jarFile);
      addURL(jarFile.toURI().toURL());
   }

   public boolean addJARs(final File jarDirectory, final boolean recursive) throws IOException {
      final File[] children = jarDirectory.listFiles();
      if (children == null)
         return false;

      boolean jarsAdded = false;
      for (final File child : children)
         if (child.isFile() && child.getName().endsWith(".jar")) {
            addJAR(child);
            jarsAdded = true;
         } else if (recursive && child.isDirectory() && addJARs(child, true)) {
            jarsAdded = true;
         }
      return jarsAdded;
   }

   public boolean addJARs(final File jarDirectory, final boolean recursive, final Pattern jarNamePattern) throws IOException {
      Args.notNull("jarNamePattern", jarNamePattern);

      final File[] children = jarDirectory.listFiles();
      if (children == null)
         return false;

      boolean jarsAdded = false;
      for (final File child : children)
         if (child.isFile() && jarNamePattern.matcher(child.getName()).matches()) {
            addJAR(child);
            jarsAdded = true;
         } else if (recursive && child.isDirectory() && addJARs(child, true, jarNamePattern)) {
            jarsAdded = true;
         }
      return jarsAdded;
   }

   public boolean addJARs(final File jarDirectory, final boolean recursive, final String jarNamePattern) throws IOException {
      Args.notNull("jarNamePattern", jarNamePattern);

      final File[] children = jarDirectory.listFiles();
      if (children == null)
         return false;

      boolean jarsAdded = false;
      final Pattern pattern = Pattern.compile(jarNamePattern);
      for (final File child : children)
         if (child.isFile() && pattern.matcher(child.getName()).matches()) {
            addJAR(child);
            jarsAdded = true;
         } else if (recursive && child.isDirectory() && addJARs(child, true, pattern)) {
            jarsAdded = true;
         }

      return jarsAdded;
   }

   @Override
   public @Nullable URL getResource(final String name) {
      Args.notNull("name", name);

      // if class loader strategy is parent first, then use the default resource loading behavior
      if (!parentLast)
         return super.getResource(name);

      final URL url = findResource(name);
      return url == null ? super.getResource(name) : url;
   }

   public boolean isParentLast() {
      return parentLast;
   }

   @Override
   protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
      Args.notNull("name", name);

      // if class loader strategy is parent first, then use the default class loading behavior
      if (!parentLast)
         return super.loadClass(name, resolve);

      // see if the class was loaded already
      Class<?> clazz = findLoadedClass(name);

      // load the class
      if (clazz == null) {
         try {
            clazz = findClass(name);
            LOG.trace("Loaded class %s", name);
         } catch (final ClassNotFoundException ex) {
            LOG.trace("Loading class %s via parent class loader", name);
            // in case the class is not part of the registered URLs let the super class handle the class loading
            return super.loadClass(name, resolve);
         }
      }

      if (resolve) {
         resolveClass(clazz);
      }
      return clazz;
   }

   public void setParentLast(final boolean parentLast) {
      this.parentLast = parentLast;
   }
}
