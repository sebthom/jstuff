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
package net.sf.jstuff.integration.atom.feed;

import java.util.Date;

public class SimpleEntry<T>
{
	private String authorDisplayName;
	private String authorEMailAddress;
	private String authorURL;
	private String content;
	private Date dateCreated;
	private Date dateLastModified;
	private T id;
	private String subject;

	private String tags;

	/**
	 * @return the authorDisplayName
	 */
	public String getAuthorDisplayName()
	{
		return authorDisplayName;
	}

	/**
	 * @return the authorEMailAddress
	 */
	public String getAuthorEMailAddress()
	{
		return authorEMailAddress;
	}

	/**
	 * @return the authorURL
	 */
	public String getAuthorURL()
	{
		return authorURL;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @return the dateCreated
	 */
	public Date getDateCreated()
	{
		return dateCreated;
	}

	/**
	 * @return the dateLastModified
	 */
	public Date getDateLastModified()
	{
		return dateLastModified;
	}

	/**
	 * @return the id
	 */
	public T getId()
	{
		return id;
	}

	/**
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}

	/**
	 * @return the tags
	 */
	public String getTags()
	{
		return tags;
	}

	/**
	 * @param authorDisplayName the authorDisplayName to set
	 */
	public void setAuthorDisplayName(final String authorDisplayName)
	{
		this.authorDisplayName = authorDisplayName;
	}

	/**
	 * @param authorEMailAddress the authorEMailAddress to set
	 */
	public void setAuthorEMailAddress(final String authorEMailAddress)
	{
		this.authorEMailAddress = authorEMailAddress;
	}

	/**
	 * @param authorURL the authorURL to set
	 */
	public void setAuthorURL(final String authorURL)
	{
		this.authorURL = authorURL;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(final String content)
	{
		this.content = content;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 */
	public void setDateCreated(final Date dateCreated)
	{
		this.dateCreated = dateCreated;
	}

	/**
	 * @param dateLastModified the dateLastModified to set
	 */
	public void setDateLastModified(final Date dateLastModified)
	{
		this.dateLastModified = dateLastModified;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final T id)
	{
		this.id = id;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(final String subject)
	{
		this.subject = subject;
	}

	/**
	 * @param tags the tags to set
	 */
	public void setTags(final String tags)
	{
		this.tags = tags;
	}

}
