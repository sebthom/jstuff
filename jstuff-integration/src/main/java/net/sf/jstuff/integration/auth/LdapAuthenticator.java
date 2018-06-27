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
package net.sf.jstuff.integration.auth;

import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;

import net.sf.jstuff.core.functional.Invocable;
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

   public boolean authenticate(final String logonName, final String password) {
      LOG.trace("Trying to authenticate user %s", logonName);
      try {
         ldapTemplate.execute(new Invocable<Object, LdapContext, NamingException>() {
            public Object invoke(final LdapContext ctx) throws NamingException {
               final UserDetails userDetails = userDetailsService.getUserDetailsByLogonName(logonName);
               ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "simple");
               ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDetails.getDistingueshedName());
               ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
               return ctx.lookup(userDetails.getDistingueshedName());
            }
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
