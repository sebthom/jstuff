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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
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
import net.sf.jstuff.integration.userregistry.DefaultGroupDetails;
import net.sf.jstuff.integration.userregistry.GroupDetails;
import net.sf.jstuff.integration.userregistry.GroupDetailsService;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class LdapGroupDetailsService implements GroupDetailsService {
   private static final Logger LOG = Logger.create();

   protected String groupAttributeDisplayName;
   protected String groupAttributeGroupId;
   protected String groupAttributeMember;
   protected String groupSearchBase;
   protected String groupSearchFilter;
   protected boolean groupSearchSubtree = true;

   private LdapTemplate ldapTemplate;

   public LdapGroupDetailsService() {
      LOG.infoNew(this);
   }

   @Override
   public GroupDetails getGroupDetailsByGroupDN(final String groupDN) {
      Args.notNull("groupDN", groupDN);

      return (GroupDetails) ldapTemplate.execute(new Invocable<Object, LdapContext, NamingException>() {

         @Override
         public Object invoke(final LdapContext ctx) throws NamingException {
            final Attributes attr = ctx.getAttributes(groupDN, new String[] {groupAttributeDisplayName, groupAttributeGroupId, groupAttributeMember});

            final DefaultGroupDetails groupDetails = new DefaultGroupDetails();
            groupDetails.setDisplayName((String) attr.get(groupAttributeDisplayName).get());
            groupDetails.setDistingueshedName(groupDN);
            groupDetails.setGroupId((String) attr.get(groupAttributeGroupId).get());

            final Set<String> memberDNs = new HashSet<>();
            for (final Object dn : Enumerations.toIterable(attr.get(groupAttributeMember).getAll())) {
               memberDNs.add((String) dn);
            }
            groupDetails.setMemberDNs(memberDNs.toArray(new String[memberDNs.size()]));
            return groupDetails;
         }
      });
   }

   @Override
   @SuppressWarnings("unchecked")
   public Set<String> getGroupIdsByUserDN(final String userDN) {
      Args.notNull("userDN", userDN);

      return (Set<String>) ldapTemplate.execute(new Invocable<Object, LdapContext, NamingException>() {

         @Override
         public Object invoke(final LdapContext ctx) throws NamingException {
            final Set<String> groupIds = new HashSet<>();

            LOG.trace("Performing LDAP Group Search for %s=%s", groupAttributeMember, userDN);
            for (final SearchResult sr : searchGroup(ctx, groupAttributeMember + "=" + userDN, new String[] {groupAttributeGroupId})) {
               groupIds.add((String) sr.getAttributes().get(groupAttributeGroupId).get());
            }
            LOG.trace("Found %s group(s) for user %s", groupIds.size(), userDN);
            return groupIds;
         }
      });
   }

   protected Iterable<SearchResult> searchGroup(final DirContext ctx, final String filter, final String[] attrs) throws NamingException {
      final SearchControls options = new SearchControls();
      options.setSearchScope(groupSearchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
      options.setReturningAttributes(attrs);

      return Enumerations.toIterable(ctx.search(groupSearchBase, //
         "(&(" + filter + ")(" + groupSearchFilter + "))", //
         options));
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
