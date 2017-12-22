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
package net.sf.jstuff.integration.servlet.session;

import java.util.Map;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface SessionMap extends Map<String, Object> {
    Object get(String key, Object defaultValueIfNull);

    /**
     * @return true if an underlying HTTP session exists already
     */
    boolean exists();

    /**
     * @return the session Id
     */
    Object getId();

    /**
     * invalidates the underlying HTTP session
     */
    void invalidate();
}
