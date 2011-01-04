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

import net.sf.jstuff.core.io.FileUtils;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class FileUtilsTest extends TestCase
{
	public void testGetFileBaseName()
	{
		final File f = new File("foo/bar.java");
		assertEquals("bar", FileUtils.getFileBaseName(f));
	}

	public void testGetFileExtension()
	{
		final File f = new File("foo/bar.java");
		assertEquals("java", FileUtils.getFileExtension(f));
	}
}
