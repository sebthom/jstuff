/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import java.io.Serializable;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public interface UserDetails extends Serializable {
   String getDisplayName();

   @Nullable
   String getDistinguishedName();

   @Nullable
   String getEMailAddress();

   @Nullable
   String getLogonName();

   String getUserId();
}
