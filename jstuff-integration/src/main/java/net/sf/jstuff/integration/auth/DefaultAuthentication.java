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
package net.sf.jstuff.integration.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.sf.jstuff.integration.userregistry.DefaultUserDetails;
import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
class DefaultAuthentication implements Authentication {
    public static final Authentication UNBOUND = new DefaultAuthentication(new DefaultUserDetails("anonymous", "anonymous", null, null, null), null);

    private String password;
    private final Map<String, Serializable> properties = new HashMap<String, Serializable>(2);
    private UserDetails userDetails;

    public DefaultAuthentication(final UserDetails userDetails, final String password) {
        this.userDetails = userDetails;
        this.password = password;
    }

    /**
     * @return the password
     */
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Serializable getProperty(final String name) {
        return properties.get(name);
    }

    @Override
    public UserDetails getUserDetails() {
        return userDetails;
    }

    @Override
    public void invalidate() {
        userDetails = UNBOUND.getUserDetails();
        properties.clear();
        password = null;
    }

    @Override
    public boolean isAuthenticated() {
        return userDetails != null && userDetails.getDistingueshedName() != null;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public void setProperty(final String name, final Serializable value) {
        properties.put(name, value);
    }
}
