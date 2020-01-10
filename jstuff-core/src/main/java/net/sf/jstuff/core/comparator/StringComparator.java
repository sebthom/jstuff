/*********************************************************************
 * Copyright 2010-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringComparator implements Comparator<String>, Serializable {
   private static final long serialVersionUID = 1L;

   // collator is only serializable starting Java 6
   private transient Collator collator;
   private final Locale locale;

   public StringComparator() {
      this(Locale.getDefault());
   }

   public StringComparator(final Locale locale) {
      this.locale = locale;
   }

   private Collator _getCollator() {
      if (collator == null) {
         collator = Collator.getInstance(locale);
      }
      return collator;
   }

   @Override
   public int compare(final String o1, final String o2) {
      if (o1 == o2)
         return 0;
      return _getCollator().compare(o1, o2);
   }

   public Locale getLocale() {
      return locale;
   }
}
