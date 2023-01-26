/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface GroupDetailsService {
   @Nullable
   GroupDetails getGroupDetailsByGroupDN(String groupDN);

   Set<String> getGroupIdsByUserDN(String userDN);
}
