/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
 * <filter>
 *     <filter-name>DummyPrincipalInjectingFilter</filter-name>
 *     <filter-class>net.sf.jstuff.integration.servlet.DummyPrincipalInjectingFilter</filter-class>
 *     <init-param>
 *         <param-name>username</param-name>
 *         <param-value>john</param-value>
 *     </init-param>
 *     <init-param>
 *         <param-name>user-roles</param-name>
 *         <param-value>admin,monitor</param-value>
 *     </init-param>
 * </filter>
 * <filter-mapping>
 *     <filter-name>DummyPrincipalInjectingFilter</filter-name>
 *     <url-pattern>/*</url-pattern>
 * </filter-mapping>
 * }
 * </pre>
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DummyPrincipalInjectingFilter implements Filter {
   private static final Logger LOG = Logger.create();

   public static final String USER_NAME_SYSTEM_PROPERTY = "net.sf.jstuff.integration.servlet.dummyUsername";
   public static final String USER_ROLES_SYSTEM_PROPERTY = "net.sf.jstuff.integration.servlet.dummyUserRoles";
   public static final String USER_ROLES_SEPARATOR = ",";

   // CHECKSTYLE:IGNORE StaticVariableName FOR NEXT 2 LINES
   public static String DEFAULT_USER_NAME;
   public static String[] DEFAULT_USER_ROLES;

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
               return userRoles.contains(roleName);
            }
         };
      }
   };

   private final Principal user = new Principal() {

      @Override
      public String getName() {
         return username;
      }

      @Override
      public String toString() {
         return Principal.class.getName() + "[name=" + username + ", roles=" + userRoles + "]";
      }
   };
   private String username;
   private final Set<String> userRoles = new HashSet<>();

   public DummyPrincipalInjectingFilter() {
      LOG.infoNew(this);
   }

   @Override
   public void destroy() {
   }

   @Override
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

   @Override
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
      if (userRoles.isEmpty()) {
         final String userRoles = config.getInitParameter("user-roles");
         if (userRoles != null) {
            setUserRoles(userRoles.split(USER_ROLES_SEPARATOR));
         }
      }

      // configure based on static default values
      if (DEFAULT_USER_NAME != null && user.getName() == null) {
         setUsername(DEFAULT_USER_NAME);
      }
      if (DEFAULT_USER_ROLES != null && userRoles.isEmpty()) {
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
