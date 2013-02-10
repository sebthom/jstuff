/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
public interface GuestBookService
{
	@REST_POST("entries")
	int addEntry(final AddGuestBookEntryCommand entry) throws PermissionDeniedException;

	@REST_HEAD(value = "entries", fallback = "entries/exists")
	boolean existsEntry(final int entryId);

	@REST_GET("entries/of_author")
	PagedListWithSortBy<GuestBookEntry, String> getEntriesOfAuthor(final String createdBy, final int start, final int max,
			final SortBy<String>... sortBy);

	@REST_HEAD(value = "entries/of_author", fallback = "entries/of_author_count")
	int getEntriesOfAuthorCount(final String createdBy);

	@REST_GET("entries")
	GuestBookEntry getEntry(final int entryId) throws PermissionDeniedException;

	/**
	 * @sortBy supported fields: "createdBy", "createdOn", "lastModifiedBy", "lastModifiedOn", "responsesCount"
	 */
	@REST_GET("entries")
	PagedListWithSortBy<GuestBookEntry, String> getEntries(final int start, final int max, final SortBy<String>... sortBy);

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
	PagedListWithSortBy<GuestBookEntry, String> getResponses(final int entryId, final int start, final int max,
			final SortBy<String>... sortBy) throws PermissionDeniedException;

	@REST_HEAD(value = "responses", fallback = "responses_count")
	int getResponsesCount(final int entryId) throws PermissionDeniedException;

	@REST_GET("version")
	String getVersion();

	@REST_POST("rating")
	void rateEntry(final int entryId, final boolean isGoodEntry) throws PermissionDeniedException;

	@REST_DELETE(value = "entries", fallback = "entries/deletion")
	void removeEntry(final int entryId) throws PermissionDeniedException;

	@REST_DELETE(value = "entries/of_author", fallback = "entries/of_author_deletion")
	int removeEntriesOfAuthor(final String createdBy) throws PermissionDeniedException;

	@REST_PUT(value = "entries", fallback = "entries/update")
	void updateEntry(final int entryId, final UpdateGuestBookEntryCommand entry) throws PermissionDeniedException;
}
