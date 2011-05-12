/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.userregistry;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.functional.Invocable;
import net.sf.jstuff.integration.ldap.LDAPTemplate;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GroupDetailsServiceLDAPImpl implements GroupDetailsService
{
	private final static Logger LOG = Logger.get();

	protected String groupAttributeDisplayName;
	protected String groupAttributeGroupId;
	protected String groupAttributeMember;
	protected String groupSearchBase;
	protected String groupSearchFilter;
	protected boolean groupSearchSubtree = true;

	private LDAPTemplate ldapTemplate;

	/**
	 * {@inheritDoc}
	 */
	public GroupDetails getGroupDetailsByGroupDN(final String groupDN)
	{
		Assert.argumentNotNull("groupDN", groupDN);

		return (GroupDetails) ldapTemplate.execute(new Invocable<Object, LdapContext>()
			{
				public Object invoke(final LdapContext ctx) throws NamingException
				{
					final Attributes attr = ctx.getAttributes(groupDN, new String[]{groupAttributeDisplayName,
							groupAttributeGroupId, groupAttributeMember});

					final GroupDetailsImpl groupDetails = new GroupDetailsImpl();
					groupDetails.setDisplayName((String) attr.get(groupAttributeDisplayName).get());
					groupDetails.setDistingueshedName(groupDN);
					groupDetails.setGroupId((String) attr.get(groupAttributeGroupId).get());

					final Set<String> memberDNs = new HashSet<String>();
					for (final Enumeration< ? > en = attr.get(groupAttributeMember).getAll(); en.hasMoreElements();)
						memberDNs.add((String) en.nextElement());
					groupDetails.setMemberDNs(memberDNs.toArray(new String[memberDNs.size()]));
					return groupDetails;
				}
			});
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Set<String> getGroupIdsByUserDN(final String userDN)
	{
		Assert.argumentNotNull("userDN", userDN);

		return (Set<String>) ldapTemplate.execute(new Invocable<Object, LdapContext>()
			{
				public Object invoke(final LdapContext ctx) throws NamingException
				{
					final Set<String> groupIds = new HashSet<String>();

					LOG.trace("Performing LDAP Group Search for %s=%s", groupAttributeMember, userDN);
					for (final NamingEnumeration<SearchResult> results = searchGroup(ctx, groupAttributeMember + "="
							+ userDN, new String[]{groupAttributeGroupId}); results.hasMoreElements();)
					{
						final SearchResult sr = results.next();
						final Attributes attr = sr.getAttributes();

						groupIds.add((String) attr.get(groupAttributeGroupId).get());
					}
					LOG.trace("Found %s group(s) for user %s", groupIds.size(), userDN);
					return groupIds;
				}
			});
	}

	protected NamingEnumeration<SearchResult> searchGroup(final DirContext ctx, final String filter,
			final String[] attrs) throws NamingException
	{
		final SearchControls options = new SearchControls();
		options.setSearchScope(groupSearchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
		options.setReturningAttributes(attrs);

		return ctx.search(groupSearchBase, //
				"(&(" + filter + ")(" + groupSearchFilter + "))", //
				options);
	}

	/**
	 * @param groupAttributeDisplayName the groupAttributeDisplayName to set
	 */
	@Required
	public void setGroupAttributeDisplayName(final String groupAttributeDisplayName)
	{
		Assert.argumentNotNull("groupAttributeDisplayName", groupAttributeDisplayName);

		this.groupAttributeDisplayName = groupAttributeDisplayName;
	}

	/**
	 * @param groupAttributeGroupId the groupAttributeGroupId to set
	 */
	@Required
	public void setGroupAttributeGroupId(final String groupAttributeGroupId)
	{
		Assert.argumentNotNull("groupAttributeGroupId", groupAttributeGroupId);

		this.groupAttributeGroupId = groupAttributeGroupId;
	}

	/**
	 * @param groupAttributeMember the groupAttributeMember to set
	 */
	@Required
	public void setGroupAttributeMember(final String groupAttributeMember)
	{
		Assert.argumentNotNull("groupAttributeMember", groupAttributeMember);

		this.groupAttributeMember = groupAttributeMember;
	}

	/**
	 * @param groupSearchBase the groupSearchBase to set
	 */
	@Required
	public void setGroupSearchBase(final String groupSearchBase)
	{
		Assert.argumentNotNull("groupSearchBase", groupSearchBase);

		this.groupSearchBase = groupSearchBase;
	}

	/**
	 * @param groupSearchFilter the groupSearchFilter to set
	 */
	@Required
	public void setGroupSearchFilter(final String groupSearchFilter)
	{
		Assert.argumentNotNull("groupSearchFilter", groupSearchFilter);

		this.groupSearchFilter = groupSearchFilter;
	}

	/**
	 * @param groupSearchSubtree the groupSearchSubtree to set
	 */
	public void setGroupSearchSubtree(final boolean groupSearchSubtree)
	{
		this.groupSearchSubtree = groupSearchSubtree;
	}

	/**
	 * @param ldapTemplate the ldapTemplate to set
	 */
	@Required
	public void setLdapTemplate(final LDAPTemplate ldapTemplate)
	{
		Assert.argumentNotNull("ldapTemplate", ldapTemplate);

		this.ldapTemplate = ldapTemplate;
	}
}
