/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.asNonNull;

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

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.builder.Builder.Property;
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

   @Property(required = false, nullable = false)
   public interface Builder<THIS extends Builder<THIS, T>, T extends DirectoryCleaner> extends net.sf.jstuff.core.builder.Builder<T> {

      /**
       * Specifies if this cleaner should automatically run when the JVM exits.
       */
      THIS cleanOnExit(boolean value);

      /**
       * The directory to clean.
       */
      @Property(required = true)
      THIS directory(Path value);

      THIS fileFilter(BiPredicate<Path, BasicFileAttributes> value);

      /**
       * Only delete files whose last modification is older than given duration. Default is to delete all files.
       */
      THIS minimumFileAge(Duration value);

      /**
       * Only delete files that at least have the given file size. Default is to delete all files.
       */
      THIS minimumFileSize(long value, ByteUnit unit);

      THIS onFileDeleted(BiConsumer<Path, BasicFileAttributes> value);

      /**
       * Recursively remove all files (but does not delete directories). Default is false.
       */
      THIS recursive(boolean value);
   }

   private static final Logger LOG = Logger.create();

   @Nullable
   private static ConcurrentLinkedQueue<DirectoryCleaner> directoryCleanersToRunOnExit;

   protected static synchronized void registerCleanerOnExit(final DirectoryCleaner cleaner) {
      if (directoryCleanersToRunOnExit == null) {
         directoryCleanersToRunOnExit = new ConcurrentLinkedQueue<>();
         Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
            @Override
            public void run() {
               for (final DirectoryCleaner cleaner : asNonNull(directoryCleanersToRunOnExit)) {
                  try {
                     cleaner.cleanDirectory();
                  } catch (final NoSuchFileException | FileNotFoundException ex) {
                     // ignore
                  } catch (final Exception ex) {
                     LOG.error("Failed to clean directory: " + cleaner.getDirectory(), ex);
                  }
               }
            }
         });
      }
      asNonNull(directoryCleanersToRunOnExit).add(cleaner);
   }

   @SuppressWarnings("unchecked")
   public static Builder<?, ? extends DirectoryCleaner> builder() {
      return (Builder<?, ? extends DirectoryCleaner>) BuilderFactory.of(Builder.class).create();
   }

   protected boolean cleanOnExit;
   protected Path directory = Path.of("/foobar"); // overridden by mandatory builder argument
   protected boolean recursive;
   @Nullable
   protected Duration minimumFileAge;
   protected Size minimumFileSize = Size.ZERO;
   protected BiPredicate<Path, BasicFileAttributes> fileFilter = (path, attr) -> true;
   @Nullable
   protected BiConsumer<Path, BasicFileAttributes> onFileDeleted;

   protected DirectoryCleaner() {
   }

   /**
    * Cleans the directory specified via the builder.
    */
   public void cleanDirectory() throws IOException {
      cleanDirectory(directory, onFileDeleted);
   }

   /**
    * Cleans the given directory, ignoring the directory specified via the builder.
    */
   public void cleanDirectory(final Path directory, final @Nullable BiConsumer<Path, BasicFileAttributes> onFileDeleted)
      throws IOException {
      Args.notNull("directory", directory);

      if (!Files.isDirectory(directory)) {
         LOG.warn("Cannot clean [%s] which is a file and not a directory...", directory);
         return;
      }

      LOG.debug("Cleaning [%s]...", directory);

      final long deleteBefore;
      final var minimumFileAge = this.minimumFileAge;
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
            if (!fileFilter.test(file, attrs) //
               || minimumFileSize > 0 && attrs.size() < minimumFileSize //
               || minimumFileAge != null && attrs.lastModifiedTime().toMillis() > deleteBefore)
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

   @Nullable
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
