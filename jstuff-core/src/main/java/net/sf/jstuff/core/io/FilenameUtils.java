/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class FilenameUtils extends org.apache.commons.io.FilenameUtils {

   @Nullable
   public static String getBaseName(final File file) {
      return getBaseName(file.getName());
   }

   /**
    * @return {@code null} if this path has zero elements
    */
   @Nullable
   public static String getBaseName(final Path file) {
      return getBaseName(file.getFileName().toString());
   }

   @Nullable
   public static String getExtension(final File file) {
      return getExtension(file.getName());
   }

   /**
    * @return {@code null} if this path has zero elements
    */
   @Nullable
   public static String getExtension(final Path file) {
      return getExtension(file.getFileName().toString());
   }
}
