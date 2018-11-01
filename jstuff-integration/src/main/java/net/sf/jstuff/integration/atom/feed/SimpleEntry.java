/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.atom.feed;

import java.util.Date;

import net.sf.jstuff.core.types.Identifiable;

public class SimpleEntry<T> extends Identifiable.Default<T> {
   private static final long serialVersionUID = 1L;

   private String authorDisplayName;
   private String authorEMailAddress;
   private String authorURL;
   private String content;
   private Date dateCreated;
   private Date dateLastModified;
   private String subject;

   private String tags;

   /**
    * @return the authorDisplayName
    */
   public String getAuthorDisplayName() {
      return authorDisplayName;
   }

   /**
    * @return the authorEMailAddress
    */
   public String getAuthorEMailAddress() {
      return authorEMailAddress;
   }

   /**
    * @return the authorURL
    */
   public String getAuthorURL() {
      return authorURL;
   }

   /**
    * @return the content
    */
   public String getContent() {
      return content;
   }

   /**
    * @return the dateCreated
    */
   public Date getDateCreated() {
      return dateCreated;
   }

   /**
    * @return the dateLastModified
    */
   public Date getDateLastModified() {
      return dateLastModified;
   }

   /**
    * @return the subject
    */
   public String getSubject() {
      return subject;
   }

   /**
    * @return the tags
    */
   public String getTags() {
      return tags;
   }

   /**
    * @param authorDisplayName the authorDisplayName to set
    */
   public void setAuthorDisplayName(final String authorDisplayName) {
      this.authorDisplayName = authorDisplayName;
   }

   /**
    * @param authorEMailAddress the authorEMailAddress to set
    */
   public void setAuthorEMailAddress(final String authorEMailAddress) {
      this.authorEMailAddress = authorEMailAddress;
   }

   /**
    * @param authorURL the authorURL to set
    */
   public void setAuthorURL(final String authorURL) {
      this.authorURL = authorURL;
   }

   /**
    * @param content the content to set
    */
   public void setContent(final String content) {
      this.content = content;
   }

   /**
    * @param dateCreated the dateCreated to set
    */
   public void setDateCreated(final Date dateCreated) {
      this.dateCreated = dateCreated;
   }

   /**
    * @param dateLastModified the dateLastModified to set
    */
   public void setDateLastModified(final Date dateLastModified) {
      this.dateLastModified = dateLastModified;
   }

   /**
    * @param subject the subject to set
    */
   public void setSubject(final String subject) {
      this.subject = subject;
   }

   /**
    * @param tags the tags to set
    */
   public void setTags(final String tags) {
      this.tags = tags;
   }

}
