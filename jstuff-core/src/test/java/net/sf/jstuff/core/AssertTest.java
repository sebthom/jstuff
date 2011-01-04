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
package net.sf.jstuff.core;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AssertTest extends TestCase
{
	public void testArgumentNotEmpty()
	{
		try
		{
			Assert.argumentNotEmpty("password", (String) null);
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[password] must not be null", ex.getMessage());
		}

		try
		{
			Assert.argumentNotEmpty("password", "");
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[password] must not be empty", ex.getMessage());
		}

		Assert.argumentNotEmpty("password", "secret");

		try
		{
			Assert.argumentNotEmpty("values", (String[]) null);
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[values] must not be null", ex.getMessage());
		}

		try
		{
			Assert.argumentNotEmpty("values", new String[0]);
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[values] must not be empty", ex.getMessage());
		}

		Assert.argumentNotEmpty("values", new String[]{"dfd"});

	}

	public void testArgumentNotNull()
	{
		try
		{
			Assert.argumentNotNull("password", null);
			fail();
		}
		catch (final IllegalArgumentException ex)
		{
			assertEquals("[password] must not be null", ex.getMessage());
		}

		Assert.argumentNotNull("password", "");
		Assert.argumentNotNull("password", "secret");
	}

	public void testIsFalse()
	{
		try
		{
			Assert.isFalse(true, "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		Assert.isFalse(false, "foo");
	}

	public void testIsReadableFile() throws IOException
	{
		try
		{
			Assert.isFileReadable(new File("foo"));
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertTrue(ex.getMessage().contains("does not exist"));
		}

		try
		{
			Assert.isFileReadable(File.createTempFile("foo", "bar").getParentFile());
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertTrue(ex.getMessage().contains("is not a file"));
		}
	}

	public void testIsTrue()
	{
		try
		{
			Assert.isTrue(false, "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		Assert.isTrue(true, "foo");
	}

	public void testNotEmpty()
	{
		try
		{
			Assert.notEmpty("", "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		try
		{
			Assert.notEmpty((String) null, "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		Assert.notEmpty("value", "foo");

		try
		{
			Assert.notEmpty(new String[0], "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		try
		{
			Assert.notEmpty((String[]) null, "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		Assert.notEmpty(new String[]{"value"}, "foo");
	}

	public void testNotFalse()
	{
		try
		{
			Assert.notFalse(false, "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		Assert.notFalse(true, "foo");
	}

	public void testNotNull()
	{
		try
		{
			Assert.notNull(null, "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		Assert.notNull("value", "foo");
	}

	public void testNotTrue()
	{
		try
		{
			Assert.notTrue(true, "foo");
			fail();
		}
		catch (final IllegalStateException ex)
		{
			assertEquals("foo", ex.getMessage());
		}

		Assert.notTrue(false, "foo");
	}
}
