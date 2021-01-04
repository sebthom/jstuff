/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.auth;

import net.sf.jstuff.integration.userregistry.UserDetails;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface AuthListener {
   void afterLogin(Authentication authentication);

   void afterLogout(UserDetails userDetails);
}
