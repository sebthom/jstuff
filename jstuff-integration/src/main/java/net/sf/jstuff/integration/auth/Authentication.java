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
package net.sf.jstuff.integration.auth;

import java.io.Serializable;

import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public interface Authentication {
    String getPassword();

    Serializable getProperty(String name);

    UserDetails getUserDetails();

    void invalidate();

    boolean isAuthenticated();

    void setProperty(String name, Serializable value);
}
