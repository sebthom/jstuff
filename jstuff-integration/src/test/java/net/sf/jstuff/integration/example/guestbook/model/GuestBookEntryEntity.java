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

import java.util.ArrayList;
import java.util.List;

import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class GuestBookEntryEntity extends AbstractEntity {
    private static final long serialVersionUID = 1L;

    private final GuestBookEntryEntity parent;
    private String message;
    private List<GuestBookEntryRatingEntity> ratings = new ArrayList<GuestBookEntryRatingEntity>();
    private List<GuestBookEntryEntity> responses = new ArrayList<GuestBookEntryEntity>();

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
