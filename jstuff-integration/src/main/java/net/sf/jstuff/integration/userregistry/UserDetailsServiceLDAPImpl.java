/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import net.sf.jstuff.core.Assert;
import net.sf.jstuff.core.Invocable;
import net.sf.jstuff.integration.ldap.LDAPTemplate;
import net.sf.jstuff.integration.ldap.LDAPUtils;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class UserDetailsServiceLDAPImpl implements UserDetailsService
{
	protected LDAPTemplate ldapTemplate;

	protected String userAttributeDisplayName;
	protected String userAttributeEMailAdress;
	protected String userAttributeLogonName;
	protected String userAttributeUserId;

	protected String userSearchBase;
	protected String userSearchFilter;
	protected boolean userSearchSubtree = true;

	protected UserDetails getUserDetailsByFilter(final String filter)
	{
		Assert.argumentNotNull("filter", filter);

		return (UserDetails) ldapTemplate.execute(new Invocable<LdapContext, Object>()
			{
				public Object invoke(final LdapContext ctx) throws NamingException
				{
					final NamingEnumeration<SearchResult> results = searchUser(ctx, filter, new String[]{
							userAttributeDisplayName, userAttributeEMailAdress, userAttributeLogonName,
							userAttributeUserId});
					if (!results.hasMore()) return null;

					final SearchResult sr = results.next();

					final Attributes attr = sr.getAttributes();

					// building the user DN
					final NameParser parser = ctx.getNameParser("");
					final Name contextName = parser.parse(ctx.getNameInNamespace());
					final Name baseName = parser.parse(userSearchBase);
					final Name entryName = parser.parse(new CompositeName(sr.getName()).get(0));
					final Name dn = contextName.addAll(baseName).addAll(entryName);

					return new UserDetailsImpl( //
							(String) attr.get(userAttributeUserId).get(),//
							(String) attr.get(userAttributeDisplayName).get(),//
							(String) attr.get(userAttributeLogonName).get(), //
							dn.toString(), //
							(String) attr.get(userAttributeEMailAdress).get()//
					);
				}
			});
	}

	/**
	 * {@inheritDoc}
	 */
	public UserDetails getUserDetailsByLogonName(final String logonName)
	{
		Assert.argumentNotNull("logonName", logonName);

		return getUserDetailsByFilter(userAttributeLogonName + "=" + LDAPUtils.ldapEscape(logonName));
	}

	/**
	 * {@inheritDoc}
	 */
	public UserDetails getUserDetailsByUserId(final String userId)
	{
		Assert.argumentNotNull("userId", userId);

		return getUserDetailsByFilter(userAttributeUserId + "=" + LDAPUtils.ldapEscape(userId));
	}

	protected NamingEnumeration<SearchResult> searchUser(final DirContext ctx, final String filter, final String[] attrs)
			throws NamingException
	{
		final SearchControls options = new SearchControls();
		options.setSearchScope(userSearchSubtree ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);
		options.setReturningAttributes(attrs);

		return ctx.search(userSearchBase, //
				"(&(" + filter + ")(" + userSearchFilter + "))", //
				options);
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

	/**
	 * @param userAttributeDisplayName the userAttributeDisplayName to set
	 */
	@Required
	public void setUserAttributeDisplayName(final String userAttributeDisplayName)
	{
		Assert.argumentNotNull("userAttributeDisplayName", userAttributeDisplayName);

		this.userAttributeDisplayName = userAttributeDisplayName;
	}

	/**
	 * @param userAttributeEMailAdress the userAttributeEMailAdress to set
	 */
	@Required
	public void setUserAttributeEMailAdress(final String userAttributeEMailAdress)
	{
		Assert.argumentNotNull("userAttributeEMailAdress", userAttributeEMailAdress);

		this.userAttributeEMailAdress = userAttributeEMailAdress;
	}

	/**
	 * @param userAttributeLogonName the userAttributeLogonName to set
	 */
	@Required
	public void setUserAttributeLogonName(final String userAttributeLogonName)
	{
		Assert.argumentNotNull("userAttributeLogonName", userAttributeLogonName);

		this.userAttributeLogonName = userAttributeLogonName;
	}

	/**
	 * @param userAttributeUserId the userAttributeUserId to set
	 */
	@Required
	public void setUserAttributeUserId(final String userAttributeUserId)
	{
		Assert.argumentNotNull("userAttributeUserId", userAttributeUserId);

		this.userAttributeUserId = userAttributeUserId;
	}

	/**
	 * @param userSearchBase the userSearchBase to set
	 */
	@Required
	public void setUserSearchBase(final String userSearchBase)
	{
		Assert.argumentNotNull("userSearchBase", userSearchBase);

		this.userSearchBase = userSearchBase;
	}

	/**
	 * @param userSearchFilter the userSearchFilter to set
	 */
	@Required
	public void setUserSearchFilter(final String userSearchFilter)
	{
		Assert.argumentNotNull("userSearchFilter", userSearchFilter);

		this.userSearchFilter = userSearchFilter;
	}

	/**
	 * @param userSearchSubtree the userSearchSubtree to set
	 */
	public void setUserSearchSubtree(final boolean userSearchSubtree)
	{
		this.userSearchSubtree = userSearchSubtree;
	}
}
