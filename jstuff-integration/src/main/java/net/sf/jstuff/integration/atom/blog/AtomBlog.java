/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.blog;

import java.io.Serializable;

/**
 * A <code>Blog</code> represents a blog where entries can be posted.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class AtomBlog implements Serializable {
   private static final long serialVersionUID = 1L;

   private String title;
   private String entriesUrl;

   public AtomBlog() {
   }

   public AtomBlog(final String title, final String entriesUrl) {
      this.title = title;
      this.entriesUrl = entriesUrl;
   }

   public String getEntriesUrl() {
      return entriesUrl;
   }

   public String getTitle() {
      return title;
   }

   public void setEntriesUrl(final String entriesUrl) {
      this.entriesUrl = entriesUrl;
   }

   public void setTitle(final String title) {
      this.title = title;
   }
}
