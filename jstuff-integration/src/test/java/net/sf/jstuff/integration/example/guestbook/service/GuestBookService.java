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

import net.sf.jstuff.core.collection.PagedListWithSortBy;
import net.sf.jstuff.core.comparator.SortBy;
import net.sf.jstuff.integration.auth.PermissionDeniedException;
import net.sf.jstuff.integration.example.guestbook.service.command.AddGuestBookEntryCommand;
import net.sf.jstuff.integration.example.guestbook.service.command.UpdateGuestBookEntryCommand;
import net.sf.jstuff.integration.rest.REST_DELETE;
import net.sf.jstuff.integration.rest.REST_GET;
import net.sf.jstuff.integration.rest.REST_HEAD;
import net.sf.jstuff.integration.rest.REST_POST;
import net.sf.jstuff.integration.rest.REST_PUT;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface GuestBookService {
   @REST_POST("entries")
   int addEntry(AddGuestBookEntryCommand entry) throws PermissionDeniedException;

   @REST_HEAD(value = "entries", fallback = "entries/exists")
   boolean existsEntry(int entryId);

   @REST_GET("entries/of_author")
   PagedListWithSortBy<GuestBookEntry, String> getEntriesOfAuthor(String createdBy, int start, int max,
      @SuppressWarnings("unchecked") SortBy<String>... sortBy);

   @REST_HEAD(value = "entries/of_author", fallback = "entries/of_author_count")
   int getEntriesOfAuthorCount(String createdBy);

   @REST_GET("entries")
   GuestBookEntry getEntry(int entryId) throws PermissionDeniedException;

   /**
    * @sortBy supported fields: "createdBy", "createdOn", "lastModifiedBy", "lastModifiedOn", "responsesCount"
    */
   @REST_GET("entries")
   PagedListWithSortBy<GuestBookEntry, String> getEntries(int start, int max, @SuppressWarnings("unchecked") SortBy<String>... sortBy);

   @REST_HEAD(value = "entries", fallback = "entries/count")
   int getEntriesCount();

   /**
    * @sortBy supported fields: "subject", "dateCreated", "authorDisplayName", "responsesCount"
    *
    * @return the direct responses to a comment sorted by date either ascending or descending
    *
    * @throws PermissionDeniedException if not authorized to access the comment with the given ID
    */
   @REST_GET("responses")
   PagedListWithSortBy<GuestBookEntry, String> getResponses(int entryId, int start, int max, @SuppressWarnings("unchecked") SortBy<String>... sortBy)
      throws PermissionDeniedException;

   @REST_HEAD(value = "responses", fallback = "responses_count")
   int getResponsesCount(int entryId) throws PermissionDeniedException;

   @REST_GET("version")
   String getVersion();

   @REST_POST("rating")
   void rateEntry(int entryId, boolean isGoodEntry) throws PermissionDeniedException;

   @REST_DELETE(value = "entries", fallback = "entries/deletion")
   void removeEntry(int entryId) throws PermissionDeniedException;

   @REST_DELETE(value = "entries/of_author", fallback = "entries/of_author_deletion")
   int removeEntriesOfAuthor(String createdBy) throws PermissionDeniedException;

   @REST_PUT(value = "entries", fallback = "entries/update")
   void updateEntry(int entryId, UpdateGuestBookEntryCommand entry) throws PermissionDeniedException;
}
