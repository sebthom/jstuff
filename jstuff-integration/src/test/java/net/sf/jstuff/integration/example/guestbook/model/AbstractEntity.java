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
package net.sf.jstuff.integration.example.guestbook.model;

import java.util.Date;

import net.sf.jstuff.core.types.Identifiable;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class AbstractEntity extends Identifiable.Default<Integer> {
    private static final long serialVersionUID = 1L;

    private final Date createdOn;
    private Date lastModifiedOn;
    private final String createdBy;
    private String lastModifiedBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public AbstractEntity(final String createdBy) {
        Args.notNull("createdBy", createdBy);
        this.createdBy = createdBy;
        lastModifiedBy = createdBy;
        createdOn = new Date();
        lastModifiedOn = createdOn;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Date getLastModifiedOn() {
        return lastModifiedOn;
    }

    public void setLastModifiedOn(final Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public Date getCreatedOn() {
        return createdOn;
    }
}
