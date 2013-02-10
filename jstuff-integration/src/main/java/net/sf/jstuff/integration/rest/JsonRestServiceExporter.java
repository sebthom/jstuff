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
package net.sf.jstuff.integration.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.sf.jstuff.core.io.IOUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JsonRestServiceExporter extends AbstractRestServiceExporter
{
	public JsonRestServiceExporter()
	{
		super("UTF-8", "application/json;charset=UTF-8");
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request) throws IOException
	{
		return (T) JSONObject.toBean(JSONObject.fromObject(IOUtils.toString(request.getReader())), targetType);
	}

	@Override
	protected String serializeResponse(final Object resultObject)
	{
		if (resultObject == null) return "null";

		final JSONObject response = new JSONObject();
		response.put(resultObject.getClass().getSimpleName(), resultObject);

		return response.toString();
	}
}
