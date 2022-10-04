/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class StringComparator implements Comparator<String>, Serializable {
   private static final long serialVersionUID = 1L;

   public static final StringComparator INSTANCE = new StringComparator(Locale.getDefault());

   @Nullable
   private Collator collator;
   private final Locale locale;

   public StringComparator(final Locale locale) {
      this.locale = locale;
   }

   @Override
   public int compare(final @Nullable String o1, final @Nullable String o2) {
      if (o1 == o2)
         return 0;
      if (o1 == null)
         return -1;
      if (o2 == null)
         return 1;
      return getCollator().compare(o1, o2);
   }

   private Collator getCollator() {
      var collator = this.collator;
      if (collator == null) {
         collator = this.collator = Collator.getInstance(locale);
      }
      return collator;
   }

   public Locale getLocale() {
      return locale;
   }
}
