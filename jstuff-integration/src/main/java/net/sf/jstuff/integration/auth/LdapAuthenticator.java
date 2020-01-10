/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.auth;

import javax.inject.Inject;
import javax.naming.Context;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.integration.ldap.LdapException;
import net.sf.jstuff.integration.ldap.LdapTemplate;
import net.sf.jstuff.integration.userregistry.UserDetails;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapAuthenticator implements Authenticator {
   private static final Logger LOG = Logger.create();

   protected LdapTemplate ldapTemplate;
   protected UserDetailsService userDetailsService;

   public LdapAuthenticator() {
      LOG.infoNew(this);
   }

   @Override
   public boolean authenticate(final String logonName, final String password) {
      LOG.trace("Trying to authenticate user %s", logonName);
      try {
         ldapTemplate.execute(ctx -> {
            final UserDetails userDetails = userDetailsService.getUserDetailsByLogonName(logonName);
            ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
            ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDetails.getDistingueshedName());
            ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            return ctx.lookup(userDetails.getDistingueshedName());
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
