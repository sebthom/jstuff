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

import com.thoughtworks.xstream.annotations.XStreamImplicit;

import net.sf.jstuff.core.date.Dates;
import net.sf.jstuff.core.types.Identifiable;

/**
 * The following child elements are defined by this specification (note
 * that the presence of some of these elements is required):
 * <ol>
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
 * </ol>
 * If multiple atom:entry elements with the same atom:id value appear in
 * an Atom Feed Document, they represent the same entry. Their
 * atom:updated timestamps SHOULD be different. If an Atom Feed
 * Document contains multiple entries with the same atom:id, Atom
 * Processors MAY choose to display all of them or some subset of them.
 * One typical behavior would be to display only the entry with the
 * latest atom:updated timestamp.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomEntry extends Identifiable.Default<String> {
   private static final long serialVersionUID = 1L;

   private String title;
   private AtomText summary;
   private AtomText content;

   @XStreamImplicit(itemFieldName = "category")
   private Collection<AtomCategory> categories;

   private AtomLink link;
   private String published;
   private String updated;
   private AtomPerson author;

   public AtomPerson getAuthor() {
      return author;
   }

   public Collection<AtomCategory> getCategories() {
      return categories;
   }

   public AtomText getContent() {
      return content;
   }

   public AtomLink getLink() {
      return link;
   }

   public Date getPublished() throws ParseException {
      return Dates.fromRFC3399(published);
   }

   public AtomText getSummary() {
      return summary;
   }

   public String getTitle() {
      return title;
   }

   public Date getUpdated() throws ParseException {
      return Dates.fromRFC3399(updated);
   }

   public void setAuthor(final AtomPerson author) {
      this.author = author;
   }

   public void setCategories(final AtomCategory... categories) {
      this.categories = Arrays.asList(categories);
   }

   public void setCategories(final Collection<AtomCategory> categories) {
      this.categories = categories;
   }

   public void setContent(final AtomText content) {
      this.content = content;
   }

   public void setLink(final AtomLink link) {
      this.link = link;
   }

   public void setPublished(final Date published) {
      this.published = Dates.toRFC3399_UTC(published);
   }

   public void setSummary(final AtomText summary) {
      this.summary = summary;
   }

   public void setTitle(final String title) {
      this.title = title;
   }

   public void setUpdated(final Date updated) {
      this.updated = Dates.toRFC3399_UTC(updated);
   }
}
