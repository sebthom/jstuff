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

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import net.sf.jstuff.core.Identifiable;
import net.sf.jstuff.core.date.DateUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * http://www.ietf.org/rfc/rfc4287.txt
 *
 * The following child elements are defined by this specification (note
 that the presence of some of these elements is required):
 * <ul>
 * <li>atom:feed elements MUST contain one or more atom:author elements,
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
 * </ul>
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@XStreamAlias("feed")
public class AtomFeed extends Identifiable.Default<String>
{
	private static final long serialVersionUID = 1L;

	@XStreamAsAttribute
	private String xmlns = "http://www.w3.org/2005/Atom";

	private String title;
	private String logo;

	@XStreamImplicit(itemFieldName = "author")
	private Collection<AtomPerson> authors;

	private String subtitle;

	@XStreamImplicit(itemFieldName = "link")
	private Collection<AtomLink> links;

	private String updated;

	@XStreamImplicit(itemFieldName = "entry")
	private Collection<AtomEntry> entries;

	public AtomFeed()
	{
		super();
	}

	public AtomFeed(final String id)
	{
		setId(id);
	}

	/**
	 * @return the authors
	 */
	public Collection<AtomPerson> getAuthors()
	{
		return authors;
	}

	/**
	 * @return the entries
	 */
	public Collection<AtomEntry> getEntries()
	{
		return entries;
	}

	/**
	 * @return the links
	 */
	public Collection<AtomLink> getLinks()
	{
		return links;
	}

	/**
	 * @return the logo
	 */
	public String getLogo()
	{
		return logo;
	}

	/**
	 * @return the subtitle
	 */
	public String getSubtitle()
	{
		return subtitle;
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
	 * @return the xmlns
	 */
	public String getXmlns()
	{
		return xmlns;
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(final AtomPerson... authors)
	{
		this.authors = Arrays.asList(authors);
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(final Collection<AtomPerson> authors)
	{
		this.authors = authors;
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(final AtomEntry... entries)
	{
		this.entries = Arrays.asList(entries);
	}

	/**
	 * @param entries the entries to set
	 */
	public void setEntries(final Collection<AtomEntry> entries)
	{
		this.entries = entries;
	}

	/**
	 * @param links the links to set
	 */
	public void setLinks(final AtomLink... links)
	{
		this.links = Arrays.asList(links);
	}

	/**
	 * @param links the links to set
	 */
	public void setLinks(final Collection<AtomLink> links)
	{
		this.links = links;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(final String logo)
	{
		this.logo = logo;
	}

	/**
	 * @param subtitle the subtitle to set
	 */
	public void setSubtitle(final String subtitle)
	{
		this.subtitle = subtitle;
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

	/**
	 * @param xmlns the xmlns to set
	 */
	public void setXmlns(final String xmlns)
	{
		this.xmlns = xmlns;
	}
}
