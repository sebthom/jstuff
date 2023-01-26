/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ClassPathResourcesServletContext extends ServletContextWrapper {
   private static final Logger LOG = Logger.create();

   public ClassPathResourcesServletContext(final ServletContext delegate) {
      super(delegate);
   }

   @Override
   public @Nullable ServletContext getContext(final String uripath) {
      final var ctx = delegate.getContext(uripath);
      if (ctx instanceof ClassPathResourcesServletContext)
         return ctx;
      if (ctx == delegate)
         return this;
      if (ctx == null)
         return null;
      return new ClassPathResourcesServletContext(ctx);
   }

   @Override
   public @Nullable URL getResource(final String path) throws MalformedURLException {
      URL resource = delegate.getResource(path);
      if (resource == null) {
         resource = ClassPathResourcesFilter.findResourceInClassPath(path);
      }
      return resource;
   }

   @SuppressWarnings("resource")
   @Override
   public @Nullable InputStream getResourceAsStream(final String path) {
      InputStream stream = delegate.getResourceAsStream(path);
      if (stream == null) {
         final URL resource = ClassPathResourcesFilter.findResourceInClassPath(path);
         if (resource != null) {
            try {
               stream = resource.openStream();
            } catch (final IOException ex) {
               LOG.error(ex, "Failed to open stream of resource [%s]", path);
            }
         }
      }
      return stream;
   }
}
