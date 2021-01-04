/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.date;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
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
