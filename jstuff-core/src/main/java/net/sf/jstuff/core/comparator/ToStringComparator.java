/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class ToStringComparator<T> implements Comparator<T>, Serializable {
   private static final long serialVersionUID = 1L;

   private static final ToStringComparator<?> INSTANCE = new ToStringComparator<>(Locale.getDefault());

   /**
    * Shared comparator instance for default locale.
    */
   @SuppressWarnings("unchecked")
   public static <T> ToStringComparator<T> get() {
      return (ToStringComparator<T>) INSTANCE;
   }

   private transient Collator collator;
   private final Locale locale;

   public ToStringComparator(final Locale locale) {
      this.locale = locale;
   }

   @Override
   public int compare(final T o1, final T o2) {
      if (o1 == o2)
         return 0;
      if (o1 == null)
         return -1;
      if (o2 == null)
         return 1;
      return getCollator().compare(o1.toString(), o2.toString());
   }

   private Collator getCollator() {
      if (collator == null) {
         collator = Collator.getInstance(locale);
      }
      return collator;
   }

   public Locale getLocale() {
      return locale;
   }
}
