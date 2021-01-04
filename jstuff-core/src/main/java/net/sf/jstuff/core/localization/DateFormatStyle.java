/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.localization;

import java.text.DateFormat;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public enum DateFormatStyle {
   FULL(DateFormat.FULL), //
   LONG(DateFormat.LONG), //
   MEDIUM(DateFormat.MEDIUM), //
   SHORT(DateFormat.SHORT);

   public final int style;

   DateFormatStyle(final int style) {
      this.style = style;
   }
}
