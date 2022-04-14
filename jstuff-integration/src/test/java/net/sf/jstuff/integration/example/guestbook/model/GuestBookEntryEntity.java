/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.example.guestbook.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class GuestBookEntryEntity extends AbstractEntity {
   private static final long serialVersionUID = 1L;

   private final GuestBookEntryEntity parent;
   private String message;
   private List<GuestBookEntryRatingEntity> ratings = new ArrayList<>();
   private List<GuestBookEntryEntity> responses = new ArrayList<>();

   public GuestBookEntryEntity(final String createdBy) {
      super(createdBy);
      parent = null;
   }

   public GuestBookEntryEntity(final String createdBy, final String message, final GuestBookEntryEntity parent) {
      super(createdBy);
      setMessage(message);
      this.parent = parent;
   }

   public List<GuestBookEntryEntity> getResponses() {
      return responses;
   }

   public void setResponses(final List<GuestBookEntryEntity> responses) {
      this.responses = responses;
   }

   public GuestBookEntryEntity getParent() {
      return parent;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(final String message) {
      Args.notEmpty("message", message);
      this.message = message.trim();
   }

   public List<GuestBookEntryRatingEntity> getRatings() {
      return ratings;
   }

   public void setRatings(final List<GuestBookEntryRatingEntity> ratings) {
      this.ratings = ratings;
   }
}
