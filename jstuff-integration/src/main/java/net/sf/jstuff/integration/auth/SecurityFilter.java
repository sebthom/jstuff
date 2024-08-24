/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * Class filter requests and fetch auth object from session and
 * authenticate users session against container.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SecurityFilter implements Filter {
   private static final Logger LOG = Logger.create();
   private static final String SESSION_AUTHENTICATION_ATTRIBUTE = Authentication.class.getName();

   public static final ThreadLocal<HttpServletRequest> HTTP_SERVLET_REQUEST_HOLDER = new ThreadLocal<>();

   private AuthService authService = lateNonNull();
   private UserDetailsService userDetailsService = lateNonNull();

   public SecurityFilter() {
      LOG.infoNew(this);
   }

   @Override
   public void destroy() {
      // do nothing
   }

   @Override
   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
         ServletException {
      final HttpServletRequest req = (HttpServletRequest) request;
      HTTP_SERVLET_REQUEST_HOLDER.set(req);

      final HttpSession sess = req.getSession();
      LOG.debug("URI = %s", req.getRequestURI());

      boolean wasLoggedInBeforeChain = false;

      // check if we have an existing auth object in the session
      Authentication auth = (Authentication) sess.getAttribute(SESSION_AUTHENTICATION_ATTRIBUTE);
      try {
         if (auth == null) {
            final var remoteUser = req.getRemoteUser();
            if (remoteUser != null) {
               // build a auth object based on form-based login
               auth = new DefaultAuthentication( //
                  userDetailsService.getUserDetailsByLogonName(remoteUser), //
                  (String) sess.getAttribute("j_password"));
               sess.removeAttribute("j_password");
               sess.setAttribute(SESSION_AUTHENTICATION_ATTRIBUTE, auth);
            } else {
               auth = DefaultAuthentication.UNBOUND;
            }
         }
         AuthenticationHolder.setAuthentication(auth);

         wasLoggedInBeforeChain = auth.isAuthenticated();
         authService.assertURIAccess(req.getRequestURI().substring(req.getContextPath().length()));
         chain.doFilter(request, response);
      } catch (final PermissionDeniedException ex) {
         if (!response.isCommitted()) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
         } else
            throw ex;
      } finally {
         auth = AuthenticationHolder.getAuthentication();
         if (wasLoggedInBeforeChain && !auth.isAuthenticated()) {
            sess.invalidate();
         } else if (!wasLoggedInBeforeChain && auth.isAuthenticated()) {
            sess.setAttribute(SESSION_AUTHENTICATION_ATTRIBUTE, AuthenticationHolder.getAuthentication());
         }
         AuthenticationHolder.setAuthentication(DefaultAuthentication.UNBOUND);
         HTTP_SERVLET_REQUEST_HOLDER.remove();
      }

   }

   @Override
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
