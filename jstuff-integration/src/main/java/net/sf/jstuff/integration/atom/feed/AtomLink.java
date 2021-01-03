/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.feed;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomLink {

   /**
    * The value "alternate" signifies that the IRI in the value of the
    * href attribute identifies an alternate version of the resource
    * described by the containing element.
    */
   public static final String REL_ALTERNATE = "alternate";

   /**
    * The value "enclosure" signifies that the IRI in the value of the
    * href attribute identifies a related resource that is potentially
    * large in size and might require special handling. For atom:link
    * elements with rel="enclosure", the length attribute SHOULD be
    * provided.
    */
   public static final String REL_ENCLOSURE = "enclosure";

   /**
    * The value "related" signifies that the IRI in the value of the
    * href attribute identifies a resource related to the resource
    * described by the containing element. For example, the feed for a
    * site that discusses the performance of the search engine at
    * "http://search.example.com" might contain, as a child of atom:feed:
    *
    * <link rel="related" href="http://search.example.com/"/>
    *
    * An identical link might appear as a child of any atom:entry whose
    * content contains a discussion of that same search engine.
    */
   public static final String REL_RELATED = "related";

   /**
    * The value "self" signifies that the IRI in the value of the href
    * attribute identifies a resource equivalent to the containing
    * element.
    */
   public static final String REL_SELF = "self";

   /**
    * The value "via" signifies that the IRI in the value of the href
    * attribute identifies a resource that is the source of the
    * information provided in the containing element.The value "via" signifies that
    * the IRI in the value of the href
    * attribute identifies a resource that is the source of the
    * information provided in the containing element.
    */
   public static final String REL_VIA = "via";

   public static final String TYPE_APPLICATION_ATOM_XML = "application/atom+xml";

   public static final String TYPE_TEXT_HTML = "text/html";

   @XStreamAsAttribute
   private String href;

   @XStreamAsAttribute
   private String rel;

   @XStreamAsAttribute
   private String type;

   public AtomLink() {
   }

   public AtomLink(final String href) {
      this.href = href;
   }

   public AtomLink(final String href, final String rel, final String type) {
      this.href = href;
      this.rel = rel;
      this.type = type;
   }

   public String getHref() {
      return href;
   }

   public String getRel() {
      return rel;
   }

   public String getType() {
      return type;
   }

   public void setHref(final String href) {
      this.href = href;
   }

   public void setRel(final String rel) {
      this.rel = rel;
   }

   public void setType(final String type) {
      this.type = type;
   }

}
