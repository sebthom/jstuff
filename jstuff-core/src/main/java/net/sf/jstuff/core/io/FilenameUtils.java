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
@SuppressWarnings("deprecation")
public abstract class FilenameUtils extends org.apache.commons.io.FilenameUtils {

   public static @Nullable String getBaseName(final File file) {
      return getBaseName(file.getName());
   }

   /**
    * @return {@code null} if this path has zero elements
    */
   public static @Nullable String getBaseName(final Path file) {
      return getBaseName(file.getFileName().toString());
   }

   public static @Nullable String getExtension(final File file) {
      return getExtension(file.getName());
   }

   /**
    * @return {@code null} if this path has zero elements
    */
   public static @Nullable String getExtension(final Path file) {
      return getExtension(file.getFileName().toString());
   }
}
