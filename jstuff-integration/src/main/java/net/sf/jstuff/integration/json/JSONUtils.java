/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.integration.json;

import java.lang.reflect.Array;

import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class JSONUtils
{
	/**
	 * Returns an array containing Java objects converted from the given jsonArray.
	 * 
	 * @param jsonArray the JSON array to convert
	 * @param targetTypes the types of each array element
	 * @return an array containing Java objects converted from the given jsonArray.
	 */
	public static Object[] toArray(final JSONArray jsonArray, final Class< ? >... targetTypes)
	{
		final Object[] array = (Object[]) Array.newInstance(Object.class, JSONArray.getDimensions(jsonArray)[0]);
		for (int i = 0, l = jsonArray.size(); i < l; i++)
		{
			final Object value = jsonArray.get(i);

			// classify object
			if (net.sf.json.util.JSONUtils.isNull(value))
				array[i] = null;

			else if (value instanceof JSONArray)
				array[i] = JSONArray.toArray((JSONArray) value, targetTypes[i].getComponentType());

			else if (value instanceof String || value instanceof Boolean || value instanceof Number
					|| value instanceof Character || value instanceof JSONFunction)
				array[i] = value;

			else
				array[i] = JSONObject.toBean((JSONObject) value, targetTypes[i]);
		}
		return array;
	}

	protected JSONUtils()
	{
		super();
	}
}
