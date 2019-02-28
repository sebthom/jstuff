/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.example.guestbook.service;

import java.io.Serializable;
import java.util.Date;

import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.example.guestbook.model.GuestBookEntryEntity;
import net.sf.jstuff.integration.example.guestbook.model.GuestBookEntryRatingEntity;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GuestBookEntry implements Serializable {
   private static final long serialVersionUID = 1L;

   public static GuestBookEntry of(final GuestBookEntryEntity entity) {
      Args.notNull("entity", entity);

      final GuestBookEntry summary = new GuestBookEntry();
      summary.id = entity.getId();
      summary.message = entity.getMessage();
      summary.createdBy = entity.getCreatedBy();
      summary.createdOn = entity.getCreatedOn();
      summary.lastModifiedBy = entity.getLastModifiedBy();
      summary.lastModifiedOn = entity.getLastModifiedOn();

      for (final GuestBookEntryRatingEntity r : entity.getRatings())
         if (r.isGoodEntry()) {
            summary.goodRatingsCount++;
         } else {
            summary.badRatingsCount++;
         }
      summary.responsesCount = entity.getResponses().size();
      return summary;
   }

   public int id;
   public String createdBy;
   public Date createdOn;
   public String lastModifiedBy;
   public Date lastModifiedOn;
   public String message;
   public int goodRatingsCount;
   public int badRatingsCount;
   public int responsesCount;
}
