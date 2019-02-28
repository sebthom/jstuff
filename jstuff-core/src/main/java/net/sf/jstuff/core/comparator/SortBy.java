/*********************************************************************
 * Copyright 2010-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.comparator;

import java.io.Serializable;

/**
 * Encapsulation class for setting sort key and direction.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SortBy<SortKeyType> implements Serializable {
   private static final long serialVersionUID = 1L;

   private SortDirection direction;
   private SortKeyType key;

   public SortBy() {
      super();
   }

   public SortBy(final SortKeyType key) {
      this.key = key;
      this.direction = SortDirection.ASC;
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
