/*********************************************************************
 * Copyright 2010-2021 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.integration.userregistry;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultUserDetails implements UserDetails {
   private static final long serialVersionUID = 1L;

   private final String displayName;
   private final String distingueshedName;
   private final String eMailAddress;
   private final String logonName;
   private final String userId;

   public DefaultUserDetails(final String userId, final String displayName, final String logonName, final String distingueshedName, final String mailAddress) {
      this.userId = userId;
      this.displayName = displayName;
      this.logonName = logonName;
      this.distingueshedName = distingueshedName;
      eMailAddress = mailAddress;
   }

   @Override
   public String getDisplayName() {
      return displayName;
   }

   @Override
   public String getDistingueshedName() {
      return distingueshedName;
   }

   @Override
   public String getEMailAddress() {
      return eMailAddress;
   }

   @Override
   public String getLogonName() {
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
