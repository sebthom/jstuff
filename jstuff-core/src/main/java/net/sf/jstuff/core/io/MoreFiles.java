/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
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
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.SystemUtils;
import net.sf.jstuff.core.functional.LongBiConsumer;
import net.sf.jstuff.core.io.channel.DelegatingWritableByteChannel;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MoreFiles {
   private static final Logger LOG = Logger.create();

   private static final AtomicLong _FILE_UNIQUE_ID = new AtomicLong();
   private static final Queue<Path> _FILES_TO_DELETE_ON_SHUTDOWN = new ConcurrentLinkedQueue<>();

   private static final LinkOption[] NOFOLLOW_LINKS = {LinkOption.NOFOLLOW_LINKS};

   private static final StandardOpenOption[] FILE_READ_OPTIONS = {StandardOpenOption.READ};
   private static final StandardOpenOption[] FILE_WRITE_OPTIONS = { //
      StandardOpenOption.CREATE, //
      StandardOpenOption.TRUNCATE_EXISTING, //
      StandardOpenOption.WRITE //
   };

   static {
      Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
         @Override
         public void run() {
            for (final Path path : _FILES_TO_DELETE_ON_SHUTDOWN) {
               try {
                  LOG.debug("Deleting %s...", path);
                  forceDelete(path);
               } catch (final FileNotFoundException ex) {
                  // ignore
               } catch (final IOException ex) {
                  LOG.error("Failed to delete file: " + path, ex);
               }
            }
         }
      });
   }

   public static boolean contentEquals(final Path file1, final Path file2) throws IOException {
      if (file1 == null && file2 == null)
         return true;
      if (file1 == null || file2 == null)
         return false;

      final boolean file1Exists = Files.exists(file1);
      if (file1Exists && !Files.exists(file2))
         return false;

      if (!file1Exists)
         return true;

      if (Files.isDirectory(file1))
         throw new IOException("[" + file1 + "] is a directory and not a file.");

      if (Files.isDirectory(file2))
         throw new IOException("[" + file2 + "] is a directory and not a file.");

      if (Files.size(file1) != Files.size(file2))
         return false;

      if (Files.isSameFile(file1, file2))
         return true;

      try (FileChannel file1Ch = FileChannel.open(file1, FILE_READ_OPTIONS);
           FileChannel file2Ch = FileChannel.open(file2, FILE_READ_OPTIONS)) {
         final MappedByteBuffer buff1 = file1Ch.map(FileChannel.MapMode.READ_ONLY, 0, file1Ch.size());
         final MappedByteBuffer buff2 = file2Ch.map(FileChannel.MapMode.READ_ONLY, 0, file2Ch.size());
         return buff1.equals(buff2);
      }
   }

   public static void copyContent(final Path source, final Path target) throws IOException {
      copyContent(source, target, (bytesWritten, totalBytesWritten) -> { /* ignore */ });
   }

   public static void copyContent(final Path source, final Path target, final LongBiConsumer onBytesWritten) throws IOException {
      Args.notNull("onBytesWritten", onBytesWritten);
      try ( //
           FileChannel inCh = FileChannel.open(source, FILE_READ_OPTIONS);
           WritableByteChannel outCh = new DelegatingWritableByteChannel(FileChannel.open(target, FILE_WRITE_OPTIONS), onBytesWritten) //
      ) {
         final long size = inCh.size();
         long position = 0;

         if (SystemUtils.IS_OS_WINDOWS) {
            // using a fixed buffer with 64Mb - 32Kb is for some reason slightly faster on Windows (even on Win 10x64)
            // see also https://stackoverflow.com/q/7379469/5116073
            while (position < size) {
               position += inCh.transferTo(position, (long) 64 * 1024 * 1024 - 32 * 1024, outCh);
            }
         } else {
            while (position < size) {
               position += inCh.transferTo(position, size - position, outCh);
            }
         }
      }
   }

   /**
    * Creates a temp directory that will be automatically deleted on JVM exit.
    *
    * @param parentDirectory if null, then create in system temp directory
    */
   public static Path createTempDirectory(final Path parentDirectory, final String prefix, final String extension) throws IOException {
      final Path tmpDir = createUniqueDirectory(parentDirectory == null ? getTempDirectory() : parentDirectory, prefix, extension);
      forceDeleteOnExit(tmpDir);
      return tmpDir;
   }

   /**
    * Creates a temp directory that will be automatically deleted on JVM exit.
    */
   public static Path createTempDirectory(final String prefix, final String extension) throws IOException {
      final Path tmpDir = createUniqueDirectory(getTempDirectory(), prefix, extension);
      forceDeleteOnExit(tmpDir);
      return tmpDir;
   }

   /**
    * @param parentDirectory if null, then create in current working directory
    */
   public static Path createUniqueDirectory(Path parentDirectory, final String prefix, final String extension) throws IOException {
      if (parentDirectory == null) {
         parentDirectory = getWorkingDirectory();
      }
      while (true) {
         final String dirName = (prefix == null ? "" : prefix) + _FILE_UNIQUE_ID.getAndIncrement() + (extension == null ? extension : "");
         final Path dir = parentDirectory.resolve(dirName);
         if (!Files.exists(dir, NOFOLLOW_LINKS))
            return Files.createDirectories(dir);
      }
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<Path> find(final Path searchRoot, final String globPattern, final boolean includeFiles, final boolean includeDirectories)
      throws IOException {
      final Collection<Path> result = new ArrayList<>();
      if (includeFiles && includeDirectories) {
         find(searchRoot, globPattern, result::add, result::add);
      } else if (includeDirectories) {
         find(searchRoot, globPattern, result::add, null);
      } else {
         find(searchRoot, globPattern, null, result::add);
      }

      return result;
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static void find(Path searchRoot, final String globPattern, final Consumer<Path> onDirMatch, final Consumer<Path> onFileMatch) throws IOException {
      Args.notNull("searchRoot", searchRoot);
      Args.notNull("globPattern", globPattern);

      searchRoot = searchRoot.normalize().toAbsolutePath();
      final String searchRootPath = searchRoot.toString();
      final int searchRootLen = searchRootPath.length();
      final String searchRegEx = Strings.globToRegex(globPattern).toString();
      LOG.debug("\n  glob:  %s\n  regex: %s\n  searchRoot: %s", globPattern, searchRegEx, searchRootPath);
      final Pattern filePattern = Pattern.compile("^" + searchRegEx);

      Files.walkFileTree(searchRoot, new SimpleFileVisitor<Path>() {

         @Override
         public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            String folderPath = dir.toString().replace('\\', '/');
            if (folderPath.length() == searchRootLen)
               return FileVisitResult.CONTINUE;

            folderPath = folderPath.substring(searchRootLen + 1);
            final boolean isMatch = filePattern.matcher(folderPath).find();
            if (isMatch && onDirMatch != null) {
               onDirMatch.accept(dir);
            }
            return FileVisitResult.CONTINUE;
         }

         @Override
         public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            String filePath = file.toString().replace('\\', '/');
            filePath = filePath.substring(searchRootLen + 1);
            final boolean isMatch = filePattern.matcher(filePath).find();
            if (isMatch && onFileMatch != null) {
               onFileMatch.accept(file);
            }
            return FileVisitResult.CONTINUE;
         }
      });
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<Path> findDirectories(final Path searchRootPath, final String globPattern) throws IOException {
      return find(searchRootPath, globPattern, false, true);
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<Path> findFiles(final Path searchRootPath, final String globPattern) throws IOException {
      return find(searchRootPath, globPattern, true, false);
   }

   public static void forceDelete(final Path fileOrDirectory) throws IOException {
      if (Files.isDirectory(fileOrDirectory, NOFOLLOW_LINKS)) {
         Files.walkFileTree(fileOrDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
               if (exc != null && !(exc instanceof FileNotFoundException))
                  throw exc;
               Files.delete(dir);
               return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
               Files.delete(file);
               return FileVisitResult.CONTINUE;
            }
         });
      } else {
         Files.deleteIfExists(fileOrDirectory);
      }
   }

   public static void forceDeleteOnExit(final Path fileOrDirectory) {
      Args.notNull("fileOrDirectory", fileOrDirectory);
      LOG.debug("Registering %s for deletion on JVM shutdown...", fileOrDirectory);
      _FILES_TO_DELETE_ON_SHUTDOWN.add(fileOrDirectory);
   }

   /**
    * Based on jre/lib/content-types.properties
    */
   public static String getContentTypeByFileExtension(final Path file) {
      return URLConnection.getFileNameMap().getContentTypeFor(file.getFileName().toString());
   }

   public static Size getFreeSpace(final Path path) throws IOException {
      return Size.ofBytes(Files.getFileStore(path).getUsableSpace());
   }

   public static Size getFreeTempSpace() throws IOException {
      return Size.ofBytes(Files.getFileStore(getTempDirectory()).getUsableSpace());
   }

   public static Path getTempDirectory() {
      return Paths.get(System.getProperty("java.io.tmpdir"));
   }

   public static Path getWorkingDirectory() {
      return Paths.get("").toAbsolutePath();
   }

   public static Path[] toPaths(final String... filePaths) {
      Args.notNull("filePaths", filePaths);

      final Path[] result = new Path[filePaths.length];
      for (int i = 0, l = filePaths.length; i < l; i++) {
         result[i] = Paths.get(filePaths[i]);
      }
      return result;
   }
}
