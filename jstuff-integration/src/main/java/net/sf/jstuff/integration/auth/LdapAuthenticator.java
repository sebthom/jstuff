/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.lateNonNull;

import javax.naming.Context;

import jakarta.inject.Inject;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.integration.ldap.LdapException;
import net.sf.jstuff.integration.ldap.LdapTemplate;
import net.sf.jstuff.integration.userregistry.UserDetails;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapAuthenticator implements Authenticator {
   private static final Logger LOG = Logger.create();

   protected LdapTemplate ldapTemplate = lateNonNull();
   protected UserDetailsService userDetailsService = lateNonNull();

   public LdapAuthenticator() {
      LOG.infoNew(this);
   }

   @Override
   public boolean authenticate(final String logonName, final String password) {
      LOG.trace("Trying to authenticate user %s", logonName);
      final UserDetails userDetails = userDetailsService.getUserDetailsByLogonName(logonName);
      if (userDetails == null) {
         LOG.trace("Authentication failed. Unkown user with loginName=%s", logonName);
         return false;
      }
      try {
         ldapTemplate.execute(ctx -> {
            ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDetails.getDistinguishedName());
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            return ctx.lookup(userDetails.getDistinguishedName());
         });
         return true;
      } catch (final LdapException ex) {
         LOG.trace("Authentication failed.", ex);
         return false;
      }
   }

   @Inject
   public void setLdapTemplate(final LdapTemplate ldapTemplate) {
      this.ldapTemplate = ldapTemplate;
   }

   @Inject
   public void setUserDetailsService(final UserDetailsService userDetailsService) {
      this.userDetailsService = userDetailsService;
   }
}
