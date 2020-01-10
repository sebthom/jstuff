/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.date;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public enum Month {
   JANUARY,
   FEBRUAR,
   MARCH,
   APRIL,
   MAY,
   JUNE,
   JULY,
   AUGUST,
   SEPTEMBER,
   OCTOBER,
   NOVEMBER,
   DECEMBER;

   private static final String BUNDLE_NAME = Month.class.getName();

   @Override
   public String toString() {
      try {
         return ResourceBundle.getBundle(BUNDLE_NAME).getString(name());
      } catch (final MissingResourceException e) {
         return name();
      }
   }
}
