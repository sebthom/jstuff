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
package net.sf.jstuff.integration.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.sf.jstuff.core.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JsonRestServiceExporter extends AbstractRestServiceExporter
{
	private static final Logger LOG = Logger.create();

	private static final ObjectMapper JSON = new ObjectMapper();

	public JsonRestServiceExporter()
	{
		super("UTF-8", "application/json;charset=UTF-8");
		LOG.infoNew(this);
	}

	@Override
	protected <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request) throws IOException
	{
		return JSON.readValue(request.getReader(), targetType);
	}

	@Override
	protected String serializeResponse(final Object resultObject) throws JsonProcessingException
	{
		return JSON.writeValueAsString(resultObject);
	}
}
