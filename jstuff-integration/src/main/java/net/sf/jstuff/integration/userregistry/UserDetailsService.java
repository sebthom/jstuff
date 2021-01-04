/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface UserDetailsService {
   UserDetails getUserDetailsByLogonName(String logonName);

   UserDetails getUserDetailsByUserId(String userId);
}
