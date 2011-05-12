/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class FileUtils extends org.apache.commons.io.FileUtils
{
	private static Logger LOG = Logger.get();

	public static File[] asFileArray(final String... filePaths)
	{
		Assert.argumentNotNull("filePaths", filePaths);

		final File[] result = new File[filePaths.length];
		for (int i = 0, l = filePaths.length; i < l; i++)
			result[i] = new File(filePaths[i]);
		return result;
	}

	/**
	 * Creates a backup of the given file if it exists, otherwise returns with null.
	 * 
	 * @return a File object representing the backup copy or null if no backup was created because the file to backup did not exist
	 */
	public static File backupFile(final File fileToBackup) throws IOException
	{
		Assert.argumentNotNull("fileToBackup", fileToBackup);

		return backupFile(fileToBackup, fileToBackup.getParentFile());
	}

	/**
	 * Creates a backup of the given file if it exists, otherwise returns with null.
	 * 
	 * @return a File object representing the backup copy or null if no backup was created because the file to backup did not exist
	 */
	public static File backupFile(final File fileToBackup, final File backupFolder) throws IOException
	{
		Assert.argumentNotNull("fileToBackup", fileToBackup);
		Assert.argumentNotNull("backupFolder", backupFolder);

		if (!backupFolder.isDirectory())
			throw new IllegalArgumentException("backupFolder [" + backupFolder + "] is not a directory");

		if (fileToBackup.exists())
		{
			Assert.isFileReadable(fileToBackup); // ensure it is actually a file

			final File backupFile = new File(backupFolder, getFileBaseName(fileToBackup) + "_"
					+ DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_hhmmss") + "."
					+ getFileExtension(fileToBackup));
			LOG.debug("Backing up [%s] to [%s]", fileToBackup, backupFile);
			copyFile(fileToBackup, backupFile, true);
			return backupFile;
		}
		return null;
	}

	public static void cleanDirectory(final File directory, final Date oldestFileDate, final boolean recursive)
	{
		Assert.argumentNotNull("directory", directory);

		cleanDirectory(directory, oldestFileDate, recursive, null);
	}

	public static void cleanDirectory(final File directory, final Date purgeFilesOlderThan, final boolean recursive,
			final FilenameFilter filenameFilter)
	{
		Assert.argumentNotNull("directory", directory);
		Assert.argumentNotNull("purgeFilesOlderThan", purgeFilesOlderThan);

		if (!directory.isDirectory()) return;

		for (final File currFile : directory.listFiles(filenameFilter))
			if (currFile.isFile())
			{
				if (currFile.lastModified() < purgeFilesOlderThan.getTime()) currFile.delete();
			}
			else if (recursive && currFile.isDirectory())
				cleanDirectory(currFile, purgeFilesOlderThan, true, filenameFilter);
	}

	public static void cleanDirectory(final File directory, final int purgeFilesOlderThanXDays, final boolean recursive)
	{
		Assert.argumentNotNull("directory", directory);

		cleanDirectory(directory, purgeFilesOlderThanXDays, recursive, null);
	}

	public static void cleanDirectory(final File directory, final int purgeFilesOlderThanXDays,
			final boolean recursive, final FilenameFilter filenameFilter)
	{
		Assert.argumentNotNull("directory", directory);

		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -purgeFilesOlderThanXDays);

		cleanDirectory(directory, c.getTime(), recursive, filenameFilter);
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

	/**
	 * @return the file name without file extension
	 */
	public static String getFileBaseName(final File file)
	{
		Assert.argumentNotNull("file", file);

		final int lastDotPos = file.getName().lastIndexOf('.');
		if (lastDotPos > 1 && lastDotPos <= file.getName().length() - 2)
			return file.getName().substring(0, lastDotPos);
		return "";
	}

	public static String getFileExtension(final File file)
	{
		Assert.argumentNotNull("file", file);

		final int lastDotPos = file.getName().lastIndexOf('.');
		if (0 < lastDotPos && lastDotPos <= file.getName().length() - 2)
			return file.getName().substring(lastDotPos + 1);
		return "";
	}

	public static File getTempDirecory()
	{
		return new File(getTempDirectoryPath());
	}

	public static String getTempDirectoryPath()
	{
		return System.getProperty("java.io.tmpdir");
	}

	protected FileUtils()
	{
		super();
	}
}
