/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.feed;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import net.sf.jstuff.core.date.Dates;
import net.sf.jstuff.core.types.Identifiable;

/**
 * http://www.ietf.org/rfc/rfc4287.txt
 *
 * The following child elements are defined by this specification (note
 * that the presence of some of these elements is required):
 * <ul>
 * <li>atom:feed elements MUST contain one or more atom:author elements,
 * unless all of the atom:feed element's child atom:entry elements
 * contain at least one atom:author element.
 * <li>atom:feed elements MAY contain any number of atom:category elements.
 * <li>atom:feed elements MAY contain any number of atom:contributor elements.
 * <li>atom:feed elements MUST NOT contain more than one atom:generator element.
 * <li>atom:feed elements MUST NOT contain more than one atom:icon element.
 * <li>atom:feed elements MUST NOT contain more than one atom:logo element.
 * <li>atom:feed elements MUST contain exactly one atom:id element.
 * <li>atom:feed elements SHOULD contain one atom:link element with a rel
 * attribute value of "self". This is the preferred URI for
 * retrieving Atom Feed Documents representing this Atom feed.
 * <li>atom:feed elements MUST NOT contain more than one atom:link
 * element with a rel attribute value of "alternate" that has the
 * same combination of type and hreflang attribute values.
 * <li>atom:feed elements MAY contain additional atom:link elements
 * beyond those described above.
 * <li>atom:feed elements MUST NOT contain more than one atom:rights element.
 * <li>atom:feed elements MUST NOT contain more than one atom:subtitle element.
 * <li>atom:feed elements MUST contain exactly one atom:title element.
 * <li>atom:feed elements MUST contain exactly one atom:updated element.
 * </ul>
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@XStreamAlias("feed")
public class AtomFeed extends Identifiable.Default<String> {
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

   public AtomFeed() {
      super();
   }

   public AtomFeed(final String id) {
      setId(id);
   }

   public Collection<AtomPerson> getAuthors() {
      return authors;
   }

   public Collection<AtomEntry> getEntries() {
      return entries;
   }

   public Collection<AtomLink> getLinks() {
      return links;
   }

   public String getLogo() {
      return logo;
   }

   public String getSubtitle() {
      return subtitle;
   }

   public String getTitle() {
      return title;
   }

   public Date getUpdated() throws ParseException {
      return Dates.fromRFC3399(updated);
   }

   public String getXmlns() {
      return xmlns;
   }

   public void setAuthors(final AtomPerson... authors) {
      this.authors = Arrays.asList(authors);
   }

   public void setAuthors(final Collection<AtomPerson> authors) {
      this.authors = authors;
   }

   public void setEntries(final AtomEntry... entries) {
      this.entries = Arrays.asList(entries);
   }

   public void setEntries(final Collection<AtomEntry> entries) {
      this.entries = entries;
   }

   public void setLinks(final AtomLink... links) {
      this.links = Arrays.asList(links);
   }

   public void setLinks(final Collection<AtomLink> links) {
      this.links = links;
   }

   public void setLogo(final String logo) {
      this.logo = logo;
   }

   public void setSubtitle(final String subtitle) {
      this.subtitle = subtitle;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setUpdated(final Date updated) {
      this.updated = Dates.toRFC3399_UTC(updated);
   }

   public void setXmlns(final String xmlns) {
      this.xmlns = xmlns;
   }
}
