/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.localization;

import java.text.DateFormat;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
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
