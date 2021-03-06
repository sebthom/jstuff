/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultGroupDetails implements GroupDetails {
   private static final long serialVersionUID = 1L;

   private String displayName;
   private String distingueshedName;
   private String groupId;
   private String[] memberDNs;

   @Override
   public String getDisplayName() {
      return displayName;
   }

   @Override
   public String getDistingueshedName() {
      return distingueshedName;
   }

   @Override
   public String getGroupId() {
      return groupId;
   }

   @Override
   public String[] getMemberDNs() {
      return memberDNs.clone();
   }

   public void setDisplayName(final String displayName) {
      this.displayName = displayName;
   }

   public void setDistingueshedName(final String distingueshedName) {
      this.distingueshedName = distingueshedName;
   }

   public void setGroupId(final String groupId) {
      this.groupId = groupId;
   }

   public void setMemberDNs(final String[] memberDNs) {
      this.memberDNs = memberDNs.clone();
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }
}
