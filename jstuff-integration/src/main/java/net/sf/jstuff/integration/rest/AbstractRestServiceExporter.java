/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
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

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.ArrayUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Beans;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractRestServiceExporter extends RemoteExporter implements HttpRequestHandler {
    private static final Logger LOG = Logger.create();

    protected final String characterEncoding;
    protected final String contentType;

    protected RestResourceActionRegistry serviceActions;
    protected RestServiceDescriptor serviceDescriptor;

    protected AbstractRestServiceExporter(final String characterEncoding, final String contentType) {
        LOG.infoNew(this);

        this.characterEncoding = characterEncoding;
        this.contentType = contentType;
    }

    protected abstract <T> T deserializeRequestBody(final Class<T> targetType, final HttpServletRequest request) throws IOException;

    protected void explainActionsAsHTML(final PrintWriter pw, final boolean fallbackMethods, final String baseRequestURL) {
        for (final RestResourceAction action : serviceDescriptor.getActions()) {
            final String requestURL = baseRequestURL + "/" + action.getRequestURITemplate();
            if (action.isFallBackMethod() != fallbackMethods) {
                continue;
            }

            pw.println("<p style='font-size:8pt'>");
            if (action.isFallBackMethod()) {
                pw.println("<span style='color:red'>*isFallback*</span> ");
            }
            pw.println("<b>" + action.getHttpRequestMethod() + "</b>");
            pw.println(Strings.replace(Strings.replace(Strings.replace(requestURL, "${", "<span style='color:blue'>${"), "}", "}</span>"), baseRequestURL,
                baseRequestURL + "<b>") + "</b>");
            pw.println("</p><table style='border: 1px solid black;width:90%;margin-bottom:0.5em'>");

            /*
             * request body type
             */
            if (action.getHttpRequestBodyType() == null) {
                pw.println("<tr><td width='200px'>HTTP Request Body Type</td><td><i>ignored</i></td></tr>");
            } else {
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
            final String[] requestParams = Strings.substringsBetween(requestURL, "${", "}");

            pw.println("<span style='font-weight:bold;color:darkgreen'>" + action.getHttpResponseBodyType().getSimpleName() + "</span>");

            if (action.getHttpRequestBodyType() == null) {
                pw.println(Strings.replace(action.getServiceMethodSignature(), Strings.join(requestParams, ","), "<span style='color:blue'>" + Strings.join(
                    requestParams, "</span>,<span style='color:blue'>") + "</span>"));
            } else {
                final String param = Strings.substringAfterLast("," + Strings.substringBetween(action.getServiceMethodSignature(), "(", ")"), ",");
                pw.println(Strings.replace(Strings.replace(action.getServiceMethodSignature(), Strings.join(requestParams, ","), "<span style='color:blue'>"
                        + Strings.join(requestParams, "</span>,<span style='color:blue'>") + "</span>"), param + ")", "<span style='color:darkred'>" + param
                                + "</span>)"));
            }
            pw.println("</td></tr>");
            pw.println("</table>");
        }
    }

    protected void explainService(final HttpServletResponse resp) throws IOException {
        resp.getWriter().println(serializeResponse(serviceDescriptor));
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    protected void explainServiceAsHTML(final String requestURL, final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=" + characterEncoding);
        @SuppressWarnings("resource")
        final PrintWriter pw = resp.getWriter();
        pw.println("<html><head><style>* {font-family:Tahoma} table * {font-size:8pt}</style><title>");
        pw.println(getServiceInterface().getSimpleName());
        pw.println(" Service Definition</title></head><body>");
        pw.println("<h1>");
        pw.println(getServiceInterface().getSimpleName());
        pw.println("</h1>");
        pw.println("<p>Supported Content Type: <b>");
        pw.println(contentType);
        pw.println("</b></p>");

        pw.println("<h3>RESTful resource actions</h3>");
        explainActionsAsHTML(pw, false, requestURL);

        pw.println("<h3>Fallback resource actions for HTTP clients without support for PUT, DELETE, HEAD</h3>");
        explainActionsAsHTML(pw, true, requestURL);

        pw.println("</body></html>");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    public void handleRequest(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding(characterEncoding);
        resp.setCharacterEncoding(characterEncoding);
        resp.setContentType(contentType);

        final HttpRequestMethod reqMethod = HttpRequestMethod.valueOf(req.getMethod().toUpperCase());
        if (reqMethod == HttpRequestMethod.GET) {
            if (req.getParameter("explain") != null) {
                explainService(resp);
                return;
            }
            if (req.getParameter("explainAsHTML") != null) {
                explainServiceAsHTML(req.getRequestURL().toString(), resp);
                return;
            }
        }

        final String beanName = req.getAttribute("beanName").toString();
        final String requestParams = Strings.substringAfter(req.getPathInfo(), beanName + "/");

        final RestResourceAction action = serviceActions.getResourceAction(reqMethod, requestParams);

        if (action == null) {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            resp.getWriter().println(serializeResponse(new RestServiceError("UnsupportedOperationException", "Unsupported HTTP request method " + reqMethod
                    + " for resource " + requestParams)));
            return;
        }

        LOG.debug("Invoking [%s]...", action);

        final String argsString = requestParams.substring(action.getResourceName().length());
        String[] args = "".equals(argsString) ? ArrayUtils.EMPTY_STRING_ARRAY : Strings.split(argsString.substring(1), "/");
        if (args.length < action.getRequiredURLParameterCount()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(serializeResponse(new RestServiceError("MissingArgumentException", "Missing parameter " + action
                .getParameterNames()[args.length])));
            return;
        }

        // if the service method has var args and the var args are not provided add a null element to the args list
        if (args.length < action.getServiceMethod().getParameterTypes().length && action.getServiceMethod().isVarArgs()) {
            args = ArrayUtils.add(args, null);
        }

        // converting URL parameters to the required object values
        Object[] methodArguments = Beans.valuesOf(args, action.getServiceMethod().getParameterTypes());

        // for POST/PUT requests add the request body as additional argument for the method arguments
        if (reqMethod == HttpRequestMethod.POST || reqMethod == HttpRequestMethod.PUT) {
            methodArguments = ArrayUtils.add(methodArguments, deserializeRequestBody(action.getHttpRequestBodyType(), req));
        }

        try {
            final Object methodReturnValue = action.getServiceMethod().invoke(getService(), methodArguments);
            onServiceMethodInvoked(action.getServiceMethod(), methodArguments, methodReturnValue);

            switch (reqMethod) {
                case POST:
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    break;
                default:
                    resp.setStatus(HttpServletResponse.SC_OK);
                    break;
            }

            resp.getWriter().println(serializeResponse(methodReturnValue));
        } catch (final Exception ex) {
            LOG.error(ex, "Invoking method [%s] failed.", action.getServiceMethod());

            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(serializeResponse(new RestServiceError(ex.getClass().getSimpleName(), ex.getMessage())));
            return;
        }
    }

    @SuppressWarnings("unused")
    protected void onServiceMethodInvoked(final Method m, final Object[] args, final Object returnValue) {
        // may be sub-classed, e.g. for logging
    }

    protected abstract String serializeResponse(final Object resultObject) throws IOException;

    @Override
    public synchronized void setService(final Object service) {

        super.setService(service);

        serviceActions = new RestResourceActionRegistry();
        serviceDescriptor = new RestServiceDescriptor();

        if (service == null)
            return;

        // initialize resourceActions map
        for (final Method m : getServiceInterface().getMethods()) {
            if (m.isAnnotationPresent(REST_GET.class)) {
                serviceActions.registerResourceAction(m.getAnnotation(REST_GET.class).value(), HttpRequestMethod.GET, m, getService());
            }

            if (m.isAnnotationPresent(REST_POST.class)) {
                serviceActions.registerResourceAction(m.getAnnotation(REST_POST.class).value(), HttpRequestMethod.POST, m, getService());
            }

            if (m.isAnnotationPresent(REST_PUT.class)) {
                final REST_PUT annotation = m.getAnnotation(REST_PUT.class);
                final RestResourceAction action = serviceActions.registerResourceAction(annotation.value(), HttpRequestMethod.PUT, m, getService());
                if (Strings.isNotEmpty(annotation.fallback())) {
                    serviceActions.registerFallbackResourceAction(annotation.fallback(), m, getService(), action);
                }
            }

            if (m.isAnnotationPresent(REST_PATCH.class)) {
                final REST_PATCH annotation = m.getAnnotation(REST_PATCH.class);
                final RestResourceAction action = serviceActions.registerResourceAction(annotation.value(), HttpRequestMethod.PATCH, m, getService());
                if (Strings.isNotEmpty(annotation.fallback())) {
                    serviceActions.registerFallbackResourceAction(annotation.fallback(), m, getService(), action);
                }
            }

            if (m.isAnnotationPresent(REST_DELETE.class)) {
                final REST_DELETE annotation = m.getAnnotation(REST_DELETE.class);
                final RestResourceAction action = serviceActions.registerResourceAction(annotation.value(), HttpRequestMethod.DELETE, m, getService());
                if (Strings.isNotEmpty(annotation.fallback())) {
                    serviceActions.registerFallbackResourceAction(annotation.fallback(), m, getService(), action);
                }
            }

            if (m.isAnnotationPresent(REST_HEAD.class)) {
                final REST_HEAD annotation = m.getAnnotation(REST_HEAD.class);
                final RestResourceAction action = serviceActions.registerResourceAction(annotation.value(), HttpRequestMethod.HEAD, m, getService());
                if (Strings.isNotEmpty(annotation.fallback())) {
                    serviceActions.registerFallbackResourceAction(annotation.fallback(), m, getService(), action);
                }
            }
        }

        final List<RestResourceAction> actions = serviceActions.getAllResourceActions();
        Collections.sort(actions, new Comparator<RestResourceAction>() {
            public int compare(final RestResourceAction o1, final RestResourceAction o2) {
                return ObjectUtils.compare(o1.getRequestURITemplate(), o2.getRequestURITemplate());
            }
        });
        serviceDescriptor.setActions(actions);
    }
}
