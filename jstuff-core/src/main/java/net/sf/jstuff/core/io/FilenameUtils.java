/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.lang3.StringUtils;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class FilenameUtils extends org.apache.commons.io.FilenameUtils {
   public static String concat(final File basePath, final String... paths) {
      Args.notNull("basePath", basePath);
      return concat(basePath.getPath(), paths);
   }

   public static String concat(final String basePath, final String... paths) {
      Args.notNull("basePath", basePath);
      String result = basePath;
      for (final String path : paths)
         if (StringUtils.isNotEmpty(path)) {
            result = org.apache.commons.io.FilenameUtils.concat(result, path);
         }
      return result;
   }

   public static String getBaseName(final File file) {
      return getBaseName(file.getName());
   }

   public static String getBaseName(final Path file) {
      return getBaseName(file.getFileName().toString());
   }

   public static String getCurrentPath() {
      try {
         return new File(".").getCanonicalPath();
      } catch (final IOException ex) {
         throw new RuntimeIOException(ex);
      }
   }

   public static String getExtension(final File file) {
      return getExtension(file.getName());
   }

   public static String getExtension(final Path file) {
      return getExtension(file.getFileName().toString());
   }
}
