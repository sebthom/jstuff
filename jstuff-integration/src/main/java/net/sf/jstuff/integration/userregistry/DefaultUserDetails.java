/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultUserDetails implements UserDetails {
   private static final long serialVersionUID = 1L;

   private final String displayName;
   private final @Nullable String distinguishedName;
   private final @Nullable String eMailAddress;
   private final @Nullable String logonName;
   private final String userId;

   public DefaultUserDetails(final String userId, final String displayName, final @Nullable String logonName,
      final @Nullable String distinguishedName, final @Nullable String mailAddress) {
      this.userId = userId;
      this.displayName = displayName;
      this.logonName = logonName;
      this.distinguishedName = distinguishedName;
      eMailAddress = mailAddress;
   }

   @Override
   public String getDisplayName() {
      return displayName;
   }

   @Override
   public @Nullable String getDistinguishedName() {
      return distinguishedName;
   }

   @Override
   public @Nullable String getEMailAddress() {
      return eMailAddress;
   }

   @Override
   public @Nullable String getLogonName() {
      return logonName;
   }

   @Override
   public String getUserId() {
      return userId;
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }
}
