/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2010-2018 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.example.guestbook.model;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
