/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.auth;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.util.PatternMatchUtils;

import net.sf.jstuff.core.collection.MapWithSets;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.userregistry.GroupDetailsService;
import net.sf.jstuff.integration.userregistry.UserDetails;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultAuthService implements AuthService {
   private static final Logger LOG = Logger.create();

   protected Set<String> applicationRoles;
   protected Authenticator authenticator;
   protected GroupDetailsService groupDetailsService;
   protected MapWithSets<String, String> groupIdToApplicationRoleMappings;
   protected AuthListener listener;
   protected MapWithSets<String, String> uriPatternsToApplicationRoleMappings;
   protected UserDetailsService userDetailsService;

   public DefaultAuthService() {
      LOG.infoNew(this);
   }

   public void assertAuthenticated() throws PermissionDeniedException {
      if (!isAuthenticated())
         throw new PermissionDeniedException("You are not authorized to perform that operation. You need to authenticate first.");
   }

   public void assertIdentity(final String userId) throws PermissionDeniedException {
      if (!isIdentity(userId))
         throw new PermissionDeniedException("You are not authorized to perform that operation. Identity mismatch.");
   }

   public void assertRole(final String applicationRole) throws PermissionDeniedException {
      if (!hasRole(applicationRole))
         throw new PermissionDeniedException("You are not authorized to perform that operation.");
   }

   public void assertURIAccess(final String uri) throws PermissionDeniedException {
      for (final Entry<String, Set<String>> entry : uriPatternsToApplicationRoleMappings.entrySet()) {
         final String uriPattern = entry.getKey();

         if (PatternMatchUtils.simpleMatch(uriPattern, uri)) {
            LOG.trace("%s matches %s", uri, uriPattern);

            final Collection<String> roles = entry.getValue();
            if (roles.size() == 0) {
               continue;
            }

            boolean hasAnyRequiredRole = false;
            for (final String requiredRole : roles)
               if (hasRole(requiredRole)) {
                  hasAnyRequiredRole = true;
                  break;
               }
            if (!hasAnyRequiredRole)
               throw new PermissionDeniedException("You are not authorized to perform that operation.");
         } else {
            LOG.trace("%s does NOT match %s", uri, uriPattern);
         }
      }
   }

   public Authentication getAuthentication() {
      return AuthenticationHolder.getAuthentication();
   }

   public Set<String> getGrantedRoles() {
      final Authentication auth = AuthenticationHolder.getAuthentication();
      if (!auth.isAuthenticated()) {
         LOG.trace("User is not authenticated.");
         return null;
      }
      return getGrantedRoles(auth.getUserDetails().getDistingueshedName());
   }

   protected Set<String> getGrantedRoles(final String userDN) {
      Args.notEmpty("userDN", userDN);

      final Set<String> groupIds = groupDetailsService.getGroupIdsByUserDN(userDN);
      final Set<String> roles = new HashSet<String>();

      for (final String groupId : groupIds) {
         final Collection<String> coll = groupIdToApplicationRoleMappings.get(groupId);
         if (coll != null) {
            roles.addAll(coll);
         }
      }
      return roles;
   }

   public Set<String> getGroupIds() {
      final Authentication auth = AuthenticationHolder.getAuthentication();
      if (!auth.isAuthenticated()) {
         LOG.trace("User is not authenticated.");
         return null;
      }
      return groupDetailsService.getGroupIdsByUserDN(auth.getUserDetails().getDistingueshedName());
   }

   public boolean hasRole(final String applicationRole) {
      final Set<String> roles = getGrantedRoles();
      if (roles == null)
         return false;
      return roles.contains(applicationRole);
   }

   public boolean isAuthenticated() {
      return AuthenticationHolder.getAuthentication().isAuthenticated();
   }

   public boolean isIdentity(final String userId) throws PermissionDeniedException {
      final Authentication auth = AuthenticationHolder.getAuthentication();

      return auth.isAuthenticated() && auth.getUserDetails().getUserId().equals(userId);
   }

   public void login(final String logonName, final String password) throws AuthenticationFailedException, AlreadyAuthenticatedException {
      if (isAuthenticated())
         throw new AlreadyAuthenticatedException("An authentication for the active session already exists.");

      if (isAuthenticated()) {
         logout();
      }

      if (!authenticator.authenticate(logonName, password))
         throw new AuthenticationFailedException("Incorrect username or password.");

      final Authentication auth = new DefaultAuthentication(userDetailsService.getUserDetailsByLogonName(logonName), password);
      AuthenticationHolder.setAuthentication(auth);

      if (listener != null) {
         listener.afterLogin(auth);
      }
   }

   public void logout() {
      AuthenticationHolder.getAuthentication().invalidate();
      if (listener != null) {
         final UserDetails details = AuthenticationHolder.getAuthentication().getUserDetails();
         listener.afterLogout(details);
      }
   }

   /**
    * @param applicationRoles the applicationRoles to set
    */
   @Inject
   public synchronized void setApplicationRoles(final String[] applicationRoles) {
      this.applicationRoles = new HashSet<String>();
      for (final String element : applicationRoles) {
         LOG.trace("Registering application role: %s", element);
         this.applicationRoles.add(element);
      }
   }

   /**
    * @param authenticator the authenticator to set
    */
   @Inject
   public void setAuthenticator(final Authenticator authenticator) {
      this.authenticator = authenticator;
   }

   /**
    * @param groupDetailsService the groupDetailsService to set
    */
   @Inject
   public void setGroupDetailsService(final GroupDetailsService groupDetailsService) {
      this.groupDetailsService = groupDetailsService;
   }

   /**
    * @param mappings format ? groupIdXXX -> roleXXX
    */
   public synchronized void setGroupIdToApplicationRoleMappings(final Map<String, String> mappings) throws UnknownApplicationRoleException {
      groupIdToApplicationRoleMappings = new MapWithSets<String, String>();
      for (final Entry<String, String> mapping : mappings.entrySet()) {
         final String group = mapping.getKey().trim();
         String role = mapping.getValue();
         if (group.length() > 0 && role != null) {
            role = role.trim();

            if (role.length() > 0) {
               LOG.trace("Registering groupId -> application role mapping: %s => %s", group, role);

               if (!applicationRoles.contains(role))
                  throw new UnknownApplicationRoleException("Application role is unknown: " + role);

               groupIdToApplicationRoleMappings.add(group, role);
            }
         }
      }
   }

   /**
    * @param mappings format = "groupIdXXX=roleXXX"
    */
   public synchronized void setGroupIdToApplicationRoleMappingsViaStringArray(final String[] mappings) throws UnknownApplicationRoleException {
      groupIdToApplicationRoleMappings = new MapWithSets<String, String>();
      for (final String element : mappings) {
         final String[] pair = element.split("=");
         pair[0] = pair[0].trim();
         pair[1] = pair[1].trim();

         if (pair[0].length() == 0 || pair[1].length() == 0) {
            continue;
         }

         LOG.trace("Registering groupId -> application role mapping: %s => %s", pair[0], pair[1]);

         if (!applicationRoles.contains(pair[1]))
            throw new UnknownApplicationRoleException("Application role is unknown: " + pair[1]);

         groupIdToApplicationRoleMappings.add(pair[0], pair[1]);
      }
   }

   public void setListener(final AuthListener listener) {
      this.listener = listener;
   }

   /**
    * @param mappings format = "/myuri*=roleXXX"
    */
   @Inject
   public synchronized void setUriPatternsToApplicationRoleMappings(final String[] mappings) throws UnknownApplicationRoleException {
      uriPatternsToApplicationRoleMappings = new MapWithSets<String, String>();
      for (final String element : mappings) {
         final String[] pair = element.split("=");
         pair[0] = pair[0].trim();
         pair[1] = pair[1].trim();

         if (pair[0].length() == 0 || pair[1].length() == 0) {
            continue;
         }

         LOG.trace("Registering URI pattern -> application role mapping: %s => %s", pair[0], pair[1]);

         if (!applicationRoles.contains(pair[1]))
            throw new UnknownApplicationRoleException("Application role is unknown: " + pair[1]);

         uriPatternsToApplicationRoleMappings.add(pair[0], pair[1]);
      }
   }

   /**
    * @param userDetailsService the userDetailsService to set
    */
   @Inject
   public void setUserDetailsService(final UserDetailsService userDetailsService) {
      this.userDetailsService = userDetailsService;
   }
}
