/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.jstuff.core.comparator.SortBy;
import net.sf.jstuff.core.comparator.SortDirection;
import net.sf.jstuff.integration.atom.feed.AbstractAtomFeedExporter;
import net.sf.jstuff.integration.atom.feed.SimpleEntry;

import org.apache.commons.lang3.StringUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GuestBookAtomFeedExporter extends AbstractAtomFeedExporter {
    private GuestBookService guestBookService;
    private int maxEntries = 20;

    @SuppressWarnings("unchecked")
    @Override
    protected List<SimpleEntry<?>> getSimpleEntries(final HttpServletRequest request) {
        final List<SimpleEntry<?>> result = newArrayList();
        for (final GuestBookEntry entry : guestBookService.getEntries(1, maxEntries, new SortBy<String>("lastModifiedOn", SortDirection.DESC))) {
            final SimpleEntry<Integer> se = new SimpleEntry<Integer>();
            se.setAuthorDisplayName(entry.createdBy);
            se.setAuthorEMailAddress("n/a");
            se.setAuthorURL("n/a");
            se.setContent(entry.message);
            se.setId(entry.id);
            se.setSubject(StringUtils.abbreviate(entry.message, 40));
            se.setDateCreated(entry.createdOn);
            se.setDateLastModified(entry.lastModifiedOn);
            result.add(se);
        }
        return result;
    }

    public void setGuestBookService(final GuestBookService guestBookService) {
        this.guestBookService = guestBookService;
    }

    public void setMaxEntries(final int maxEntries) {
        this.maxEntries = maxEntries;
    }

}
