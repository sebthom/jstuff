/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry.ldap;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import javax.inject.Inject;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.ldap.LdapTemplate;
import net.sf.jstuff.integration.ldap.LdapUtils;
import net.sf.jstuff.integration.userregistry.DefaultUserDetails;
import net.sf.jstuff.integration.userregistry.UserDetails;
import net.sf.jstuff.integration.userregistry.UserDetailsService;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapUserDetailsService implements UserDetailsService {
   private static final Logger LOG = Logger.create();

   protected LdapTemplate ldapTemplate = lazyNonNull();
   protected String userAttributeDisplayName = lazyNonNull();
   protected String userAttributeEMailAdress = lazyNonNull();
   protected String userAttributeLogonName = lazyNonNull();
   protected String userAttributeUserId = lazyNonNull();
   protected String userSearchBase = lazyNonNull();
   protected String userSearchFilter = lazyNonNull();
   protected boolean userSearchSubtree = true;

   public LdapUserDetailsService() {
      LOG.infoNew(this);
   }

   public String getUserAttributeLogonName() {
      return userAttributeLogonName;
   }

   protected @Nullable UserDetails getUserDetailsByFilter(final String filter) {
      Args.notNull("filter", filter);

      return ldapTemplate.execute(ctx -> {
         final var results = searchUser(ctx, filter, new String[] { //
            userAttributeDisplayName, //
            userAttributeEMailAdress, //
            userAttributeLogonName, //
            userAttributeUserId //
         });
         if (!results.hasMore())
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
            LdapUtils.getAttributeValue(attr, userAttributeUserId, "n/a"), //
            LdapUtils.getAttributeValue(attr, userAttributeDisplayName, "n/a"), //
            LdapUtils.getAttributeValue(attr, userAttributeLogonName, null), //
            dn.toString(), //
            LdapUtils.getAttributeValue(attr, userAttributeEMailAdress, null) //
         );
      });
   }

   @Override
   public @Nullable UserDetails getUserDetailsByLogonName(final String logonName) {
      Args.notNull("logonName", logonName);

      return getUserDetailsByFilter(userAttributeLogonName + "=" + LdapUtils.ldapEscape(logonName));
   }

   @Override
   public @Nullable UserDetails getUserDetailsByUserId(final String userId) {
      Args.notNull("userId", userId);

      return getUserDetailsByFilter(userAttributeUserId + "=" + LdapUtils.ldapEscape(userId));
   }

   protected NamingEnumeration<SearchResult> searchUser(final DirContext ctx, final String filter, final String[] attrs)
      throws NamingException {
      final var options = new SearchControls();
      options.setSearchScope(userSearchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
      options.setReturningAttributes(attrs);

      return ctx.search(userSearchBase, //
         "(&(" + filter + ")(" + userSearchFilter + "))", //
         options);
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
