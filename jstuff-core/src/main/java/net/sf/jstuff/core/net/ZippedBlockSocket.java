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
package net.sf.jstuff.core.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.Deflater;

import net.sf.jstuff.core.io.ZippedBlockInputStream;
import net.sf.jstuff.core.io.ZippedBlockOutputStream;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ZippedBlockSocket extends Socket
{
	private ZippedBlockInputStream in;
	private ZippedBlockOutputStream out;

	public ZippedBlockSocket()
	{
		super();
	}

	public ZippedBlockSocket(final String host, final int port) throws IOException
	{
		super(host, port);
	}

	@Override
	public synchronized void close() throws IOException
	{
		getOutputStream().flush();
		super.close();
	}

	@Override
	public InputStream getInputStream() throws IOException
	{
		if (in == null) in = new ZippedBlockInputStream(super.getInputStream());
		return in;
	}

	@Override
	public OutputStream getOutputStream() throws IOException
	{
		if (out == null)
		{
			out = new ZippedBlockOutputStream(super.getOutputStream(), 1024);
			out.getCompressor().setStrategy(Deflater.DEFAULT_STRATEGY);
			out.getCompressor().setLevel(Deflater.BEST_COMPRESSION);
		}
		return out;
	}
}