/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.integration.example.guestbook.service;

import static net.sf.jstuff.core.collection.CollectionUtils.*;
import static net.sf.jstuff.core.functional.Accepts.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jstuff.core.collection.Maps;
import net.sf.jstuff.core.collection.PagedListWithSortBy;
import net.sf.jstuff.core.comparator.SortBy;
import net.sf.jstuff.core.comparator.SortByPropertyComparator;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.integration.auth.PermissionDeniedException;
import net.sf.jstuff.integration.example.guestbook.model.GuestBookEntryEntity;
import net.sf.jstuff.integration.example.guestbook.model.GuestBookEntryRatingEntity;
import net.sf.jstuff.integration.example.guestbook.service.command.AddGuestBookEntryCommand;
import net.sf.jstuff.integration.example.guestbook.service.command.UpdateGuestBookEntryCommand;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GuestBookServiceInMemoryImpl implements GuestBookService {
    private static List<GuestBookEntry> sortInPlace(final List<GuestBookEntry> entries, final SortBy<String>... sortBy) {
        Collections.sort(entries, new SortByPropertyComparator<GuestBookEntry>(sortBy));
        return entries;
    }

    private static List<GuestBookEntry> toEntries(final Collection<GuestBookEntryEntity> entities) {
        final List<GuestBookEntry> list = newArrayList(entities.size());
        for (final GuestBookEntryEntity e : entities) {
            list.add(GuestBookEntry.of(e));
        }
        return list;
    }

    AtomicInteger counter = new AtomicInteger(0);

    private final Map<Integer, GuestBookEntryEntity> store = Maps.newHashMap();

    public GuestBookServiceInMemoryImpl() {
        final AddGuestBookEntryCommand entry1 = new AddGuestBookEntryCommand();
        entry1.message = "Hello world!";
        addEntry(entry1);

        final AddGuestBookEntryCommand entry2 = new AddGuestBookEntryCommand();
        entry2.parentEntryId = 1;
        entry2.message = "Welcome!";
        addEntry(entry2);
    }

    @Override
    public int addEntry(final AddGuestBookEntryCommand entry) throws PermissionDeniedException {
        final GuestBookEntryEntity entity = new GuestBookEntryEntity(getCurrentUser());
        entity.setId(counter.incrementAndGet());
        entity.setMessage(entry.message);
        if (entry.parentEntryId != null) {
            final GuestBookEntryEntity parent = store.get(entry.parentEntryId);
            if (parent == null)
                throw new UnknownEntityException(GuestBookEntry.class, entry.parentEntryId);
        }
        store.put(entity.getId(), entity);
        return entity.getId();
    }

    @Override
    public boolean existsEntry(final int entryId) {
        return store.containsKey(entryId);
    }

    @SuppressWarnings("static-method")
    private String getCurrentUser() {
        return "anonymous";
    }

    @Override
    @SuppressWarnings("unchecked")
    public PagedListWithSortBy<GuestBookEntry, String> getEntries(final int start, final int max, final SortBy<String>... sortBy) {
        Args.inRange("start", start, 1, Integer.MAX_VALUE);
        Args.inRange("max", max, 1, 1024);

        return new PagedListWithSortBy<GuestBookEntry, String>(//
            GuestBookEntry.class, //
            slice(sortInPlace(toEntries(store.values())), start - 1, max), //
            start, //
            store.size()//
        );
    }

    @Override
    public int getEntriesCount() {
        return store.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public PagedListWithSortBy<GuestBookEntry, String> getEntriesOfAuthor(final String createdBy, final int start, final int max,
            final SortBy<String>... sortBy) {
        Args.notNull("createdBy", createdBy);
        Args.inRange("start", start, 1, store.size());
        Args.inRange("max", max, 1, 1024);

        return new PagedListWithSortBy<GuestBookEntry, String>(//
            GuestBookEntry.class, //
            slice(sortInPlace(toEntries(filter(store.values(), property(GuestBookEntryEntity.class, "createdBy", equalTo(createdBy))))), start - 1, max), //
            start, //
            store.size() //
        );
    }

    @Override
    public int getEntriesOfAuthorCount(final String createdBy) {
        Args.notNull("createdBy", createdBy);

        int count = 0;
        for (final GuestBookEntryEntity e : store.values())
            if (createdBy.equals(e.getCreatedBy())) {
                count++;
            }
        return count;
    }

    @Override
    public GuestBookEntry getEntry(final int entryId) throws PermissionDeniedException {
        final GuestBookEntryEntity entity = store.get(entryId);
        if (entity == null)
            return null;
        return GuestBookEntry.of(entity);
    }

    public static <T> List<T> slice(final List<T> elements, final int fromIndex, final int maxElements) {
        if (fromIndex >= elements.size())
            return Collections.emptyList();

        int toIndex = fromIndex + maxElements;
        if (toIndex >= elements.size()) {
            toIndex = elements.size();
        }
        return elements.subList(fromIndex, toIndex);
    }

    @Override
    @SuppressWarnings("unchecked")
    public PagedListWithSortBy<GuestBookEntry, String> getResponses(final int entryId, final int start, final int max, final SortBy<String>... sortBy)
            throws PermissionDeniedException {
        Args.notNull("entryId", entryId);

        final GuestBookEntryEntity entity = store.get(entryId);
        if (entity == null)
            throw new UnknownEntityException(GuestBookEntry.class, entryId);

        return new PagedListWithSortBy<GuestBookEntry, String>(//
            GuestBookEntry.class, //
            slice(sortInPlace(toEntries(entity.getResponses())), start - 1, max), //
            start, //
            store.size()//
        );
    }

    @Override
    public int getResponsesCount(final int entryId) throws PermissionDeniedException {
        final GuestBookEntryEntity entity = store.get(entryId);
        if (entity == null)
            throw new UnknownEntityException(GuestBookEntry.class, entryId);
        return entity.getResponses().size();
    }

    @Override
    public String getVersion() {
        return "0.5";
    }

    @Override
    public void rateEntry(final int entryId, final boolean isGoodEntry) throws PermissionDeniedException {
        final GuestBookEntryEntity entity = store.get(entryId);
        if (entity == null)
            throw new UnknownEntityException(GuestBookEntry.class, entryId);
        entity.getRatings().add(new GuestBookEntryRatingEntity(getCurrentUser(), entity));
    }

    private void remove(final GuestBookEntryEntity entity) {
        // remove from database
        store.remove(entity.getId());

        // remove from parent
        if (entity.getParent() != null) {
            entity.getParent().getResponses().remove(entity);
        }

        // trigger child removal
        for (final GuestBookEntryEntity response : entity.getResponses()) {
            remove(response);
        }
    }

    @Override
    public int removeEntriesOfAuthor(final String createdBy) throws PermissionDeniedException {
        Args.notNull("createdBy", createdBy);

        final Collection<GuestBookEntryEntity> entities = filter(store.values(), property(GuestBookEntryEntity.class, "createdBy", equalTo(createdBy)));
        for (final GuestBookEntryEntity e : entities) {
            remove(e);
        }
        return entities.size();
    }

    @Override
    public void removeEntry(final int entryId) throws PermissionDeniedException {
        Args.notNull("entryId", entryId);

        final GuestBookEntryEntity entity = store.get(entryId);
        if (entity == null)
            throw new UnknownEntityException(GuestBookEntry.class, entryId);

        remove(entity);
    }

    @Override
    public void updateEntry(final int entryId, final UpdateGuestBookEntryCommand entry) throws PermissionDeniedException {
        Args.notNull("entryId", entryId);
        Args.notNull("entry", entry);

        final GuestBookEntryEntity entity = store.get(entryId);
        if (entity == null)
            throw new UnknownEntityException(GuestBookEntry.class, entryId);

        entity.setMessage(entry.message);
    }
}
