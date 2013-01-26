/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockStreamTest extends TestCase
{
	@SuppressWarnings("resource")
	public void testZippedBlockStream() throws IOException
	{
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ZippedBlockOutputStream zos = new ZippedBlockOutputStream(bos, 16, Deflater.BEST_SPEED);
		zos.write("Hello World! Hello World!".getBytes());
		zos.flush();

		final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		final ZippedBlockInputStream zis = new ZippedBlockInputStream(bis);
		assertEquals(12, IOUtils.readBytes(zis, 12).length);

		assertEquals(" Hello World!", new String(IOUtils.readBytes(zis, 13)));
	}
}