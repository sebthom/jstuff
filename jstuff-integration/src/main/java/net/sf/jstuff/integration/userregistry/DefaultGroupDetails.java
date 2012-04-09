/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultGroupDetails implements GroupDetails
{
	private static final long serialVersionUID = 1L;

	private String displayName;
	private String distingueshedName;
	private String groupId;
	private String[] memberDNs;

	/**
	 * {@inheritDoc}
	 */
	public String getDisplayName()
	{
		return displayName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDistingueshedName()
	{
		return distingueshedName;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getGroupId()
	{
		return groupId;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getMemberDNs()
	{
		return memberDNs.clone();
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(final String displayName)
	{
		this.displayName = displayName;
	}

	/**
	 * @param distingueshedName the distingueshedName to set
	 */
	public void setDistingueshedName(final String distingueshedName)
	{
		this.distingueshedName = distingueshedName;
	}

	/**
	 * @param groupId the groupId to set
	 */
	public void setGroupId(final String groupId)
	{
		this.groupId = groupId;
	}

	/**
	 * @param memberDNs the memberDNs to set
	 */
	public void setMemberDNs(final String[] memberDNs)
	{
		this.memberDNs = memberDNs.clone();
	}

	@Override
	public String toString()
	{
		return ToStringBuilder.reflectionToString(this);
	}
}
