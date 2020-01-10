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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.lang3.time.DateFormatUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class FileUtils extends org.apache.commons.io.FileUtils {
   private static final Logger LOG = Logger.create();

   private static final Queue<File> _FILES_TO_DELETE_ON_SHUTDOWN = new ConcurrentLinkedQueue<>();
   private static final Queue<File> _DIRS_TO_DELETE_ON_SHUTDOWN = new ConcurrentLinkedQueue<>();
   private static final AtomicLong _FILE_UNIQUE_ID = new AtomicLong();

   static {
      Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
         @Override
         public void run() {
            for (final File file : _FILES_TO_DELETE_ON_SHUTDOWN) {
               try {
                  LOG.debug("Deleting %s...", file);
                  forceDelete(file);
               } catch (final FileNotFoundException ex) {
                  // ignore
               } catch (final IOException ex) {
                  LOG.error("Failed to delete file: " + file, ex);
               }
            }

            for (final File directory : _DIRS_TO_DELETE_ON_SHUTDOWN) {
               try {
                  LOG.debug("Cleaning %s...", directory);
                  cleanDirectory(directory);
               } catch (final FileNotFoundException ex) {
                  // ignore
               } catch (final IOException ex) {
                  LOG.error("Failed to delete directory: " + directory, ex);
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
   public static File backupFile(final File fileToBackup) throws IOException {
      Args.notNull("fileToBackup", fileToBackup);

      return backupFile(fileToBackup, fileToBackup.getParentFile());
   }

   /**
    * Creates a backup of the given file if it exists, otherwise returns with null.
    *
    * @return a File object representing the backup copy or null if no backup was created because the file to backup did not exist
    */
   public static File backupFile(final File fileToBackup, final File backupFolder) throws IOException {
      Args.notNull("fileToBackup", fileToBackup);
      Args.notNull("backupFolder", backupFolder);

      if (!backupFolder.isDirectory())
         throw new IllegalArgumentException("backupFolder [" + backupFolder + "] is not a directory");

      if (fileToBackup.exists()) {
         Args.isFileReadable("fileToBackup", fileToBackup); // ensure it is actually a file

         final File backupFile = new File(backupFolder, FilenameUtils.getBaseName(fileToBackup) + "_" + DateFormatUtils.format(System.currentTimeMillis(),
            "yyyy-MM-dd_hhmmss") + "." + FilenameUtils.getExtension(fileToBackup));
         LOG.debug("Backing up [%s] to [%s]", fileToBackup, backupFile);
         copyFile(fileToBackup, backupFile, true);
         return backupFile;
      }
      return null;
   }

   public static void cleanDirectory(final File directory, final Date deleteFilesOlderThan, final boolean recursive) {
      Args.notNull("directory", directory);

      cleanDirectory(directory, deleteFilesOlderThan, recursive, null);
   }

   public static void cleanDirectory(final File directory, final Date deleteFilesOlderThan, final boolean recursive, final FilenameFilter filenameFilter) {
      Args.notNull("directory", directory);
      Args.notNull("deleteFilesOlderThan", deleteFilesOlderThan);

      if (!directory.isDirectory())
         return;

      final File[] files = directory.listFiles(filenameFilter);
      if (files != null) {
         for (final File currFile : files)
            if (currFile.isFile()) {
               if (currFile.lastModified() < deleteFilesOlderThan.getTime()) {
                  currFile.delete();
               }
            } else if (recursive && currFile.isDirectory()) {
               cleanDirectory(currFile, deleteFilesOlderThan, true, filenameFilter);
            }
      }
   }

   public static void cleanDirectory(final File directory, final int deleteFilesOlderThanXDays, final boolean recursive) {
      Args.notNull("directory", directory);

      cleanDirectory(directory, deleteFilesOlderThanXDays, recursive, null);
   }

   public static void cleanDirectory(final File directory, final int deleteFilesOlderThanXDays, final boolean recursive, final FilenameFilter filenameFilter) {
      Args.notNull("directory", directory);

      final Calendar c = Calendar.getInstance();
      c.add(Calendar.DAY_OF_MONTH, -deleteFilesOlderThanXDays);

      cleanDirectory(directory, c.getTime(), recursive, filenameFilter);
   }

   public static void cleanDirectoryOnExit(final File directory) {
      _DIRS_TO_DELETE_ON_SHUTDOWN.add(directory);
   }

   /**
    * Creates a temp directory that will be automatically deleted on JVM exit.
    *
    * @param parentDirectory if null, then create in system temp directory
    */
   public static File createTempDirectory(final File parentDirectory, final String prefix, final String extension) {
      final File tmpDir = createUniqueDirectory(parentDirectory == null ? getTempDirectory() : parentDirectory, prefix, extension);
      forceDeleteOnExit(tmpDir);
      return tmpDir;
   }

   /**
    * Creates a temp directory that will be automatically deleted on JVM exit.
    */
   public static File createTempDirectory(final String prefix, final String extension) {
      final File tmpDir = createUniqueDirectory(getTempDirectory(), prefix, extension);
      forceDeleteOnExit(tmpDir);
      return tmpDir;
   }

   /**
    * @param parentDirectory if null, then create in current directory
    */
   public static File createUniqueDirectory(final File parentDirectory, final String prefix, final String extension) {
      while (true) {
         final String name = (prefix == null ? "" : prefix) + _FILE_UNIQUE_ID.getAndIncrement() + (extension == null ? extension : "");
         final File dir = new File(parentDirectory, name);
         if (!dir.exists() && dir.mkdir())
            return dir;
      }
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<File> find(final File searchRootPath, final String globPattern, final boolean includeFiles, final boolean includeDirectories)
      throws IOException {
      return find(searchRootPath == null ? null : searchRootPath.getAbsolutePath(), globPattern, includeFiles, includeDirectories);
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<File> find(final String searchRootPath, final String globPattern, final boolean includeFiles, final boolean includeDirectories)
      throws IOException {
      final Collection<File> result = new ArrayList<>();
      find(searchRootPath, globPattern, file -> {
         if (includeDirectories && file.isDirectory()) {
            result.add(file);
         }
         if (includeFiles && file.isFile()) {
            result.add(file);
         }
      });
      return result;
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   @SuppressWarnings("unused")
   public static void find(String searchRootPath, final String globPattern, final EventListener<File> onMatch) throws IOException {
      Args.notNull("globPattern", globPattern);
      Args.notNull("onMatch", onMatch);

      if (Strings.isEmpty(searchRootPath)) {
         searchRootPath = "."; // current directory
      }
      final File searchRoot = new File(new File(searchRootPath).getCanonicalPath());
      searchRootPath = searchRoot.getAbsolutePath();
      final int searchRootLen = searchRootPath.length();

      final String searchRegEx = Strings.globToRegex(globPattern).toString();
      LOG.debug("\n  glob:  %s\n  regex: %s\n  searchRoot: %s", globPattern, searchRegEx, searchRootPath);
      final Pattern filePattern = Pattern.compile("^" + searchRegEx);
      new DirectoryWalker<File>() {
         {
            walk(searchRoot, null);
         }

         @Override
         protected boolean handleDirectory(final File directory, final int depth, final Collection<File> results) throws IOException {
            if (depth == 0)
               return true;
            String folderPath = directory.getAbsolutePath().replace('\\', '/');
            folderPath = folderPath.substring(searchRootLen + 1);
            final boolean isMatch = filePattern.matcher(folderPath).find();
            if (isMatch) {
               onMatch.onEvent(directory);
            }
            return true;
         }

         @Override
         protected void handleFile(final File file, final int depth, final java.util.Collection<File> results) throws IOException {
            String filePath = file.getAbsolutePath().replace('\\', '/');
            filePath = filePath.substring(searchRootLen + 1);
            final boolean isMatch = filePattern.matcher(filePath).find();
            if (isMatch) {
               onMatch.onEvent(file);
            }
         }
      };
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<File> findDirectories(final File searchRootPath, final String globPattern) throws IOException {
      return find(searchRootPath, globPattern, false, true);
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<File> findDirectories(final String searchRootPath, final String globPattern) throws IOException {
      return find(searchRootPath, globPattern, false, true);
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<File> findFiles(final File searchRootPath, final String globPattern) throws IOException {
      return find(searchRootPath, globPattern, true, false);
   }

   /**
    * @param globPattern Pattern in the Glob syntax style, see https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
    */
   public static Collection<File> findFiles(final String searchRootPath, final String globPattern) throws IOException {
      return find(searchRootPath, globPattern, true, false);
   }

   public static void forceDeleteOnExit(final File file) {
      Args.notNull("file", file);
      LOG.debug("Registering %s for deletion on JVM shutdown...", file);
      _FILES_TO_DELETE_ON_SHUTDOWN.add(file);
   }

   /**
    * Based on jre/lib/content-types.properties
    */
   public static String getContentTypeByFileExtension(final String file) {
      return URLConnection.getFileNameMap().getContentTypeFor(file);
   }

   public static long getFreeSpaceInKB(final String path) {
      return new File(path).getFreeSpace() / 1024;
   }

   public static long getFreeTempSpaceInKB() {
      return getTempDirectory().getUsableSpace() / 1024;
   }

   public static String readFileToString(final File file) throws IOException {
      return readFileToString(file, Charset.defaultCharset());
   }

   public static List<String> readLines(final File file) throws IOException {
      return readLines(file, Charset.defaultCharset());
   }

   public static File[] toFiles(final String... filePaths) {
      Args.notNull("filePaths", filePaths);

      final File[] result = new File[filePaths.length];
      for (int i = 0, l = filePaths.length; i < l; i++) {
         result[i] = new File(filePaths[i]);
      }
      return result;
   }

   @SuppressWarnings("resource")
   public static void writeAndClose(final File file, InputStream is) throws IOException {
      if (!(is instanceof BufferedInputStream)) {
         is = new BufferedInputStream(is);
      }
      IOUtils.copyAndClose(is, new BufferedOutputStream(new FileOutputStream(file)));
   }

   public static void writeAndClose(final String file, final InputStream is) throws IOException {
      writeAndClose(new File(file), is);
   }

   public static void writeStringToFile(final String file, final CharSequence data) throws IOException {
      write(new File(file), data, Charset.defaultCharset(), false);
   }

   public static void writeStringToFile(final String file, final CharSequence data, final boolean append) throws IOException {
      write(new File(file), data, Charset.defaultCharset(), append);
   }

   public static void writeStringToFile(final String file, final CharSequence data, final Charset encoding) throws IOException {
      write(new File(file), data, encoding, false);
   }

   public static void writeStringToFile(final String file, final CharSequence data, final Charset encoding, final boolean append) throws IOException {
      write(new File(file), data, encoding, append);
   }

   public static void writeStringToFile(final String file, final CharSequence data, final String encoding) throws IOException {
      write(new File(file), data, encoding, false);
   }

   public static void writeStringToFile(final String file, final CharSequence data, final String encoding, final boolean append) throws IOException {
      write(new File(file), data, encoding, append);
   }
}
