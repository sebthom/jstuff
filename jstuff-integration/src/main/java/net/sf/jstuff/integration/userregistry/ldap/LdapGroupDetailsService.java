/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry.ldap;

import static net.sf.jstuff.core.validation.NullAnalysisHelper.*;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.eclipse.jdt.annotation.Nullable;

import net.sf.jstuff.core.collection.Loops;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.ldap.LdapTemplate;
import net.sf.jstuff.integration.ldap.LdapUtils;
import net.sf.jstuff.integration.userregistry.DefaultGroupDetails;
import net.sf.jstuff.integration.userregistry.GroupDetails;
import net.sf.jstuff.integration.userregistry.GroupDetailsService;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapGroupDetailsService implements GroupDetailsService {
   private static final Logger LOG = Logger.create();

   protected String groupAttributeDisplayName = lazyNonNull();
   protected String groupAttributeGroupId = lazyNonNull();
   protected String groupAttributeMember = lazyNonNull();
   protected String groupSearchBase = lazyNonNull();
   protected String groupSearchFilter = lazyNonNull();
   protected boolean groupSearchSubtree = true;

   private LdapTemplate ldapTemplate = lazyNonNull();

   public LdapGroupDetailsService() {
      LOG.infoNew(this);
   }

   @Override
   public @Nullable GroupDetails getGroupDetailsByGroupDN(final String groupDN) {
      Args.notNull("groupDN", groupDN);

      return ldapTemplate.execute(ctx -> {
         final Attributes attrs = ctx.getAttributes(groupDN, new String[] {groupAttributeDisplayName, groupAttributeGroupId,
            groupAttributeMember});

         final var memberDNs = new HashSet<String>();
         final var members = attrs.get(groupAttributeMember);
         if (members != null) {
            Loops.forEach(members.getAll(), dn -> memberDNs.add(dn.toString()));
         }

         return new DefaultGroupDetails( //
            LdapUtils.getAttributeValue(attrs, groupAttributeGroupId, "n/a"), //
            LdapUtils.getAttributeValue(attrs, groupAttributeDisplayName, "n/a"), //
            groupDN, //
            memberDNs //
         );
      });
   }

   @Override
   public Set<String> getGroupIdsByUserDN(final String userDN) {
      Args.notNull("userDN", userDN);

      return ldapTemplate.execute(ctx -> {
         final var groupIds = new HashSet<String>();
         LOG.trace("Performing LDAP Group Search for %s=%s", groupAttributeMember, userDN);
         Loops.forEach(searchGroup(ctx, groupAttributeMember + "=" + userDN, new String[] {groupAttributeGroupId}), //
            sr -> groupIds.add(LdapUtils.getAttributeValue(sr, groupAttributeGroupId, "n/a")) //
         );
         LOG.trace("Found %s group(s) for user %s", groupIds.size(), userDN);
         return groupIds;
      });
   }

   protected NamingEnumeration<SearchResult> searchGroup(final DirContext ctx, final String filter, final String[] attrs)
      throws NamingException {
      final var options = new SearchControls();
      options.setSearchScope(groupSearchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
      options.setReturningAttributes(attrs);

      return ctx.search(groupSearchBase, //
         "(&(" + filter + ")(" + groupSearchFilter + "))", //
         options);
   }

   @Inject
   public void setGroupAttributeDisplayName(final String groupAttributeDisplayName) {
      Args.notNull("groupAttributeDisplayName", groupAttributeDisplayName);

      this.groupAttributeDisplayName = groupAttributeDisplayName;
   }

   @Inject
   public void setGroupAttributeGroupId(final String groupAttributeGroupId) {
      Args.notNull("groupAttributeGroupId", groupAttributeGroupId);

      this.groupAttributeGroupId = groupAttributeGroupId;
   }

   @Inject
   public void setGroupAttributeMember(final String groupAttributeMember) {
      Args.notNull("groupAttributeMember", groupAttributeMember);

      this.groupAttributeMember = groupAttributeMember;
   }

   @Inject
   public void setGroupSearchBase(final String groupSearchBase) {
      Args.notNull("groupSearchBase", groupSearchBase);

      this.groupSearchBase = groupSearchBase;
   }

   @Inject
   public void setGroupSearchFilter(final String groupSearchFilter) {
      Args.notNull("groupSearchFilter", groupSearchFilter);

      this.groupSearchFilter = groupSearchFilter;
   }

   public void setGroupSearchSubtree(final boolean groupSearchSubtree) {
      this.groupSearchSubtree = groupSearchSubtree;
   }

   @Inject
   public void setLdapTemplate(final LdapTemplate ldapTemplate) {
      Args.notNull("ldapTemplate", ldapTemplate);

      this.ldapTemplate = ldapTemplate;
   }
}
