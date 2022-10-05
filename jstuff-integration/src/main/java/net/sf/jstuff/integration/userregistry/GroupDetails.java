/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import java.io.Serializable;
import java.util.SortedSet;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface GroupDetails extends Serializable {
   String getDisplayName();

   String getDistinguishedName();

   String getGroupId();

   SortedSet<String> getMemberDNs();
}
