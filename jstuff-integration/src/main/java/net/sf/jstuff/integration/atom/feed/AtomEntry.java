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
package net.sf.jstuff.integration.atom.feed;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import net.sf.jstuff.core.Identifiable;
import net.sf.jstuff.core.date.DateUtils;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * The following child elements are defined by this specification (note
 * that the presence of some of these elements is required):
 * <ol>
 * <li> atom:feed elements MUST contain one or more atom:author elements,
 * unless all of the atom:feed element's child atom:entry elements
 * contain at least one atom:author element.
 * <li> atom:feed elements MAY contain any number of atom:category elements.
 * <li> atom:feed elements MAY contain any number of atom:contributor elements.
 * <li> atom:feed elements MUST NOT contain more than one atom:generator element.
 * <li> atom:feed elements MUST NOT contain more than one atom:icon element.
 * <li> atom:feed elements MUST NOT contain more than one atom:logo element.
 * <li> atom:feed elements MUST contain exactly one atom:id element.
 * <li> atom:feed elements SHOULD contain one atom:link element with a rel
 * attribute value of "self".  This is the preferred URI for
 * retrieving Atom Feed Documents representing this Atom feed.
 * <li> atom:feed elements MUST NOT contain more than one atom:link
 * element with a rel attribute value of "alternate" that has the
 * same combination of type and hreflang attribute values.
 * <li> atom:feed elements MAY contain additional atom:link elements
 * beyond those described above.
 * <li> atom:feed elements MUST NOT contain more than one atom:rights element.
 * <li> atom:feed elements MUST NOT contain more than one atom:subtitle element.
 * <li> atom:feed elements MUST contain exactly one atom:title element.
 * <li> atom:feed elements MUST contain exactly one atom:updated element.
 * </ol>
 * If multiple atom:entry elements with the same atom:id value appear in
 * an Atom Feed Document, they represent the same entry.  Their
 * atom:updated timestamps SHOULD be different.  If an Atom Feed
 * Document contains multiple entries with the same atom:id, Atom
 * Processors MAY choose to display all of them or some subset of them.
 * One typical behavior would be to display only the entry with the
 * latest atom:updated timestamp.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomEntry implements Identifiable<String>, Serializable
{
	private static final long serialVersionUID = 1L;

	private String title;
	private AtomText summary;
	private AtomText content;

	@XStreamImplicit(itemFieldName = "category")
	private Collection<AtomCategory> categories;

	private AtomLink link;
	private String id;
	private String published;
	private String updated;
	private AtomPerson author;

	/**
	 * @return the author
	 */
	public AtomPerson getAuthor()
	{
		return author;
	}

	/**
	 * @return the categories
	 */
	public Collection<AtomCategory> getCategories()
	{
		return categories;
	}

	/**
	 * @return the content
	 */
	public AtomText getContent()
	{
		return content;
	}

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @return the link
	 */
	public AtomLink getLink()
	{
		return link;
	}

	/**
	 * @return the published
	 * @throws ParseException
	 */
	public Date getPublished() throws ParseException
	{
		return DateUtils.fromRFC3399(published);
	}

	/**
	 * @return the summary
	 */
	public AtomText getSummary()
	{
		return summary;
	}

	/**
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @return the updated
	 * @throws ParseException
	 */
	public Date getUpdated() throws ParseException
	{
		return DateUtils.fromRFC3399(updated);
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(final AtomPerson author)
	{
		this.author = author;
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(final AtomCategory... categories)
	{
		this.categories = Arrays.asList(categories);
	}

	/**
	 * @param categories the categories to set
	 */
	public void setCategories(final Collection<AtomCategory> categories)
	{
		this.categories = categories;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(final AtomText content)
	{
		this.content = content;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final String id)
	{
		this.id = id;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(final AtomLink link)
	{
		this.link = link;
	}

	/**
	 * @param published the published to set
	 */
	public void setPublished(final Date published)
	{
		this.published = DateUtils.toRFC3399_UTC(published);
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(final AtomText summary)
	{
		this.summary = summary;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(final String title)
	{
		this.title = title;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(final Date updated)
	{
		this.updated = DateUtils.toRFC3399_UTC(updated);
	}
}
