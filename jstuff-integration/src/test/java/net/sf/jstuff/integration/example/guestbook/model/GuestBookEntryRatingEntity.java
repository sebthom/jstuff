/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.example.guestbook.model;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class GuestBookEntryRatingEntity extends AbstractEntity {
   private static final long serialVersionUID = 1L;

   private final GuestBookEntryEntity entry;
   private boolean isGoodEntry;

   public GuestBookEntryRatingEntity(final String createdBy, final GuestBookEntryEntity entry) {
      super(createdBy);
      Args.notNull("entry", entry);
      this.entry = entry;
   }

   public boolean isGoodEntry() {
      return isGoodEntry;
   }

   public void setGoodEntry(final boolean isGoodEntry) {
      this.isGoodEntry = isGoodEntry;
   }

   public GuestBookEntryEntity getEntry() {
      return entry;
   }
}
