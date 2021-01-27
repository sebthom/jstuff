/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
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

   private static final ToStringComparator<?> INSTANCE = new ToStringComparator<>();

   public static <T> ToStringComparator<T> create(final Locale locale) {
      return new ToStringComparator<>(locale);
   }

   @SuppressWarnings("unchecked")
   public static <T> ToStringComparator<T> get() {
      return (ToStringComparator<T>) INSTANCE;
   }

   private transient Collator collator;
   private final Locale locale;

   public ToStringComparator() {
      this(Locale.getDefault());
   }

   public ToStringComparator(final Locale locale) {
      this.locale = locale;
   }

   @Override
   public int compare(final T o1, final T o2) {
      if (o1 == o2)
         return 0;
      return getCollator().compare(o1 == null ? null : o1.toString(), o2 == null ? null : o2.toString());
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
