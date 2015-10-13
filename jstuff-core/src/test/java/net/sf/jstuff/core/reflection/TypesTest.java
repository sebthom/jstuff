/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
package net.sf.jstuff.core.reflection;

import java.io.File;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.helpers.NOPLogger;

import com.thoughtworks.paranamer.Paranamer;

import junit.framework.TestCase;
import net.sf.jstuff.core.collection.ObjectCache;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class TypesTest extends TestCase
{

	public void testFindLibrary()
	{
		File library;

		// locate JDK class
		library = Types.findLibrary(String.class);
		assertNotNull(library);
		assertFalse(library.isDirectory());
		assertTrue(library.isFile());
		assertTrue(library.exists());

		// locate class in 3rd party JAR
		library = Types.findLibrary(NOPLogger.class);
		assertNotNull(library);
		assertFalse(library.isDirectory());
		assertTrue(library.isFile());
		assertTrue(library.exists());

		// locate class in exploded directory (target/classes)
		library = Types.findLibrary(ObjectCache.class);
		assertNotNull(library);
		assertTrue(library.isDirectory());
		assertFalse(library.isFile());
		assertTrue(library.exists());

		// locate anonymous inner class in exploded directory (target/classes)
		final Runnable r = new Runnable()
			{
				public void run()
				{}
			};
		library = Types.findLibrary(r.getClass());
		assertNotNull(library);
		assertTrue(library.isDirectory());
		assertFalse(library.isFile());
		assertTrue(library.exists());
	}

	public void testGetVersion()
	{
		// from META-INF/MANIFEST.MF
		assertEquals("3.1", Types.getVersion(ObjectUtils.class));

		// from META-INF/maven/.../pom.properties
		assertEquals("2.8", Types.getVersion(Paranamer.class));

		// from jar name
		assertEquals("4.12", Types.getVersion(TestCase.class));
	}

	public void testIsAssignableTo()
	{
		assertTrue(Types.isAssignableTo(Integer.class, Number.class));
		assertFalse(Types.isAssignableTo(Number.class, Integer.class));
		assertTrue(Types.isAssignableTo(String.class, Object.class));
	}
}
