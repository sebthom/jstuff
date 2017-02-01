/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.integration.auth;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * Class filter requests and fetch auth object from session and
 * authenticate users session against container.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SecurityFilter implements Filter {
    private static final Logger LOG = Logger.create();
    private static final String SESSION_AUTHENTICATION_ATTRIBUTE = Authentication.class.getName();

    public static final ThreadLocal<HttpServletRequest> HTTP_SERVLET_REQUEST_HOLDER = new ThreadLocal<HttpServletRequest>();

    private AuthService authService;
    private UserDetailsService userDetailsService;

    public SecurityFilter() {
        LOG.infoNew(this);
    }

    public void destroy() {
        // do nothing
    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        HTTP_SERVLET_REQUEST_HOLDER.set(req);

        final HttpSession sess = req.getSession();
        LOG.debug("URI = %s", req.getRequestURI());

        boolean wasLoggedInBeforeChain = false;

        // check if we have an existing auth object in the session
        Authentication auth = (Authentication) sess.getAttribute(SESSION_AUTHENTICATION_ATTRIBUTE);
        try {
            if (auth == null)
                if (req.getRemoteUser() != null) {
                // build a auth object based on form-based login
                auth = new DefaultAuthentication(userDetailsService.getUserDetailsByLogonName(req.getRemoteUser()), (String) sess.getAttribute("j_password"));
                sess.removeAttribute("j_password");
                sess.setAttribute(SESSION_AUTHENTICATION_ATTRIBUTE, auth);
            } else
                auth = DefaultAuthentication.UNBOUND;
            AuthenticationHolder.setAuthentication(auth);

            wasLoggedInBeforeChain = auth.isAuthenticated();
            authService.assertURIAccess(req.getRequestURI().substring(req.getContextPath().length()));
            chain.doFilter(request, response);
        } catch (final PermissionDeniedException ex) {
            if (!response.isCommitted())
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
            else
                throw ex;
        } finally {
            auth = AuthenticationHolder.getAuthentication();
            if (wasLoggedInBeforeChain && !auth.isAuthenticated())
                sess.invalidate();
            else if (!wasLoggedInBeforeChain && auth.isAuthenticated())
                sess.setAttribute(SESSION_AUTHENTICATION_ATTRIBUTE, AuthenticationHolder.getAuthentication());
            AuthenticationHolder.setAuthentication(DefaultAuthentication.UNBOUND);
            HTTP_SERVLET_REQUEST_HOLDER.remove();
        }

    }

    public void init(final FilterConfig cfg) throws ServletException {
        // do nothing
    }

    /**
     * @param authService the authService to set
     */
    @Inject
    public void setAuthService(final AuthService authService) {
        this.authService = authService;
    }

    /**
     * @param userDetailsService the userDetailsService to set
     */
    @Inject
    public void setUserDetailsService(final UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
