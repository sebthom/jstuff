/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.userregistry.ldap;

import java.util.Iterator;

import javax.inject.Inject;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import net.sf.jstuff.core.collection.Enumerations;
import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.ldap.LdapTemplate;
import net.sf.jstuff.integration.ldap.LdapUtils;
import net.sf.jstuff.integration.userregistry.DefaultUserDetails;
import net.sf.jstuff.integration.userregistry.UserDetails;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapUserDetailsService implements UserDetailsService {
   private static final Logger LOG = Logger.create();

   protected LdapTemplate ldapTemplate;
   protected String userAttributeDisplayName;
   protected String userAttributeEMailAdress;
   protected String userAttributeLogonName;
   protected String userAttributeUserId;
   protected String userSearchBase;
   protected String userSearchFilter;
   protected boolean userSearchSubtree = true;

   public LdapUserDetailsService() {
      LOG.infoNew(this);
   }

   public String getUserAttributeLogonName() {
      return userAttributeLogonName;
   }

   protected UserDetails getUserDetailsByFilter(final String filter) {
      Args.notNull("filter", filter);

      return (UserDetails) ldapTemplate.execute(new Invocable<Object, LdapContext, NamingException>() {

         public Object invoke(final LdapContext ctx) throws NamingException {
            final Iterator<SearchResult> results = searchUser(ctx, filter, new String[] { //
               userAttributeDisplayName, //
               userAttributeEMailAdress, //
               userAttributeLogonName, //
               userAttributeUserId //
            }).iterator();
            if (!results.hasNext())
               return null;

            final SearchResult sr = results.next();

            final Attributes attr = sr.getAttributes();

            // building the user DN
            final NameParser parser = ctx.getNameParser("");
            final Name contextName = parser.parse(ctx.getNameInNamespace());
            final Name baseName = parser.parse(userSearchBase);
            final Name entryName = parser.parse(new CompositeName(sr.getName()).get(0));
            final Name dn = contextName.addAll(baseName).addAll(entryName);

            return new DefaultUserDetails( //
               (String) attr.get(userAttributeUserId).get(), //
               (String) attr.get(userAttributeDisplayName).get(), //
               (String) attr.get(userAttributeLogonName).get(), //
               dn.toString(), //
               (String) attr.get(userAttributeEMailAdress).get()//
            );
         }
      });
   }

   public UserDetails getUserDetailsByLogonName(final String logonName) {
      Args.notNull("logonName", logonName);

      return getUserDetailsByFilter(userAttributeLogonName + "=" + LdapUtils.ldapEscape(logonName));
   }

   public UserDetails getUserDetailsByUserId(final String userId) {
      Args.notNull("userId", userId);

      return getUserDetailsByFilter(userAttributeUserId + "=" + LdapUtils.ldapEscape(userId));
   }

   protected Iterable<SearchResult> searchUser(final DirContext ctx, final String filter, final String[] attrs) throws NamingException {
      final SearchControls options = new SearchControls();
      options.setSearchScope(userSearchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
      options.setReturningAttributes(attrs);

      return Enumerations.toIterable(ctx.search(userSearchBase, //
         "(&(" + filter + ")(" + userSearchFilter + "))", //
         options));
   }

   @Inject
   public void setLdapTemplate(final LdapTemplate ldapTemplate) {
      Args.notNull("ldapTemplate", ldapTemplate);

      this.ldapTemplate = ldapTemplate;
   }

   @Inject
   public void setUserAttributeDisplayName(final String userAttributeDisplayName) {
      Args.notNull("userAttributeDisplayName", userAttributeDisplayName);

      this.userAttributeDisplayName = userAttributeDisplayName;
   }

   @Inject
   public void setUserAttributeEMailAdress(final String userAttributeEMailAdress) {
      Args.notNull("userAttributeEMailAdress", userAttributeEMailAdress);

      this.userAttributeEMailAdress = userAttributeEMailAdress;
   }

   @Inject
   public void setUserAttributeLogonName(final String userAttributeLogonName) {
      Args.notNull("userAttributeLogonName", userAttributeLogonName);

      this.userAttributeLogonName = userAttributeLogonName;
   }

   @Inject
   public void setUserAttributeUserId(final String userAttributeUserId) {
      Args.notNull("userAttributeUserId", userAttributeUserId);

      this.userAttributeUserId = userAttributeUserId;
   }

   @Inject
   public void setUserSearchBase(final String userSearchBase) {
      Args.notNull("userSearchBase", userSearchBase);

      this.userSearchBase = userSearchBase;
   }

   @Inject
   public void setUserSearchFilter(final String userSearchFilter) {
      Args.notNull("userSearchFilter", userSearchFilter);

      this.userSearchFilter = userSearchFilter;
   }

   public void setUserSearchSubtree(final boolean userSearchSubtree) {
      this.userSearchSubtree = userSearchSubtree;
   }
}
