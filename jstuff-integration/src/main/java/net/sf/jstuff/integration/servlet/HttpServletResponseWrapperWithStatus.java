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
package net.sf.jstuff.integration.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class HttpServletResponseWrapperWithStatus extends HttpServletResponseWrapper
{
	private int httpStatus = SC_OK;

	public HttpServletResponseWrapperWithStatus(final HttpServletResponse response)
	{
		super(response);
	}

	public int getStatus()
	{
		return httpStatus;
	}

	@Override
	public void sendError(final int sc) throws IOException
	{
		httpStatus = sc;
		super.sendError(sc);
	}

	@Override
	public void sendError(final int sc, final String msg) throws IOException
	{
		httpStatus = sc;
		super.sendError(sc, msg);
	}

	@Override
	public void setStatus(final int sc)
	{
		httpStatus = sc;
		super.setStatus(sc);
	}

	@Override
	public void setStatus(final int sc, final String sm)
	{
		httpStatus = sc;
		super.setStatus(sc, sm);
	}
}