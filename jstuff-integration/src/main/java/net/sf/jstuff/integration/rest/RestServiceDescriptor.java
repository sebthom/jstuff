/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
package net.sf.jstuff.integration.rest;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class RestServiceDescriptor implements Serializable {
    private static final long serialVersionUID = 1L;

    private Collection<RestResourceAction> actions;

    public Collection<RestResourceAction> getActions() {
        return actions;
    }

    public void setActions(final Collection<RestResourceAction> actions) {
        this.actions = actions;
    }
}