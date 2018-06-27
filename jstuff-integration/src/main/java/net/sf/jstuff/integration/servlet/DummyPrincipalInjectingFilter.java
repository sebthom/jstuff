/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
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
package net.sf.jstuff.integration.servlet;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.sf.jstuff.core.collection.CollectionUtils;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.reflection.Types;

/**
 * <b>Example:</b>
 *
 * <pre>
 * {@code
<filter>
    <filter-name>DummyPrincipalInjectingFilter</filter-name>
    <filter-class>net.sf.jstuff.integration.servlet.DummyPrincipalInjectingFilter</filter-class>
    <init-param>
        <param-name>username</param-name>
        <param-value>john</param-value>
    </init-param>
    <init-param>
        <param-name>user-roles</param-name>
        <param-value>admin,monitor</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>DummyPrincipalInjectingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
}
 * </pre>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DummyPrincipalInjectingFilter implements Filter {
   private static final Logger LOG = Logger.create();

   public static final String USER_NAME_SYSTEM_PROPERTY = "net.sf.jstuff.integration.servlet.dummyUsername";
   public static final String USER_ROLES_SYSTEM_PROPERTY = "net.sf.jstuff.integration.servlet.dummyUserRoles";
   public static final String USER_ROLES_SEPARATOR = ",";

   // CHECKSTYLE:IGNORE StaticVariableName FOR NEXT 2 LINES
   public static String DEFAULT_USER_NAME = null;
   public static String[] DEFAULT_USER_ROLES = null;

   private final ThreadLocal<HttpServletRequestWrapper> requestWrapper = new ThreadLocal<HttpServletRequestWrapper>() {
      final HttpServletRequest init = Types.createMixin(HttpServletRequest.class, new Object());

      @Override
      protected synchronized HttpServletRequestWrapper initialValue() {
         return new HttpServletRequestWrapper(init) {

            @Override
            public String getRemoteUser() {
               return user.getName();
            }

            @Override
            public Principal getUserPrincipal() {
               return user;
            }

            @Override
            public boolean isUserInRole(final String roleName) {
               return userRoles != null && userRoles.contains(roleName);
            }
         };
      }
   };

   private final Principal user = new Principal() {

      public String getName() {
         return username;
      }

      @Override
      public String toString() {
         return Principal.class.getName() + "[name=" + username + ", roles=" + userRoles + "]";
      }
   };
   private String username;
   private final Set<String> userRoles = new HashSet<String>();

   public DummyPrincipalInjectingFilter() {
      LOG.infoNew(this);
   }

   public void destroy() {
   }

   public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
      if (username != null && request instanceof HttpServletRequest) {
         LOG.debug("Injecting dummy user [%s]...", username);
         final HttpServletRequestWrapper wrapper = requestWrapper.get();
         wrapper.setRequest(request);
         chain.doFilter(wrapper, response);
      } else {
         chain.doFilter(request, response);
      }
   }

   public String getUsername() {
      return username;
   }

   public Set<String> getUserRoles() {
      return userRoles;
   }

   public void init(final FilterConfig config) throws ServletException {
      // configure based on system properties
      {
         final String uname = System.getProperty(USER_NAME_SYSTEM_PROPERTY);
         if (uname != null) {
            setUsername(uname);
         }

         final String userRoles = System.getProperty(USER_ROLES_SYSTEM_PROPERTY);
         if (userRoles != null) {
            setUserRoles(userRoles.split(USER_ROLES_SEPARATOR));
         }
      }

      // configure based on servlet parameters
      if (user.getName() == null) {
         final String uname = config.getInitParameter("username");
         if (uname != null) {
            setUsername(uname);
         }
      }
      if (userRoles.size() == 0) {
         final String userRoles = config.getInitParameter("user-roles");
         if (userRoles != null) {
            setUserRoles(userRoles.split(USER_ROLES_SEPARATOR));
         }
      }

      // configure based on static default values
      if (user.getName() == null && DEFAULT_USER_NAME != null) {
         setUsername(DEFAULT_USER_NAME);
      }
      if (userRoles.size() == 0 && DEFAULT_USER_ROLES != null) {
         setUserRoles(DEFAULT_USER_ROLES);
      }

   }

   public void setUsername(final String name) {
      username = name;
   }

   public void setUserRoles(final Collection<String> userRoles) {
      this.userRoles.clear();
      if (userRoles != null) {
         this.userRoles.addAll(userRoles);
      }
   }

   public void setUserRoles(final String... userRoles) {
      this.userRoles.clear();
      if (userRoles != null) {
         CollectionUtils.addAll(this.userRoles, userRoles);
      }
   }
}
