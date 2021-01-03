/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import net.sf.jstuff.core.builder.BuilderFactory;
import net.sf.jstuff.core.builder.OnPostBuild;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * Delete all regular files from the given directory.
 *
 * <pre>
 * DirectoryCleaner directoryCleaner = DirectoryCleaner.builder() //
 *    .directory(Path.get("mypath")) //
 *    .recursive(true) //
 *    .minimumFileAge(Duration.ofDays(7)) //
 *    .minimumFileSize(Size.ofMiB(10)) //
 *    .onFileDeleted((file, attrs) -> System.out.println(file)) //
 *    .build();
 *
 * directoryCleaner.cleanDirectory();
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DirectoryCleaner {

   @Builder.Property(required = false, nullable = false)
   public interface Builder<THIS extends Builder<THIS, T>, T extends DirectoryCleaner> extends net.sf.jstuff.core.builder.Builder<T> {

      /**
       * Specifies if this cleaner should automatically run when the JVM exits.
       */
      THIS cleanOnExit(boolean value);

      /**
       * The directory to clean.
       */
      THIS directory(Path value);

      THIS fileFilter(BiPredicate<Path, BasicFileAttributes> value);

      /**
       * Only delete files whose last modification is older than given duration. Default is to delete all files.
       */
      THIS minimumFileAge(Duration value);

      /**
       * Only delete files that at least have the given file size.
       */
      THIS minimumFileSize(long value, ByteUnit unit);

      THIS onFileDeleted(BiConsumer<Path, BasicFileAttributes> value);

      /**
       * Recursively remove all files (but does not delete directories). Default is false.
       */
      THIS recursive(boolean value);
   }

   private static final Logger LOG = Logger.create();

   private static ConcurrentLinkedQueue<DirectoryCleaner> directoryCleanersToRunOnExit;

   protected static synchronized void registerCleanerOnExit(final DirectoryCleaner cleaner) {
      if (directoryCleanersToRunOnExit == null) {
         directoryCleanersToRunOnExit = new ConcurrentLinkedQueue<>();
         Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
            @Override
            public void run() {
               for (final DirectoryCleaner cleaner : directoryCleanersToRunOnExit) {
                  try {
                     if (cleaner.getDirectory() != null) {
                        cleaner.cleanDirectory();
                     }
                  } catch (final NoSuchFileException | FileNotFoundException ex) {
                     // ignore
                  } catch (final Exception ex) {
                     LOG.error("Failed to clean directory: " + cleaner.getDirectory(), ex);
                  }
               }
            }
         });
      }
      directoryCleanersToRunOnExit.add(cleaner);
   }

   @SuppressWarnings("unchecked")
   public static Builder<?, ? extends DirectoryCleaner> builder() {
      return (Builder<?, ? extends DirectoryCleaner>) BuilderFactory.of(Builder.class).create();
   }

   protected boolean cleanOnExit;
   protected Path directory;
   protected boolean recursive;
   protected Duration minimumFileAge;
   protected Size minimumFileSize;
   protected BiPredicate<Path, BasicFileAttributes> fileFilter = (path, attr) -> true;
   protected BiConsumer<Path, BasicFileAttributes> onFileDeleted;

   /**
    * Cleans the directory specified via the builder.
    */
   public void cleanDirectory() throws IOException {
      cleanDirectory(directory, onFileDeleted);
   }

   /**
    * Cleans the given directory, ignoring the directory specified via the builder.
    */
   public void cleanDirectory(final Path directory, final BiConsumer<Path, BasicFileAttributes> onFileDeleted) throws IOException {
      Args.notNull("directory", directory);

      if (!Files.isDirectory(directory)) {
         LOG.warn("Cannot clean [%s] which is a file and not a directory...", directory);
         return;
      }

      LOG.debug("Cleaning [%s]...", directory);

      final long deleteBefore;
      if (minimumFileAge == null) {
         deleteBefore = Long.MAX_VALUE;
      } else {
         deleteBefore = System.currentTimeMillis() - minimumFileAge.toMillis();
      }

      final long minimumFileSize = this.minimumFileSize.getBytes().longValue();
      Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

         @Override
         public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            if (directory.equals(dir))
               return FileVisitResult.CONTINUE;

            return recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
         }

         @Override
         public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            if (!fileFilter.test(file, attrs))
               return FileVisitResult.CONTINUE;

            if (minimumFileSize > 0 && attrs.size() < minimumFileSize)
               return FileVisitResult.CONTINUE;

            if (minimumFileAge != null && attrs.lastModifiedTime().toMillis() > deleteBefore)
               return FileVisitResult.CONTINUE;

            Files.delete(file);
            if (onFileDeleted != null) {
               onFileDeleted.accept(file, attrs);
            }
            return FileVisitResult.CONTINUE;
         }
      });
   }

   public Path getDirectory() {
      return directory;
   }

   public Duration getMinimumFileAge() {
      return minimumFileAge;
   }

   public boolean isRecursive() {
      return recursive;
   }

   @OnPostBuild
   protected void onPostBuild() {
      if (cleanOnExit) {
         registerCleanerOnExit(this);
      }
   }
}
