/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.integration.atom.blog;

import java.io.Serializable;

/**
 * A <code>Blog</code> represents a blog where entries can be posted.
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomBlog implements Serializable
{
	private static final long serialVersionUID = 1L;

	private String title;
	private String entriesUrl;

	public AtomBlog()
	{
		super();
	}

	public AtomBlog(final String title, final String entriesUrl)
	{
		super();
		this.title = title;
		this.entriesUrl = entriesUrl;
	}

	public String getEntriesUrl()
	{
		return entriesUrl;
	}

	public String getTitle()
	{
		return title;
	}

	public void setEntriesUrl(final String entriesUrl)
	{
		this.entriesUrl = entriesUrl;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}
}
