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
package net.sf.jstuff.integration.rest;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.reflection.BeanUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractRestServiceExporter extends RemoteExporter
		implements
			HttpRequestHandler,
			InitializingBean
{
	private final static Logger LOG = Logger.getLogger(AbstractRestServiceExporter.class.getName());

	private final static String[] PARAM_NAMES = {"param0", "param1", "param2", "param3", "param4", "param5", "param6",
			"param7", "param8", "param9"};

	private String characterEncoding = "UTF-8";

	private String contentType = "";

	/**
	 * parameter names of exported methods by name of the exported method
	 */
	protected Map<String, String[]> paramNamesByExportedMethodName = new TreeMap<String, String[]>();

	private final Map<String, RestResourceAction> resourceActions = new TreeMap<String, RestResourceAction>();

	protected String serviceName;

	protected AbstractRestServiceExporter(final String characterEncoding, final String contentType)
	{
		this.characterEncoding = characterEncoding;
		this.contentType = contentType;
	}

	private RestServiceDescriptor _buildRESTServiceDescriptor(final HttpServletRequest request)
	{
		final TreeMap<String, RestResourceActionDescriptor> result = new TreeMap<String, RestResourceActionDescriptor>();
		for (final RestResourceAction action : resourceActions.values())
		{
			final boolean isPOST = action.getHttpMethod() == HttpRequestMethod.POST;
			final boolean isPUT = action.getHttpMethod() == HttpRequestMethod.PUT;
			final Method mappedMethod = action.getMethod();
			final int paramCount = mappedMethod.getParameterTypes().length;

			final RestResourceActionDescriptor actionDef = new RestResourceActionDescriptor(action.isFallback());
			if (isPUT || isPOST)
			{
				if (paramCount < 2)
					actionDef.setRequestURL(request.getRequestURL() + "/" + action.getResource());
				else
					actionDef.setRequestURL(request.getRequestURL()
							+ "/"
							+ action.getResource()
							+ "/${"
							+ StringUtils.join(ArrayUtils.remove(getParameterNames(mappedMethod), paramCount - 1),
									"}/${") + "}");
				actionDef.setRequestBodyType(mappedMethod.getParameterTypes()[paramCount - 1].getSimpleName());
			}
			else
			{
				if (paramCount < 1)
					actionDef.setRequestURL(request.getRequestURL() + "/" + action.getResource());
				else
					actionDef.setRequestURL(request.getRequestURL() + "/" + action.getResource() + "/${"
							+ StringUtils.join(getParameterNames(mappedMethod), "}/${") + "}");
				actionDef.setRequestBodyType("ignored");
			}
			actionDef.setHttpRequestMethod(action.getHttpMethod().toString());
			actionDef.setResponseBodyType(mappedMethod.getReturnType().getSimpleName());
			if (mappedMethod.getParameterTypes().length == 0)
				actionDef.setMappedServiceMethod(mappedMethod.getDeclaringClass().getSimpleName() + "."
						+ mappedMethod.getName() + "()");
			else
				actionDef.setMappedServiceMethod(mappedMethod.getDeclaringClass().getSimpleName() + "."
						+ mappedMethod.getName() + "(" + StringUtils.join(getParameterNames(mappedMethod), ",") + ")");
			result.put(actionDef.getRequestURL(), actionDef);
		}
		final RestServiceDescriptor serviceDescr = new RestServiceDescriptor();
		serviceDescr.setMethods(new ArrayList<RestResourceActionDescriptor>(result.values()));
		return serviceDescr;
	}

	private void _describe(final HttpServletRequest request, final HttpServletResponse response) throws IOException
	{
		response.getWriter().println(serializeResponse(_buildRESTServiceDescriptor(request)));
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void _describeAsHTML(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		response.setContentType("text/html;charset=" + characterEncoding);
		final PrintWriter pw = response.getWriter();
		pw.println("<html><head><style>* {font-family:Tahoma} table * {font-size:8pt}</style><title>");
		pw.println(getServiceInterface().getSimpleName());
		pw.println(" Service Definition</title></head><body>");
		pw.println("<h1>");
		pw.println(getServiceInterface().getSimpleName());
		pw.println("</h1>");
		pw.println("<p>Supported Content Type: ");
		pw.println(contentType);
		pw.println("</p>");

		pw.println("<h3>RESTful resource actions</h3>");
		final RestServiceDescriptor serviceDef = _buildRESTServiceDescriptor(request);
		final String requestURL = request.getRequestURL().toString();
		doExplainAsHTML(pw, false, serviceDef, requestURL);

		pw.println("<h3>Fallback resource actions for HTTP clients without support for PUT, DELETE, HEAD</h3>");
		doExplainAsHTML(pw, true, serviceDef, requestURL);

		pw.println("</body></html>");
		response.setStatus(HttpServletResponse.SC_OK);
	}

	public synchronized void afterPropertiesSet() throws Exception
	{
		// initialize resourceActions map
		for (final Method m : getServiceInterface().getMethods())
		{
			if (m.isAnnotationPresent(REST_GET.class))
			{
				final REST_GET annotation = m.getAnnotation(REST_GET.class);

				final String key = "GET_" + annotation.value();

				if (resourceActions.containsKey(key))
					throw new IllegalStateException(
							"Mapping multiple methods to the same resource+method is not supported: " + key);

				final RestResourceAction action = new RestResourceAction(m, annotation.value(), HttpRequestMethod.GET);
				resourceActions.put(key, action);
			}
			if (m.isAnnotationPresent(REST_POST.class))
			{
				final REST_POST annotation = m.getAnnotation(REST_POST.class);

				final String key = "POST_" + annotation.value();

				if (resourceActions.containsKey(key))
					throw new IllegalStateException(
							"Mapping multiple methods to the same resource+method is not supported: " + key);

				final RestResourceAction action = new RestResourceAction(m, annotation.value(), HttpRequestMethod.POST);
				resourceActions.put(key, action);
			}
			if (m.isAnnotationPresent(REST_PUT.class))
			{
				final REST_PUT annotation = m.getAnnotation(REST_PUT.class);

				final String key = "PUT" + annotation.value();

				if (resourceActions.containsKey(key))
					throw new IllegalStateException(
							"Mapping multiple methods to the same resource+method is not supported: " + key);

				final RestResourceAction action = new RestResourceAction(m, annotation.value(), HttpRequestMethod.PUT);
				resourceActions.put(key, action);

				if (annotation.fallback().length() > 0)
				{
					final String fallbackKey = "POST_" + annotation.fallback();

					if (resourceActions.containsKey(fallbackKey))
						throw new IllegalStateException(
								"Mapping multiple methods to the same resource+method is not supported: " + fallbackKey
										+ " at " + m);

					resourceActions.put(fallbackKey, new RestResourceAction(m, annotation.fallback(),
							HttpRequestMethod.POST, action));
				}
			}
			if (m.isAnnotationPresent(REST_DELETE.class))
			{
				final REST_DELETE annotation = m.getAnnotation(REST_DELETE.class);

				final String key = "DELETE" + annotation.value();

				if (resourceActions.containsKey(key))
					throw new IllegalStateException(
							"Mapping multiple methods to the same resource+method is not supported: " + key);

				final RestResourceAction action = new RestResourceAction(m, annotation.value(),
						HttpRequestMethod.DELETE);
				resourceActions.put(key, action);

				if (annotation.fallback().length() > 0)
				{
					final String fallbackKey = "POST_" + annotation.fallback();

					if (resourceActions.containsKey(fallbackKey))
						throw new IllegalStateException(
								"Mapping multiple methods to the same resource+method is not supported: " + fallbackKey
										+ " at " + m);

					resourceActions.put(fallbackKey, new RestResourceAction(m, annotation.fallback(),
							HttpRequestMethod.POST, action));
				}
			}
			if (m.isAnnotationPresent(REST_HEAD.class))
			{
				final REST_HEAD annotation = m.getAnnotation(REST_HEAD.class);

				final String key = "HEAD" + annotation.value();

				if (resourceActions.containsKey(key))
					throw new IllegalStateException(
							"Mapping multiple methods to the same resource+method is not supported: " + key);

				final RestResourceAction action = new RestResourceAction(m, annotation.value(), HttpRequestMethod.HEAD);
				resourceActions.put(key, action);

				if (annotation.fallback().length() > 0)
				{
					final String fallbackKey = "GET_" + annotation.fallback();

					if (resourceActions.containsKey(fallbackKey))
						throw new IllegalStateException(
								"Mapping multiple methods to the same resource+method is not supported: " + fallbackKey
										+ " at " + m);

					resourceActions.put(fallbackKey, new RestResourceAction(m, annotation.fallback(),
							HttpRequestMethod.GET, action));
				}
			}
		}
	}

	protected abstract <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request)
			throws IOException;

	public void doExplainAsHTML(final PrintWriter pw, final boolean fallbackMethods,
			final RestServiceDescriptor serviceDef, final String requestURL)
	{
		for (final RestResourceActionDescriptor actionDef : serviceDef.getMethods())
		{
			if (actionDef.getIsFallbackMethod() != fallbackMethods) continue;

			pw.println("<p style='font-size:8pt'>");
			if (actionDef.getIsFallbackMethod()) pw.println("<span style='color:red'>*isFallback*</span> ");
			pw.println("<b>" + actionDef.getHttpRequestMethod() + "</b>");
			pw.println(StringUtils.replace(StringUtils.replace(
					StringUtils.replace(actionDef.getRequestURL(), "${", "<span style='color:blue'>${"), "}",
					"}</span>"), requestURL, requestURL + "<b>")
					+ "</b>");
			pw.println("</p><table style='border: 1px solid black;width:90%;margin-bottom:0.5em'>");

			/*
			 * request body type
			 */
			if (actionDef.getRequestBodyType().equals("ignored"))
				pw.println("<tr><td width='200px'>Request Body Type</td><td><i>ignored</i></td></tr>");
			else
			{
				pw.println("<tr><td width='200px'>Request Body Type</td><td style='font-weight:bold;color:darkred'>");
				pw.println(actionDef.getRequestBodyType());
				pw.println("</td></tr>");
			}

			/*
			 * response body type
			 */
			pw.println("<tr><td width='200px'>Response Body Type</td><td style='font-weight:bold;color:darkgreen'>");
			pw.println(actionDef.getResponseBodyType());
			pw.println("</td></tr>");

			/*
			 * mapped service method
			 */
			pw.println("<tr><td width='200px'>Mapped Service Method</td><td style='font-weight:bold;color:gray'>");

			// extract the parameter names from the request url
			final String[] requestParams = StringUtils.substringsBetween(actionDef.getRequestURL(), "${", "}");
			final String methodSignature = actionDef.getMappedServiceMethod();

			pw.println("<span style='font-weight:bold;color:darkgreen'>" + actionDef.getResponseBodyType() + "</span>");

			if (actionDef.getRequestBodyType().equals("ignored"))
				pw.println(StringUtils.replace(
						methodSignature,
						StringUtils.join(requestParams, ","),
						"<span style='color:blue'>"
								+ StringUtils.join(requestParams, "</span>,<span style='color:blue'>") + "</span>"));
			else
			{
				final String param = StringUtils.substringAfterLast(
						"," + StringUtils.substringBetween(actionDef.getMappedServiceMethod(), "(", ")"), ",");
				pw.println(StringUtils.replace(StringUtils.replace(
						methodSignature,
						StringUtils.join(requestParams, ","),
						"<span style='color:blue'>"
								+ StringUtils.join(requestParams, "</span>,<span style='color:blue'>") + "</span>"),
						param + ")", "<span style='color:darkred'>" + param + "</span>)"));
			}
			pw.println("</td></tr>");
			pw.println("</table>");
		}
	}

	protected String[] getParameterNames(final Method method)
	{
		if (method.getParameterTypes().length == 0) return null;

		final String[] names = new String[method.getParameterTypes().length];
		for (int i = 0, l = method.getParameterTypes().length; i < l; i++)
			names[i] = "param" + i;
		return names;
	}

	public void handleRequest(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException
	{
		final HttpRequestMethod httpRequestMethod = HttpRequestMethod.valueOf(request.getMethod().toUpperCase());
		final boolean isDELETE = httpRequestMethod == HttpRequestMethod.DELETE;
		final boolean isGET = httpRequestMethod == HttpRequestMethod.GET;
		final boolean isPOST = httpRequestMethod == HttpRequestMethod.POST;
		final boolean isHEAD = httpRequestMethod == HttpRequestMethod.HEAD;
		final boolean isPUT = httpRequestMethod == HttpRequestMethod.PUT;

		request.setCharacterEncoding(characterEncoding);
		response.setCharacterEncoding(characterEncoding);
		response.setContentType(contentType);

		if (isGET && request.getParameter("explain") != null)
		{
			_describe(request, response);
			return;
		}

		if (isGET && request.getParameter("explainAsHTML") != null)
		{
			_describeAsHTML(request, response);
			return;
		}

		final String resource = request.getParameter("resource");
		final String key = resource + "_" + httpRequestMethod;
		final RestResourceAction action = resourceActions.get(key);

		if (action == null)
		{
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			response.getWriter().println(
					serializeResponse(new RestServiceError("UnsupportedOperationException",
							"Unsupported HTTP request method " + httpRequestMethod + " for resource " + resource)));
			return;
		}

		if (LOG.isLoggable(Level.FINE)) LOG.fine("Invoking " + action);

		final Method actionMethod = action.getMethod();

		// retrieving URL parameters as strings
		final Class< ? >[] paramTypes = actionMethod.getParameterTypes();
		final String[] stringArguments = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++)
		{
			stringArguments[i] = request.getParameter(PARAM_NAMES[i]);
			if (stringArguments[i] == null)
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println(
						serializeResponse(new RestServiceError("MissingArgumentException", "Missing parameter "
								+ getParameterNames(actionMethod)[i])));
				return;
			}
		}

		// converting URL parameters to the required object values
		Object[] methodArguments = BeanUtils.valuesOf(stringArguments, actionMethod.getParameterTypes());

		// for POST/PUT requests add the request body as additional argument for the method arguments 
		if (isPOST || isPUT)
			methodArguments = ArrayUtils.add(methodArguments,
					deserializeRequestBody(actionMethod.getReturnType(), request));

		try
		{
			final Object methodReturnValue = actionMethod.invoke(getService(), methodArguments);
			onServiceMethodInvoked(actionMethod, methodArguments, methodReturnValue);

			if (isGET || isHEAD)
				response.setStatus(HttpServletResponse.SC_OK);
			else if (isPOST)
				response.setStatus(HttpServletResponse.SC_CREATED);
			else if (isPUT || isDELETE) response.setStatus(HttpServletResponse.SC_ACCEPTED);

			response.getWriter().println(serializeResponse(methodReturnValue));
		}
		catch (final Exception ex)
		{
			LOG.log(Level.SEVERE, "Invoking method " + actionMethod + " failed.", ex);

			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println(
					serializeResponse(new RestServiceError(ex.getClass().getSimpleName(), ex.getMessage())));
			return;
		}
	}

	protected void onServiceMethodInvoked(final Method m, final Object[] args, final Object rc)
	{
		// may be subclassed
	}

	protected abstract String serializeResponse(Object resultObject);

	@SuppressWarnings("rawtypes")
	@Override
	public void setServiceInterface(final Class serviceInterface)
	{
		super.setServiceInterface(serviceInterface);
		serviceName = serviceInterface.getSimpleName();
	}

}
