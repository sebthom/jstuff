/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.springframework.util.PatternMatchUtils;

import net.sf.jstuff.core.Strings;
import net.sf.jstuff.core.collection.MapWithSets;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.integration.userregistry.GroupDetailsService;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultAuthService implements AuthService {
   private static final Logger LOG = Logger.create();

   protected Set<String> applicationRoles = lazyNonNull();
   protected Authenticator authenticator = lazyNonNull();
   protected GroupDetailsService groupDetailsService = lazyNonNull();
   protected MapWithSets<String, String> groupIdToApplicationRoleMappings = lazyNonNull();
   protected @Nullable AuthListener listener;
   protected MapWithSets<String, String> uriPatternsToApplicationRoleMappings = lazyNonNull();
   protected UserDetailsService userDetailsService = lazyNonNull();

   public DefaultAuthService() {
      LOG.infoNew(this);
   }

   @Override
   public void assertAuthenticated() throws PermissionDeniedException {
      if (!isAuthenticated())
         throw new PermissionDeniedException("You are not authorized to perform that operation. You need to authenticate first.");
   }

   @Override
   public void assertIdentity(final String userId) throws PermissionDeniedException {
      if (!isIdentity(userId))
         throw new PermissionDeniedException("You are not authorized to perform that operation. Identity mismatch.");
   }

   @Override
   public void assertRole(final String applicationRole) throws PermissionDeniedException {
      if (!hasRole(applicationRole))
         throw new PermissionDeniedException("You are not authorized to perform that operation.");
   }

   @Override
   public void assertURIAccess(final String uri) throws PermissionDeniedException {
      for (final Entry<String, Set<String>> entry : uriPatternsToApplicationRoleMappings.entrySet()) {
         final String uriPattern = entry.getKey();

         if (PatternMatchUtils.simpleMatch(uriPattern, uri)) {
            LOG.trace("%s matches %s", uri, uriPattern);

            final Collection<String> roles = entry.getValue();
            if (roles.isEmpty()) {
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

   @Override
   public Authentication getAuthentication() {
      return AuthenticationHolder.getAuthentication();
   }

   @Override
   public Set<String> getGrantedRoles() {
      final Authentication auth = AuthenticationHolder.getAuthentication();
      if (!auth.isAuthenticated()) {
         LOG.trace("User is not authenticated.");
         return Collections.emptySet();
      }
      return getGrantedRoles(asNonNull(auth.getUserDetails()).getDistinguishedName());
   }

   protected Set<String> getGrantedRoles(final @Nullable String userDN) {
      if (userDN == null || Strings.isBlank(userDN))
         return Collections.emptySet();

      final Set<String> groupIds = groupDetailsService.getGroupIdsByUserDN(userDN);
      final Set<String> roles = new HashSet<>();

      for (final String groupId : groupIds) {
         final Collection<String> coll = groupIdToApplicationRoleMappings.get(groupId);
         if (coll != null) {
            roles.addAll(coll);
         }
      }
      return roles;
   }

   @Override
   public Set<String> getGroupIds() {
      final Authentication auth = AuthenticationHolder.getAuthentication();
      if (!auth.isAuthenticated()) {
         LOG.trace("User is not authenticated.");
         return Collections.emptySet();
      }
      return groupDetailsService.getGroupIdsByUserDN(asNonNull(asNonNull(auth.getUserDetails()).getDistinguishedName()));
   }

   @Override
   public boolean hasRole(final String applicationRole) {
      return getGrantedRoles().contains(applicationRole);
   }

   @Override
   public boolean isAuthenticated() {
      return AuthenticationHolder.getAuthentication().isAuthenticated();
   }

   @Override
   public boolean isIdentity(final String userId) throws PermissionDeniedException {
      final Authentication auth = AuthenticationHolder.getAuthentication();

      return auth.isAuthenticated() && asNonNull(auth.getUserDetails()).getUserId().equals(userId);
   }

   @Override
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

   @Override
   public void logout() {
      final Authentication auth = AuthenticationHolder.getAuthentication();
      auth.invalidate();

      final var listener = this.listener;
      if (listener != null) {
         final var userDetails = auth.getUserDetails();
         if (userDetails != null) {
            listener.afterLogout(userDetails);
         }
      }
   }

   /**
    * @param applicationRoles the applicationRoles to set
    */
   @Inject
   public synchronized void setApplicationRoles(final @NonNull String... applicationRoles) {
      this.applicationRoles = new HashSet<>();
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
   @Inject
   public synchronized void setGroupIdToApplicationRoleMappings(final Map<String, String> mappings) throws UnknownApplicationRoleException {
      groupIdToApplicationRoleMappings = new MapWithSets<>();
      for (final Entry<String, String> mapping : mappings.entrySet()) {
         final String group = mapping.getKey().trim();
         if (group.length() > 0) {
            final var role = mapping.getValue().trim();
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
   @Inject
   public synchronized void setGroupIdToApplicationRoleMappingsViaStringArray(final @NonNull String[] mappings)
      throws UnknownApplicationRoleException {
      groupIdToApplicationRoleMappings = new MapWithSets<>();
      for (final var element : mappings) {
         final var pair = Strings.split(element, '=');
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

   @Override
   public void setListener(final AuthListener listener) {
      this.listener = listener;
   }

   /**
    * @param mappings format = "/myuri*=roleXXX"
    */
   @Inject
   public synchronized void setUriPatternsToApplicationRoleMappings(final @NonNull String[] mappings)
      throws UnknownApplicationRoleException {
      uriPatternsToApplicationRoleMappings = new MapWithSets<>();
      for (final var element : mappings) {
         final var pair = Strings.split(element, '=');
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
