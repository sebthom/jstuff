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
package net.sf.jstuff.integration.auth;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
final class AuthenticationHolder {
    private static final ThreadLocal<Authentication> threadLocal = new ThreadLocal<Authentication>();

    public static Authentication getAuthentication() {
        final Authentication auth = threadLocal.get();
        if (auth == null)
            return DefaultAuthentication.UNBOUND;
        return auth;
    }

    public static void setAuthentication(final Authentication authentication) {
        threadLocal.set(authentication);
    }

    private AuthenticationHolder() {
        super();
    }
}
