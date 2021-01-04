/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import java.io.Serializable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface GroupDetails extends Serializable {
   String getDisplayName();

   String getDistingueshedName();

   String getGroupId();

   String[] getMemberDNs();
}
