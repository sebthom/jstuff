/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface UserDetailsService {
   @Nullable
   UserDetails getUserDetailsByLogonName(String logonName);

   @Nullable
   UserDetails getUserDetailsByUserId(String userId);
}
