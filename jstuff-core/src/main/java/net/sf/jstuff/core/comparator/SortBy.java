/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;

/**
 * Encapsulation class for setting sort key and direction.
 *
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortBy<SortKeyType> implements Serializable {
   private static final long serialVersionUID = 1L;

   private SortDirection direction = SortDirection.ASC;

   private SortKeyType key;

   public SortBy(final SortKeyType key) {
      this.key = key;
   }

   public SortBy(final SortKeyType key, final SortDirection direction) {
      this.key = key;
      this.direction = direction;
   }

   public SortDirection getDirection() {
      return direction;
   }

   public void setDirection(final SortDirection direction) {
      this.direction = direction;
   }

   public SortKeyType getKey() {
      return key;
   }

   public void setKey(final SortKeyType key) {
      this.key = key;
   }

   @Override
   public String toString() {
      return key + " " + direction;
   }
}
