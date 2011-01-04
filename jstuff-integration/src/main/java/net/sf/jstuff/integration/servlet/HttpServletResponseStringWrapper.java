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
package net.sf.jstuff.integration.servlet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HttpServletResponseStringWrapper extends HttpServletResponseWrapper
{
	private final static class FilterServletOutputStream extends ServletOutputStream
	{
		private final DataOutputStream stream;

		public FilterServletOutputStream(final OutputStream output)
		{
			stream = new DataOutputStream(output);
		}

		@Override
		public void write(final byte[] b) throws IOException
		{
			stream.write(b);
		}

		@Override
		public void write(final byte[] b, final int off, final int len) throws IOException
		{
			stream.write(b, off, len);
		}

		@Override
		public void write(final int b) throws IOException
		{
			stream.write(b);
		}
	}

	private FilterServletOutputStream exposedOutputStream;
	private PrintWriter exposedPrintWriter;
	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

	public HttpServletResponseStringWrapper(final HttpServletResponse response)
	{
		super(response);
	}

	public byte[] getData()
	{
		return outputStream.toByteArray();
	}

	@Override
	public synchronized ServletOutputStream getOutputStream()
	{
		if (exposedOutputStream == null) exposedOutputStream = new FilterServletOutputStream(outputStream);
		return exposedOutputStream;
	}

	@Override
	public synchronized PrintWriter getWriter()
	{
		if (exposedPrintWriter == null) exposedPrintWriter = new PrintWriter(getOutputStream(), true);
		return exposedPrintWriter;
	}

	@Override
	public String toString()
	{
		if (exposedPrintWriter != null) exposedPrintWriter.flush();
		return outputStream.toString();
	}
}