/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.SystemUtils;
import net.sf.jstuff.core.functional.BiLongConsumer;
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

   private static final @NonNull LinkOption[] NOFOLLOW_LINKS = {LinkOption.NOFOLLOW_LINKS};
   private static final @NonNull OpenOption[] DEFAULT_FILE_READ_OPTIONS = {StandardOpenOption.READ};
   private static final @NonNull OpenOption[] DEFAULT_FILE_WRITE_OPTIONS = { //
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

   /**
    * Creates a backup of the given file if it exists, otherwise returns with null.
    *
    * @return a File object representing the backup copy or null if no backup was created because the file to backup did not exist
    */
   @Nullable
   public static File backupFile(final Path fileToBackup) throws IOException {
      Args.notNull("fileToBackup", fileToBackup);

      final var parentDir = fileToBackup.getParent();
      if (parentDir == null)
         throw new IOException("Cannot backup [" + fileToBackup + "] which has no parent directory!");
      return backupFile(fileToBackup, parentDir);
   }

   /**
    * Creates a backup of the given file if it exists, otherwise returns with null.
    *
    * @return a File object representing the backup copy or null if no backup was created because the file to backup did not exist
    */
   @Nullable
   public static File backupFile(final Path fileToBackup, final Path backupFolder) throws IOException {
      Args.notNull("fileToBackup", fileToBackup);
      Args.notNull("backupFolder", backupFolder);

      Args.isDirectoryReadable("backupFolder", backupFolder);

      if (Files.exists(fileToBackup)) {
         Args.isFileReadable("fileToBackup", fileToBackup); // ensure it is actually a file

         final var backupFile = new File( //
            backupFolder.toFile(), //
            FilenameUtils.getBaseName(fileToBackup) //
                  + "_" + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_hhmmss") //
                  + "." + FilenameUtils.getExtension(fileToBackup) //
         );
         LOG.debug("Backing up [%s] to [%s]", fileToBackup, backupFile);
         FileUtils.copyFile(fileToBackup.toFile(), backupFile, true);
         return backupFile;
      }
      return null;
   }

   public static boolean contentEquals(final @Nullable Path file1, final @Nullable Path file2) throws IOException {
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

      if (sourceSupportedAttrs.contains("posix") && targetSupportedAttrs.contains("posix")) {
         try {
            final PosixFileAttributes sourcePosixAttrs = sourceFSP.readAttributes(source, PosixFileAttributes.class, NOFOLLOW_LINKS);
            final PosixFileAttributeView targetPosixAttrs = targetFSP.getFileAttributeView(target, PosixFileAttributeView.class,
               NOFOLLOW_LINKS);
            if (copyACL) {
               targetPosixAttrs.setOwner(sourcePosixAttrs.owner());
               targetPosixAttrs.setGroup(sourcePosixAttrs.group());
               targetPosixAttrs.setPermissions(sourcePosixAttrs.permissions());
            }
            copyUserAttrs(source, target);
            copyTimeAttrs(sourcePosixAttrs, targetPosixAttrs);
            return;
         } catch (final java.nio.file.FileSystemException ex) {
            // java.nio.file.FileSystemException: <PATH>: Operation not supported
            LOG.warn(ex);
         }
      }

      if (sourceSupportedAttrs.contains("dos") && targetSupportedAttrs.contains("dos")) {
         try {
            final DosFileAttributes sourceDosAttrs = sourceFSP.readAttributes(source, DosFileAttributes.class, NOFOLLOW_LINKS);
            final DosFileAttributeView targetDosAttrs = targetFSP.getFileAttributeView(target, DosFileAttributeView.class, NOFOLLOW_LINKS);
            targetDosAttrs.setArchive(sourceDosAttrs.isArchive());
            targetDosAttrs.setHidden(sourceDosAttrs.isHidden());
            targetDosAttrs.setReadOnly(sourceDosAttrs.isReadOnly());
            targetDosAttrs.setSystem(sourceDosAttrs.isSystem());

            if (copyACL && sourceSupportedAttrs.contains("acl") && targetSupportedAttrs.contains("acl")) {
               final AclFileAttributeView sourceAclAttrs = sourceFSP.getFileAttributeView(source, AclFileAttributeView.class,
                  NOFOLLOW_LINKS);
               final AclFileAttributeView targetAclAttrs = targetFSP.getFileAttributeView(target, AclFileAttributeView.class,
                  NOFOLLOW_LINKS);
               targetAclAttrs.setAcl(sourceAclAttrs.getAcl());
               if (SystemUtils.isRunningAsAdmin()) {
                  targetAclAttrs.setOwner(sourceAclAttrs.getOwner());
               }
            }
            copyUserAttrs(source, target);
            copyTimeAttrs(sourceDosAttrs, targetDosAttrs);
            return;
         } catch (final java.nio.file.FileSystemException ex) {
            // java.nio.file.FileSystemException: <PATH>: Operation not supported
            LOG.warn(ex);
         }
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
   public static void copyContent(final FileChannel source, final FileChannel target, final BiLongConsumer onBytesWritten)
         throws IOException {
      Args.notNull("source", source);
      Args.notNull("target", target);
      Args.notNull("onBytesWritten", onBytesWritten);

      try (var outCh = new DelegatingWritableByteChannel(target, onBytesWritten)) {
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
    * @param buffer the buffer to use for the copy
    * @param onBeforeWrite a callback that is called before each write operation
    */
   public static void copyContent(final Path source, final Path target, final ByteBuffer buffer, final Consumer<ByteBuffer> onBeforeWrite)
         throws IOException {
      Args.notNull("source", source);
      Args.notNull("target", target);
      Args.notNull("onBeforeWrite", onBeforeWrite);

      try (var in = Files.newByteChannel(source, DEFAULT_FILE_READ_OPTIONS);
           var out = Files.newByteChannel(target, DEFAULT_FILE_WRITE_OPTIONS)) {
         while (in.read(buffer) > -1) {
            buffer.flip();
            onBeforeWrite.accept(buffer);
            out.write(buffer);
            buffer.compact();
         }
         buffer.flip();
         while (buffer.hasRemaining()) {
            onBeforeWrite.accept(buffer);
            out.write(buffer);
         }
      }
   }

   /**
    * @param onBytesWritten LongBiConsumer#accept(long bytesWritten, long totalBytesWritten)
    */
   public static void copyContent(final Path source, final Path target, final BiLongConsumer onBytesWritten) throws IOException {
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

         final UserDefinedFileAttributeView sourceUserAttrs = sourceFSP.getFileAttributeView(source, UserDefinedFileAttributeView.class,
            NOFOLLOW_LINKS);
         final List<String> entries = sourceUserAttrs.list();
         if (!entries.isEmpty()) {
            final FileSystemProvider targetFSP = targetFS.provider();
            final UserDefinedFileAttributeView targetUserAttrs = targetFSP.getFileAttributeView(target, UserDefinedFileAttributeView.class,
               NOFOLLOW_LINKS);
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
   public static Path createTempDirectory(final @Nullable Path parentDirectory, final String prefix, final String extension)
         throws IOException {
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
   public static Path createUniqueDirectory(@Nullable Path parentDirectory, final @Nullable String prefix, final @Nullable String extension)
         throws IOException {
      if (parentDirectory == null) {
         parentDirectory = getWorkingDirectory();
      }
      while (true) {
         final String dirName = Strings.emptyIfNull(prefix) //
               + NumericalSystem.BASE16.encode(_FILE_UNIQUE_ID.getAndIncrement()) //
               + Strings.emptyIfNull(extension);
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
   public static Collection<Path> find(final Path searchRoot, final String globPattern, final boolean includeFiles,
         final boolean includeDirectories) throws IOException {
      final var result = new ArrayList<Path>();
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
   public static void find(Path searchRoot, final String globPattern, final @Nullable Consumer<Path> onDirMatch,
         final @Nullable Consumer<Path> onFileMatch) throws IOException {
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
            public FileVisitResult postVisitDirectory(final Path dir, final @Nullable IOException exc) throws IOException {
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
   @Nullable
   public static String getContentTypeByFileExtension(final Path file) {
      return getContentTypeByFileExtension(file.getFileName().toString());
   }

   @Nullable
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
      return SystemUtils.getJavaIoTmpDir().toPath();
   }

   public static Path getWorkingDirectory() {
      return Paths.get("").toAbsolutePath().normalize();
   }

   /**
    * @return true if the given path points to a regular file that is executable by this JVM process.
    */
   public static boolean isExecutableFile(final @Nullable Path path) {
      if (path == null)
         return false;

      return Files.isRegularFile(path) && Files.isExecutable(path);
   }

   public static String readAsString(final Path file) throws IOException {
      return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
   }

   public static String readAsString(final Path file, final Charset charset) throws IOException {
      return new String(Files.readAllBytes(file), charset);
   }

   @SuppressWarnings("resource")
   public static BasicFileAttributes readAttributes(final Path path) throws IOException {
      Args.notNull("path", path);

      final FileSystem fs = path.getFileSystem();
      if (fs.supportedFileAttributeViews().contains("posix")) {
         try {
            return Files.readAttributes(path, PosixFileAttributes.class, NOFOLLOW_LINKS);
         } catch (final java.nio.file.FileSystemException ex) {
            // java.nio.file.FileSystemException: <PATH>: Operation not supported
            LOG.warn(ex);
         }
      }
      if (fs.supportedFileAttributeViews().contains("dos")) {
         try {
            return Files.readAttributes(path, DosFileAttributes.class, NOFOLLOW_LINKS);
         } catch (final java.nio.file.FileSystemException ex) {
            // java.nio.file.FileSystemException: <PATH>: Operation not supported
            LOG.warn(ex);
         }
      }
      return Files.readAttributes(path, BasicFileAttributes.class, NOFOLLOW_LINKS);
   }

   public static void write(final Path file, final @Nullable CharSequence text, final Charset charset, final OpenOption... options)
         throws IOException {
      Args.notNull("file", file);
      Args.notNull("charset", charset);

      final CharsetEncoder encoder = charset.newEncoder();
      try (OutputStream out = Files.newOutputStream(file, options);
           var writer = new OutputStreamWriter(out, encoder)) {
         if (text == null) {
            writer.write("null");
            return;
         }
         final int len = text.length();
         if (len <= 10) {
            writer.write(text.toString(), 0, len);
            return;
         }
         final var buff = new char[10];
         final var str = text.toString();
         int charsWritten = 0;
         while (charsWritten < len) {
            final int charsToWrite = Math.min(10, len - charsWritten);
            str.getChars(charsWritten, charsWritten + charsToWrite, buff, 0);
            writer.write(buff, 0, charsToWrite);
            charsWritten += charsToWrite;
         }
      }
   }

   public static void write(final Path file, final CharSequence text, final OpenOption... options) throws IOException {
      write(file, text, StandardCharsets.UTF_8, options);
   }
}
