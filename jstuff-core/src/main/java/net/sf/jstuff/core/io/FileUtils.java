/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.event.EventListener;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class FileUtils extends org.apache.commons.io.FileUtils
{
	private static final Logger LOG = Logger.create();

	private static final Queue<File> filesToDeleteOnShutdown = new ConcurrentLinkedQueue<File>();

	static
	{
		Runtime.getRuntime().addShutdownHook(new java.lang.Thread()
		{
			@Override
			public void run()
			{
				for (final File file : filesToDeleteOnShutdown)
					try
				{
						LOG.debug("Deleting %s...", file);
						forceDelete(file);
				}
				catch (final IOException ex)
				{
					LOG.error(ex);
				}
			}
		});
	}

	/**
	 * Creates a backup of the given file if it exists, otherwise returns with null.
	 *
	 * @return a File object representing the backup copy or null if no backup was created because the file to backup did not exist
	 */
	public static File backupFile(final File fileToBackup) throws IOException
	{
		Args.notNull("fileToBackup", fileToBackup);

		return backupFile(fileToBackup, fileToBackup.getParentFile());
	}

	/**
	 * Creates a backup of the given file if it exists, otherwise returns with null.
	 *
	 * @return a File object representing the backup copy or null if no backup was created because the file to backup did not exist
	 */
	public static File backupFile(final File fileToBackup, final File backupFolder) throws IOException
	{
		Args.notNull("fileToBackup", fileToBackup);
		Args.notNull("backupFolder", backupFolder);

		if (!backupFolder.isDirectory()) throw new IllegalArgumentException("backupFolder [" + backupFolder + "] is not a directory");

		if (fileToBackup.exists())
		{
			Assert.isFileReadable(fileToBackup); // ensure it is actually a file

			final File backupFile = new File(backupFolder, FilenameUtils.getBaseName(fileToBackup) + "_"
					+ DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_hhmmss") + "." + FilenameUtils.getExtension(fileToBackup));
			LOG.debug("Backing up [%s] to [%s]", fileToBackup, backupFile);
			copyFile(fileToBackup, backupFile, true);
			return backupFile;
		}
		return null;
	}

	public static void cleanDirectory(final File directory, final Date deleteFilesOlderThan, final boolean recursive)
	{
		Args.notNull("directory", directory);

		cleanDirectory(directory, deleteFilesOlderThan, recursive, null);
	}

	public static void cleanDirectory(final File directory, final Date deleteFilesOlderThan, final boolean recursive, final FilenameFilter filenameFilter)
	{
		Args.notNull("directory", directory);
		Args.notNull("deleteFilesOlderThan", deleteFilesOlderThan);

		if (!directory.isDirectory()) return;

		for (final File currFile : directory.listFiles(filenameFilter))
			if (currFile.isFile())
			{
				if (currFile.lastModified() < deleteFilesOlderThan.getTime()) currFile.delete();
			}
			else if (recursive && currFile.isDirectory()) cleanDirectory(currFile, deleteFilesOlderThan, true, filenameFilter);
	}

	public static void cleanDirectory(final File directory, final int deleteFilesOlderThanXDays, final boolean recursive)
	{
		Args.notNull("directory", directory);

		cleanDirectory(directory, deleteFilesOlderThanXDays, recursive, null);
	}

	public static void cleanDirectory(final File directory, final int deleteFilesOlderThanXDays, final boolean recursive, final FilenameFilter filenameFilter)
	{
		Args.notNull("directory", directory);

		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -deleteFilesOlderThanXDays);

		cleanDirectory(directory, c.getTime(), recursive, filenameFilter);
	}

	public static Collection<File> find(final File searchRootPath, final String globPattern, final boolean includeFiles, final boolean includeDirectories)
			throws IOException
			{
		return find(searchRootPath == null ? null : searchRootPath.getAbsolutePath(), globPattern, includeFiles, includeDirectories);
			}

	public static Collection<File> find(final String searchRootPath, final String globPattern, final boolean includeFiles, final boolean includeDirectories)
			throws IOException
			{
		final Collection<File> result = new ArrayList<File>();
		find(searchRootPath, globPattern, new EventListener<File>()
				{
			public void onEvent(final File file)
			{
				if (file.isDirectory() && includeDirectories) result.add(file);
				if (file.isFile() && includeFiles) result.add(file);
			}
				});
		return result;
			}

	@SuppressWarnings("unused")
	public static void find(String searchRootPath, final String globPattern, final EventListener<File> onMatch) throws IOException
	{
		Args.notNull("globPattern", globPattern);
		Args.notNull("onMatch", onMatch);

		if (StringUtils.isEmpty(searchRootPath)) searchRootPath = ".";

		final String searchRoot = new File(FilenameUtils.concat(searchRootPath,
				StringUtils.substringBeforeLast(StringUtils.substringBefore(globPattern, "*"), "/"))).getAbsolutePath();

		final String searchRegEx = StringUtils.globToRegex(globPattern).toString();
		LOG.debug("\n  glob:  %s\n  regex: %s\n  searchRoot: %s", globPattern, searchRegEx, searchRoot);
		final Pattern filePattern = Pattern.compile(searchRegEx);
		new DirectoryWalker<File>()
		{
			{
				walk(new File(searchRoot), null);
			}

			@Override
			protected boolean handleDirectory(final File directory, final int depth, final Collection<File> results) throws IOException
			{
				final String filePath = directory.getCanonicalPath().replace('\\', '/');
				final boolean isMatch = filePattern.matcher(filePath).find();
				if (isMatch) onMatch.onEvent(directory);
				return true;
			}

			@Override
			protected void handleFile(final File file, final int depth, final java.util.Collection<File> results) throws IOException
			{
				final String filePath = file.getCanonicalPath().replace('\\', '/');
				final boolean isMatch = filePattern.matcher(filePath).find();
				if (isMatch) onMatch.onEvent(file);
			}
		};
	}

	public static Collection<File> findFiles(final File searchRootPath, final String globPattern) throws IOException
	{
		return find(searchRootPath, globPattern, true, false);
	}

	public static Collection<File> findFiles(final String searchRootPath, final String globPattern) throws IOException
	{
		return find(searchRootPath, globPattern, true, false);
	}

	public static Collection<File> findFolders(final File searchRootPath, final String globPattern) throws IOException
	{
		return find(searchRootPath, globPattern, false, true);
	}

	public static Collection<File> findFolders(final String searchRootPath, final String globPattern) throws IOException
	{
		return find(searchRootPath, globPattern, false, true);
	}

	public static void forceDeleteOnExit(final File file) throws IOException
	{
		Args.notNull("file", file);
		LOG.debug("Registering %s for deletion on JVM shutdown...", file);
		filesToDeleteOnShutdown.add(file);
	}

	public static String getCurrentPath()
	{
		try
		{
			return new File(".").getCanonicalPath();
		}
		catch (final IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	public static long getFreeSpaceInKB(final String path) throws IOException
	{
		return FileSystemUtils.freeSpaceKb(path);
	}

	public static long getFreeTempSpaceInKB() throws IOException
	{
		return FileSystemUtils.freeSpaceKb(getTempDirectoryPath());
	}

	public static File[] toFiles(final String... filePaths)
	{
		Args.notNull("filePaths", filePaths);

		final File[] result = new File[filePaths.length];
		for (int i = 0, l = filePaths.length; i < l; i++)
			result[i] = new File(filePaths[i]);
		return result;
	}

	@SuppressWarnings("resource")
	public static void writeAndClose(final File file, InputStream is) throws IOException
	{
		if (!(is instanceof BufferedInputStream)) is = new BufferedInputStream(is);
		IOUtils.copyAndClose(is, new BufferedOutputStream(new FileOutputStream(file)));
	}

	public static void writeAndClose(final String file, final InputStream is) throws IOException
	{
		writeAndClose(new File(file), is);
	}

	public static void writeStringToFile(final String file, final CharSequence data) throws IOException
	{
		write(new File(file), data, null, false);
	}

	public static void writeStringToFile(final String file, final CharSequence data, final boolean append) throws IOException
	{
		write(new File(file), data, null, append);
	}

	public static void writeStringToFile(final String file, final CharSequence data, final String encoding) throws IOException
	{
		write(new File(file), data, encoding, false);
	}

	public static void writeStringToFile(final String file, final CharSequence data, final String encoding, final boolean append) throws IOException
	{
		write(new File(file), data, encoding, append);
	}

}
