/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import java.util.Set;

/**
 * Authentication and Authorization Service
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface AuthService {
   void assertAuthenticated() throws PermissionDeniedException;

   void assertIdentity(String userId) throws PermissionDeniedException;

   void assertRole(String applicationRole) throws PermissionDeniedException;

   /**
    * Check if resource (URI) is secure and if required roles are available
    *
    * @param uri resource address that for checking
    * @throws PermissionDeniedException throw if required are not available
    */
   void assertURIAccess(String uri) throws PermissionDeniedException;

   Authentication getAuthentication();

   /**
    * returns the application roles for the current user
    */
   Set<String> getGrantedRoles();

   Set<String> getGroupIds();

   boolean hasRole(String applicationRole);

   /**
    * Check is user is authenticated in system
    *
    * @return <code>TRUE</code> if user is authenticated<br/>
    *         <code>FALSE</code> if user is not authenticated
    */
   boolean isAuthenticated();

   boolean isIdentity(String userId);

   void login(String userName, String password) throws AuthenticationFailedException;

   void logout();

   void setListener(AuthListener listener);
}
