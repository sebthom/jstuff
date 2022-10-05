/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.userregistry;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class DefaultGroupDetails implements GroupDetails {
   private static final long serialVersionUID = 1L;

   private String displayName;
   private String distinguishedName;
   private String groupId;
   private SortedSet<String> memberDNs;

   public DefaultGroupDetails(final String groupId, final String displayName, final String distinguishedName,
      final Collection<String> memberDNs) {
      this.displayName = displayName;
      this.distinguishedName = distinguishedName;
      this.groupId = groupId;
      this.memberDNs = Collections.unmodifiableSortedSet(new TreeSet<>(memberDNs));
   }

   @Override
   public String getDisplayName() {
      return displayName;
   }

   @Override
   public String getDistinguishedName() {
      return distinguishedName;
   }

   @Override
   public String getGroupId() {
      return groupId;
   }

   @Override
   public SortedSet<String> getMemberDNs() {
      return memberDNs;
   }

   @Override
   public String toString() {
      return ToStringBuilder.reflectionToString(this);
   }
}
