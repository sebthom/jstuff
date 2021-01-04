/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.SystemUtils;
import net.sf.jstuff.core.functional.LongBiConsumer;
import net.sf.jstuff.core.io.channel.DelegatingWritableByteChannel;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.math.NumericalSystem;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class MoreFiles {
   private static final Logger LOG = Logger.create();

   private static final AtomicLong _FILE_UNIQUE_ID = new AtomicLong();
   private static final Queue<Path> _FILES_TO_DELETE_ON_SHUTDOWN = new ConcurrentLinkedQueue<>();

   private static final LinkOption[] NOFOLLOW_LINKS = {LinkOption.NOFOLLOW_LINKS};
   private static final OpenOption[] DEFAULT_FILE_READ_OPTIONS = {StandardOpenOption.READ};
   private static final OpenOption[] DEFAULT_FILE_WRITE_OPTIONS = { //
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
               } catch (final FileNotFoundException | NoSuchFileException ex) {
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

      try (FileChannel file1Ch = FileChannel.open(file1, DEFAULT_FILE_READ_OPTIONS);
           FileChannel file2Ch = FileChannel.open(file2, DEFAULT_FILE_READ_OPTIONS)) {
         final MappedByteBuffer buff1 = file1Ch.map(FileChannel.MapMode.READ_ONLY, 0, file1Ch.size());
         final MappedByteBuffer buff2 = file2Ch.map(FileChannel.MapMode.READ_ONLY, 0, file2Ch.size());
         return buff1.equals(buff2);
      }
   }

   /**
    * @experimental
    *
    * @see java.nio.file.attribute.AclFileAttributeView
    * @see java.nio.file.attribute.DosFileAttributes
    * @see java.nio.file.attribute.FileOwnerAttributeView
    * @see java.nio.file.attribute.PosixFileAttributes
    * @see java.nio.file.attribute.UserDefinedFileAttributeView
    */
   @SuppressWarnings("resource")
   public static void copyAttributes(final Path source, final Path target, final boolean copyACL) throws IOException {
      final FileSystem sourceFS = source.getFileSystem();
      final FileSystem targetFS = target.getFileSystem();

      final FileSystemProvider sourceFSP = sourceFS.provider();
      final FileSystemProvider targetFSP = targetFS.provider();

      final Set<String> sourceSupportedAttrs = sourceFS.supportedFileAttributeViews();
      final Set<String> targetSupportedAttrs = targetFS.supportedFileAttributeViews();

      if (sourceSupportedAttrs.contains("dos") && targetSupportedAttrs.contains("dos")) {
         final DosFileAttributes sourceDosAttrs = sourceFSP.readAttributes(source, DosFileAttributes.class, NOFOLLOW_LINKS);
         final DosFileAttributeView targetDosAttrs = targetFSP.getFileAttributeView(target, DosFileAttributeView.class, NOFOLLOW_LINKS);
         targetDosAttrs.setArchive(sourceDosAttrs.isArchive());
         targetDosAttrs.setHidden(sourceDosAttrs.isHidden());
         targetDosAttrs.setReadOnly(sourceDosAttrs.isReadOnly());
         targetDosAttrs.setSystem(sourceDosAttrs.isSystem());

         if (copyACL && sourceSupportedAttrs.contains("acl") && targetSupportedAttrs.contains("acl")) {
            final AclFileAttributeView sourceAclAttrs = sourceFSP.getFileAttributeView(source, AclFileAttributeView.class, NOFOLLOW_LINKS);
            final AclFileAttributeView targetAclAttrs = targetFSP.getFileAttributeView(target, AclFileAttributeView.class, NOFOLLOW_LINKS);
            targetAclAttrs.setAcl(sourceAclAttrs.getAcl());
            if (SystemUtils.isRunningAsAdmin()) {
               targetAclAttrs.setOwner(sourceAclAttrs.getOwner());
            }
         }
         copyUserAttrs(source, target);
         copyTimeAttrs(sourceDosAttrs, targetDosAttrs);
         return;
      }

      if (sourceSupportedAttrs.contains("posix") && targetSupportedAttrs.contains("posix")) {
         final PosixFileAttributes sourcePosixAttrs = sourceFSP.readAttributes(source, PosixFileAttributes.class, NOFOLLOW_LINKS);
         final PosixFileAttributeView targetPosixAttrs = targetFSP.getFileAttributeView(target, PosixFileAttributeView.class, NOFOLLOW_LINKS);
         if (copyACL) {
            targetPosixAttrs.setOwner(sourcePosixAttrs.owner());
            targetPosixAttrs.setGroup(sourcePosixAttrs.group());
            targetPosixAttrs.setPermissions(sourcePosixAttrs.permissions());
         }
         copyUserAttrs(source, target);
         copyTimeAttrs(sourcePosixAttrs, targetPosixAttrs);
         return;
      }

      if (copyACL && sourceSupportedAttrs.contains("owner") && targetSupportedAttrs.contains("owner")) {
         Files.setOwner(target, Files.getOwner(source, NOFOLLOW_LINKS));
      }
      copyUserAttrs(source, target);
      copyTimeAttrs( //
         sourceFSP.readAttributes(source, BasicFileAttributes.class, NOFOLLOW_LINKS), //
         targetFSP.getFileAttributeView(target, BasicFileAttributeView.class, NOFOLLOW_LINKS) //
      );
   }

   /**
    * @param onBytesWritten LongBiConsumer#accept(long bytesWritten, long totalBytesWritten)
    */
   @SuppressWarnings("resource")
   public static void copyContent(final FileChannel source, final FileChannel target, final LongBiConsumer onBytesWritten) throws IOException {
      Args.notNull("onBytesWritten", onBytesWritten);
      Args.notNull("source", source);
      Args.notNull("target", target);

      try ( //
           WritableByteChannel outCh = new DelegatingWritableByteChannel(target, onBytesWritten) //
      ) {
         final long size = source.size();
         long position = 0;

         if (SystemUtils.IS_OS_WINDOWS) {
            // using a fixed buffer with 64Mb - 32Kb is for some reason slightly faster on Windows (even on Win 10x64)
            // see also https://stackoverflow.com/q/7379469/5116073
            while (position < size) {
               position += source.transferTo(position, (long) 64 * 1024 * 1024 - 32 * 1024, outCh);
            }
         } else {
            while (position < size) {
               position += source.transferTo(position, size - position, outCh);
            }
         }
      }
   }

   public static void copyContent(final Path source, final Path target) throws IOException {
      copyContent(source, target, (bytesWritten, totalBytesWritten) -> { /* ignore */ });
   }

   /**
    * @param onBytesWritten LongBiConsumer#accept(long bytesWritten, long totalBytesWritten)
    */
   public static void copyContent(final Path source, final Path target, final LongBiConsumer onBytesWritten) throws IOException {
      Args.notNull("onBytesWritten", onBytesWritten);
      Args.notNull("source", source);
      Args.notNull("target", target);

      try (FileChannel sourceCh = FileChannel.open(source, DEFAULT_FILE_READ_OPTIONS);
           FileChannel targetCh = FileChannel.open(target, DEFAULT_FILE_WRITE_OPTIONS)) {
         copyContent(sourceCh, targetCh, onBytesWritten);
      }
   }

   private static void copyTimeAttrs(final BasicFileAttributes sourceAttrs, final BasicFileAttributeView targetAttrs) throws IOException {
      targetAttrs.setTimes(sourceAttrs.lastModifiedTime(), sourceAttrs.lastAccessTime(), sourceAttrs.creationTime());
   }

   @SuppressWarnings("resource")
   private static void copyUserAttrs(final Path source, final Path target) throws IOException {
      final FileSystem sourceFS = source.getFileSystem();
      final FileSystem targetFS = target.getFileSystem();

      final Set<String> sourceSupportedAttrs = sourceFS.supportedFileAttributeViews();
      final Set<String> targetSupportedAttrs = targetFS.supportedFileAttributeViews();
      if (sourceSupportedAttrs.contains("user") && targetSupportedAttrs.contains("user")) {
         final FileSystemProvider sourceFSP = sourceFS.provider();

         final UserDefinedFileAttributeView sourceUserAttrs = sourceFSP.getFileAttributeView(source, UserDefinedFileAttributeView.class, NOFOLLOW_LINKS);
         final List<String> entries = sourceUserAttrs.list();
         if (!entries.isEmpty()) {
            final FileSystemProvider targetFSP = targetFS.provider();
            final UserDefinedFileAttributeView targetUserAttrs = targetFSP.getFileAttributeView(target, UserDefinedFileAttributeView.class, NOFOLLOW_LINKS);
            ByteBuffer buf = null;
            for (final String entry : entries) {
               final int entrySize = sourceUserAttrs.size(entry);
               if (buf == null || entrySize > buf.capacity()) {
                  buf = ByteBuffer.allocate(entrySize);
               } else {
                  buf.clear();
               }
               sourceUserAttrs.read(entry, buf);
               buf.flip();
               targetUserAttrs.write(entry, buf);
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
         final String dirName = (prefix == null ? "" : prefix) //
            + NumericalSystem.BASE16.encode(_FILE_UNIQUE_ID.getAndIncrement()) //
            + (extension == null ? extension : "");
         final Path dir = parentDirectory.resolve(dirName);
         if (!Files.exists(dir, NOFOLLOW_LINKS)) {
            try {
               return Files.createDirectories(dir);
            } catch (final FileAlreadyExistsException ex) {
               // ignore
            }
         }
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

   /**
    * Deletes the given file or directory. Directories are deleted recursively.
    *
    * @return true if deleted, false if didn't exist
    */
   public static boolean forceDelete(final Path fileOrDirectory) throws IOException {
      if (Files.isDirectory(fileOrDirectory, NOFOLLOW_LINKS)) {
         Files.walkFileTree(fileOrDirectory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
               if (exc != null && !(exc instanceof NoSuchFileException) && !(exc instanceof FileNotFoundException))
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
         return true;
      }
      return Files.deleteIfExists(fileOrDirectory);
   }

   public static void forceDeleteNowOrOnExit(final Path fileOrDirectory) {
      Args.notNull("fileOrDirectory", fileOrDirectory);

      try {
         forceDelete(fileOrDirectory);
      } catch (final IOException ex) {
         LOG.debug(ex, "Registering %s for deletion on JVM shutdown...", fileOrDirectory);
         _FILES_TO_DELETE_ON_SHUTDOWN.add(fileOrDirectory);
      }
   }

   public static void forceDeleteOnExit(final Path fileOrDirectory) {
      Args.notNull("fileOrDirectory", fileOrDirectory);
      LOG.debug("Registering %s for deletion on JVM shutdown...", fileOrDirectory);
      _FILES_TO_DELETE_ON_SHUTDOWN.add(fileOrDirectory);
   }

   public static boolean forceDeleteQuietly(final Path fileOrDirectory) {
      try {
         forceDelete(fileOrDirectory);
         return true;
      } catch (final IOException e) {
         return false;
      }
   }

   /**
    * Based on jre/lib/content-types.properties
    */
   public static String getContentTypeByFileExtension(final Path file) {
      return URLConnection.getFileNameMap().getContentTypeFor(file.getFileName().toString());
   }

   public static String getContentTypeByFileExtension(final String fileName) {
      return URLConnection.getFileNameMap().getContentTypeFor(fileName);
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

   @SuppressWarnings("resource")
   public static BasicFileAttributes readAttributes(final Path path) throws IOException {
      Args.notNull("path", path);
      final FileSystem fs = path.getFileSystem();
      if (fs.supportedFileAttributeViews().contains("dos"))
         return Files.readAttributes(path, DosFileAttributes.class, NOFOLLOW_LINKS);
      if (fs.supportedFileAttributeViews().contains("posix"))
         return Files.readAttributes(path, PosixFileAttributes.class, NOFOLLOW_LINKS);
      return Files.readAttributes(path, BasicFileAttributes.class, NOFOLLOW_LINKS);
   }

   public static String readFileToString(final Path file) throws IOException {
      return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
   }

   public static String readFileToString(final Path file, final Charset charset) throws IOException {
      return new String(Files.readAllBytes(file), charset);
   }

   public static Path[] toPaths(final String... filePaths) {
      Args.notNull("filePaths", filePaths);

      final Path[] result = new Path[filePaths.length];
      for (int i = 0, l = filePaths.length; i < l; i++) {
         if (filePaths[i] != null) {
            result[i] = Paths.get(filePaths[i]);
         }
      }
      return result;
   }
}
