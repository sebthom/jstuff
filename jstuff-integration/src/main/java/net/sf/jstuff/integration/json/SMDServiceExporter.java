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
package net.sf.jstuff.integration.json;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONFunction;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;
import net.sf.jstuff.core.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.ClassUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

/**
 * JSON-RPC Standard Method Definition
 * 
 * http://www.dojotoolkit.org/reference-guide/dojox/rpc/SMDLibrary.html
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SMDServiceExporter extends RemoteExporter implements HttpRequestHandler, InitializingBean
{
	private final static Logger LOG = Logger.make();

	/**
	 * The exported methods by name.
	 */
	private Map<String, Method> exportedMethodsByName;

	/**
	 * Simple Method Description
	 */
	private String smdTemplate;

	public void afterPropertiesSet() throws Exception
	{
		exportedMethodsByName = buildExportedMethodsByName();
		smdTemplate = buildSMDTemplate();
	}

	/**
	 * Map<String, Method>
	 * 
	 * @return a map
	 */
	private Map<String, Method> buildExportedMethodsByName()
	{
		final Map<String, Method> methodsByMethodName = newTreeMap();
		final Map<String, Class< ? >[]> parameterTypesByMethodName = newHashMap();

		// loop through the service interface and all super interfaces to collect the public methods
		Class< ? > clazz = getServiceInterface();
		while (clazz != null)
		{
			// retrieve the public methods 
			final Method[] publicMethods = getServiceInterface().getMethods();
			for (final Method method : publicMethods)
			{
				final String name = method.getName();
				if (methodsByMethodName.containsKey(name))
				{
					final Class< ? >[] currentMethodParamTypes = method.getParameterTypes();
					final Class< ? >[] registeredMethodParamTypes = parameterTypesByMethodName.get(name);

					if (!Arrays.equals(currentMethodParamTypes, registeredMethodParamTypes))
						throw new IllegalStateException("Method overloading is not supported");
				}
				else
				{
					methodsByMethodName.put(name, method);
					parameterTypesByMethodName.put(name, method.getParameterTypes());
				}
			}
			clazz = clazz.getSuperclass();
		}
		return methodsByMethodName;
	}

	/**
	 * see http://dojo.jot.com/SMD
	 * see http://manual.dojotoolkit.org/WikiHome/DojoDotBook/Book9
	 * @return
	 */
	private String buildSMDTemplate()
	{
		// build the method descriptors
		final JSONArray methodDescriptions = new JSONArray();
		for (final Object element : exportedMethodsByName.values())
		{
			final Method method = (Method) element;

			final JSONObject methodDescriptor = new JSONObject();
			methodDescriptor.put("name", method.getName());
			if (method.getParameterTypes().length > 0)
			{
				// for some reason parameter names are not preserved in interfaces,
				// therefore we look them up in the service implementing class instead
				final String[] names = getParameterNames(method);/* PARANAMER.lookupParameterNames(getService().getClass()
																	.getClassLoader(), getService().getClass().getName(), method.getName());*/

				final JSONArray parameterArray = new JSONArray();
				for (int j = 0; j < method.getParameterTypes().length; j++)
				{
					final Class< ? > clazz = method.getParameterTypes()[j];
					final JSONObject parameterDescriptor = new JSONObject();

					parameterDescriptor.put("name", names[0]);
					parameterDescriptor.put("type", clazz.getName());
					parameterArray.add(parameterDescriptor);
				}
				methodDescriptor.put("parameters", parameterArray);
			}
			methodDescriptions.add(methodDescriptor);
		}

		// build the final SMD definition object
		final JSONObject result = new JSONObject();
		result.put("SMDVersion", ".1");
		result.put("objectName", ClassUtils.getShortName(getServiceInterface()));
		result.put("serviceType", "JSON-RPC");
		result.put("serviceURL", "THE_SERVICE_URL");
		result.put("methods", methodDescriptions);

		LOG.traceMethodExit(result);
		return result.toString();
	}

	protected String[] getParameterNames(final Method method)
	{
		final String[] names = new String[method.getParameterTypes().length];
		for (int i = 0, l = method.getParameterTypes().length; i < l; i++)
			names[i] = "param" + i;
		return names;
	}

	/**
	 * Processes the incoming JSON request and creates a JSON response..
	 */
	public void handleRequest(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException
	{
		// on POST requests we invoke a method and return the method value as a JSON string
		if ("POST".equals(request.getMethod()))
			invokeMethod(request, response);

		else if ("GET".equals(request.getMethod()))
		{
			// replace THE_SERVICE_URL place holder with the actual URL from the current request
			final String smd = smdTemplate.replaceFirst("THE_SERVICE_URL", request.getRequestURL().toString());

			response.getWriter().write(smd);
		}
	}

	private void invokeMethod(final HttpServletRequest request, final HttpServletResponse response) throws IOException,
			ServletException
	{
		// read the request body
		final BufferedReader requestReader = request.getReader();
		final StringBuffer requestBody = new StringBuffer();
		final char[] buffer = new char[1024];
		while (true)
		{
			final int bytesRead = requestReader.read(buffer);
			if (bytesRead == -1) break;
			requestBody.append(buffer, 0, bytesRead);
		}

		// deserializing the object
		final JSONObject requestObject = JSONObject.fromObject(new String(buffer));

		// looking up the method
		final String methodName = requestObject.getString("method");
		final Method method = exportedMethodsByName.get(methodName);

		// throw an exception if the method could not be found
		if (method == null) throw new ServletException("Method " + methodName + " not found");

		final Object[] methodArguments = toArray(requestObject.getJSONArray("params"), method.getParameterTypes());

		try
		{
			// invoking the method
			final Object methodReturnValue = method.invoke(getProxyForService(), methodArguments);

			// creating a JSON object containing the method return value
			final JSONObject jsonObj = new JSONObject();
			jsonObj.put("id", requestObject.getString("id"));
			jsonObj.put("result", methodReturnValue);

			jsonObj.write(response.getWriter());

			LOG.traceMethodExit(jsonObj);
		}
		catch (final Exception ex)
		{
			throw new NestedServletException("Invoking method " + methodName + " failed", ex);
		}
	}

	/**
	 * Returns an array containing Java objects converted from the given jsonArray.
	 * 
	 * @param jsonArray the JSON array to convert
	 * @param elementTypes the types of each array element
	 * @return an array containing Java objects converted from the given jsonArray.
	 */
	private Object[] toArray(final JSONArray jsonArray, final Class< ? >[] elementTypes)
	{
		final Object[] array = (Object[]) Array.newInstance(Object.class, JSONArray.getDimensions(jsonArray));
		final int arrayLen = jsonArray.size();

		for (int i = 0; i < arrayLen; i++)
		{
			final Object value = jsonArray.get(i);
			if (JSONUtils.isNull(value))
				array[i] = null;
			else if (value instanceof JSONArray)
				array[i] = JSONArray.toArray((JSONArray) value, elementTypes[i]);
			else if (value instanceof String || value instanceof Boolean || value instanceof Number
					|| value instanceof Character || value instanceof JSONFunction)
				array[i] = value;
			else
				array[i] = JSONObject.toBean((JSONObject) value, elementTypes[i]);
		}
		return array;
	}
}
