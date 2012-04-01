/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class IOUtilsTest extends TestCase
{
	public void testReadBytes() throws IOException
	{
		final ByteArrayInputStream is = new ByteArrayInputStream("Hello World!".getBytes());
		assertEquals(5, IOUtils.readBytes(is, 5).length);

		is.reset();
		assertEquals("Hello World!", new String(IOUtils.readBytes(is, 12)));
	}

}
