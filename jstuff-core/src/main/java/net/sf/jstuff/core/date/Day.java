/*
 * SPDX-FileCopyrightText: © Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.date;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum Day {
   MONDAY,
   TUESDAY,
   WEDNESDAY,
   THURSDAY,
   FRIDAY,
   SATURDAY,
   SUNDAY;

   private static final String BUNDLE_NAME = Day.class.getName();

   @Override
   public String toString() {
      try {
         return ResourceBundle.getBundle(BUNDLE_NAME).getString(name());
      } catch (final MissingResourceException e) {
         return name();
      }
   }
}
