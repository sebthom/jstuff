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
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.reflection.Beans;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractRestServiceExporter extends RemoteExporter implements HttpRequestHandler, InitializingBean
{
	private static final Logger LOG = Logger.create();

	private final String characterEncoding;
	private final String contentType;
	private final RestResourceActionRegistry actionRegistry = new RestResourceActionRegistry();

	protected AbstractRestServiceExporter(final String characterEncoding, final String contentType)
	{
		this.characterEncoding = characterEncoding;
		this.contentType = contentType;
	}

	public String getContentType()
	{
		return contentType;
	}

	public String getCharacterEncoding()
	{
		return characterEncoding;
	}

	public synchronized void afterPropertiesSet() throws IllegalStateException
	{
		// initialize resourceActions map
		for (final Method m : getServiceInterface().getMethods())
		{
			if (m.isAnnotationPresent(REST_GET.class))
				actionRegistry.registerResourceAction(m.getAnnotation(REST_GET.class).value(), HttpRequestMethod.GET, m, getService());

			if (m.isAnnotationPresent(REST_POST.class))
				actionRegistry.registerResourceAction(m.getAnnotation(REST_POST.class).value(), HttpRequestMethod.POST, m, getService());

			if (m.isAnnotationPresent(REST_PUT.class))
			{
				final REST_PUT annotation = m.getAnnotation(REST_PUT.class);
				final RestResourceAction action = actionRegistry.registerResourceAction(annotation.value(), HttpRequestMethod.PUT, m,
						getService());
				if (annotation.fallback().length() > 0)
					actionRegistry.registerFallbackResourceAction(annotation.fallback(), m, getService(), action);
			}
			if (m.isAnnotationPresent(REST_DELETE.class))
			{
				final REST_DELETE annotation = m.getAnnotation(REST_DELETE.class);
				final RestResourceAction action = actionRegistry.registerResourceAction(annotation.value(), HttpRequestMethod.DELETE, m,
						getService());
				if (annotation.fallback().length() > 0)
					actionRegistry.registerFallbackResourceAction(annotation.fallback(), m, getService(), action);
			}
			if (m.isAnnotationPresent(REST_HEAD.class))
			{
				final REST_HEAD annotation = m.getAnnotation(REST_HEAD.class);
				final RestResourceAction action = actionRegistry.registerResourceAction(annotation.value(), HttpRequestMethod.HEAD, m,
						getService());
				if (annotation.fallback().length() > 0)
					actionRegistry.registerFallbackResourceAction(annotation.fallback(), m, getService(), action);
			}
		}
	}

	private RestServiceDescriptor buildRESTServiceDescriptor(final HttpServletRequest req)
	{
		final List<RestResourceAction> actions = actionRegistry.getAllResourceActions();
		final RestServiceDescriptor serviceDescr = new RestServiceDescriptor();
		Collections.sort(actions, new Comparator<RestResourceAction>()
			{
				public int compare(final RestResourceAction o1, final RestResourceAction o2)
				{
					return ObjectUtils.compare(o1.getRequestURITemplate(), o2.getRequestURITemplate());
				}
			});
		serviceDescr.setActions(actions);
		return serviceDescr;
	}

	private void describe(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
	{
		resp.getWriter().println(serializeResponse(buildRESTServiceDescriptor(req)));
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	private void describeAsHTML(final HttpServletRequest req, final HttpServletResponse resp) throws IOException
	{
		resp.setContentType("text/html;charset=" + characterEncoding);
		@SuppressWarnings("resource")
		final PrintWriter pw = resp.getWriter();
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
		final RestServiceDescriptor serviceDef = buildRESTServiceDescriptor(req);
		final String requestURL = req.getRequestURL().toString();
		doExplainAsHTML(pw, false, serviceDef, requestURL);

		pw.println("<h3>Fallback resource actions for HTTP clients without support for PUT, DELETE, HEAD</h3>");
		doExplainAsHTML(pw, true, serviceDef, requestURL);

		pw.println("</body></html>");
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	protected abstract <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request) throws IOException;

	public void doExplainAsHTML(final PrintWriter pw, final boolean fallbackMethods, final RestServiceDescriptor serviceDef,
			final String baseRequestURL)
	{
		for (final RestResourceAction action : serviceDef.getActions())
		{
			final String requestURL = baseRequestURL + "/" + action.getRequestURITemplate();
			if (action.isFallBackMethod() != fallbackMethods) continue;

			pw.println("<p style='font-size:8pt'>");
			if (action.isFallBackMethod()) pw.println("<span style='color:red'>*isFallback*</span> ");
			pw.println("<b>" + action.getHttpRequestMethod() + "</b>");
			pw.println(StringUtils.replace(
					StringUtils.replace(StringUtils.replace(requestURL, "${", "<span style='color:blue'>${"), "}", "}</span>"),
					baseRequestURL, baseRequestURL + "<b>") + "</b>");
			pw.println("</p><table style='border: 1px solid black;width:90%;margin-bottom:0.5em'>");

			/*
			 * request body type
			 */
			if (action.getHttpRequestBodyType() == null)
				pw.println("<tr><td width='200px'>HTTP Request Body Type</td><td><i>ignored</i></td></tr>");
			else
			{
				pw.println("<tr><td width='200px'>HTTP Request Body Type</td><td style='font-weight:bold;color:darkred'>");
				pw.println(action.getHttpRequestBodyType().getSimpleName());
				pw.println("</td></tr>");
			}

			/*
			 * response body type
			 */
			pw.println("<tr><td width='200px'>HTTP Response Body Type</td><td style='font-weight:bold;color:darkgreen'>");
			pw.println(action.getHttpResponseBodyType().getSimpleName());
			pw.println("</td></tr>");

			/*
			 * mapped service method
			 */
			pw.println("<tr><td width='200px'>Mapped Service Method</td><td style='font-weight:bold;color:gray'>");

			// extract the parameter names from the request url
			final String[] requestParams = StringUtils.substringsBetween(requestURL, "${", "}");

			pw.println("<span style='font-weight:bold;color:darkgreen'>" + action.getHttpResponseBodyType().getSimpleName() + "</span>");

			if (action.getHttpRequestBodyType() == null)
				pw.println(StringUtils.replace(action.getServiceMethodSignature(), StringUtils.join(requestParams, ","),
						"<span style='color:blue'>" + StringUtils.join(requestParams, "</span>,<span style='color:blue'>") + "</span>"));
			else
			{
				final String param = StringUtils.substringAfterLast(
						"," + StringUtils.substringBetween(action.getServiceMethodSignature(), "(", ")"), ",");
				pw.println(StringUtils.replace(StringUtils.replace(action.getServiceMethodSignature(),
						StringUtils.join(requestParams, ","),
						"<span style='color:blue'>" + StringUtils.join(requestParams, "</span>,<span style='color:blue'>") + "</span>"),
						param + ")", "<span style='color:darkred'>" + param + "</span>)"));
			}
			pw.println("</td></tr>");
			pw.println("</table>");
		}
	}

	public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
	{
		final HttpRequestMethod reqMethod = HttpRequestMethod.valueOf(req.getMethod().toUpperCase());
		final boolean isDELETE = reqMethod == HttpRequestMethod.DELETE;
		final boolean isGET = reqMethod == HttpRequestMethod.GET;
		final boolean isPOST = reqMethod == HttpRequestMethod.POST;
		final boolean isHEAD = reqMethod == HttpRequestMethod.HEAD;
		final boolean isPUT = reqMethod == HttpRequestMethod.PUT;

		req.setCharacterEncoding(characterEncoding);
		resp.setCharacterEncoding(characterEncoding);
		resp.setContentType(contentType);

		if (isGET && req.getParameter("explain") != null)
		{
			describe(req, resp);
			return;
		}

		if (isGET && req.getParameter("explainAsHTML") != null)
		{
			describeAsHTML(req, resp);
			return;
		}

		final String beanName = req.getAttribute("beanName").toString();
		final String requestParameters = StringUtils.substringAfter(req.getPathInfo(), beanName + "/");

		final RestResourceAction action = actionRegistry.getResourceAction(reqMethod, requestParameters);

		if (action == null)
		{
			resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			resp.getWriter().println(
					serializeResponse(new RestServiceError("UnsupportedOperationException", "Unsupported HTTP request method " + reqMethod
							+ " for resource " + requestParameters)));
			return;
		}

		LOG.debug("Invoking %s", action);

		final String argsString = requestParameters.substring(action.getResourceName().length());
		String[] args = "".equals(argsString) ? ArrayUtils.EMPTY_STRING_ARRAY : StringUtils.split(argsString.substring(1), "/");
		if (args.length < action.getRequiredURLParameterCount())
		{
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().println(
					serializeResponse(new RestServiceError("MissingArgumentException", "Missing parameter "
							+ action.getParameterNames()[args.length])));
			return;
		}

		// if the service method has var args and the var args are not provided add a null element to the args list
		if (args.length < action.getServiceMethod().getParameterTypes().length && action.getServiceMethod().isVarArgs())
			args = ArrayUtils.add(args, null);

		// converting URL parameters to the required object values
		Object[] methodArguments = Beans.valuesOf(args, action.getServiceMethod().getParameterTypes());

		// for POST/PUT requests add the request body as additional argument for the method arguments
		if (isPOST || isPUT)
			methodArguments = ArrayUtils.add(methodArguments, deserializeRequestBody(action.getHttpRequestBodyType(), req));

		try
		{
			final Object methodReturnValue = action.getServiceMethod().invoke(getService(), methodArguments);
			onServiceMethodInvoked(action.getServiceMethod(), methodArguments, methodReturnValue);

			if (isGET || isHEAD)
				resp.setStatus(HttpServletResponse.SC_OK);
			else if (isPOST)
				resp.setStatus(HttpServletResponse.SC_CREATED);
			else if (isPUT || isDELETE) resp.setStatus(HttpServletResponse.SC_ACCEPTED);

			resp.getWriter().println(serializeResponse(methodReturnValue));
		}
		catch (final Exception ex)
		{
			LOG.error("Invoking method %s failed.", ex, action.getServiceMethod());

			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().println(serializeResponse(new RestServiceError(ex.getClass().getSimpleName(), ex.getMessage())));
			return;
		}
	}

	protected void onServiceMethodInvoked(final Method m, final Object[] args, final Object rc)
	{
		// may be subclassed
	}

	protected abstract String serializeResponse(final Object resultObject);
}
