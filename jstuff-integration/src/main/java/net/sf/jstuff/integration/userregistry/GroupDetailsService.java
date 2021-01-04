/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import java.util.Set;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface GroupDetailsService {
   GroupDetails getGroupDetailsByGroupDN(String groupDN);

   Set<String> getGroupIdsByUserDN(String userDN);
}
