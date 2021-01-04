/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.core.functional;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.sf.jstuff.core.ogn.ObjectGraphNavigatorDefaultImpl;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Accepts {
   public abstract static class AbstractAccept<T> implements ChainableAccept<T>, Serializable {
      private static final long serialVersionUID = 1L;

      @Override
      public String toString() {
         return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
      }
   }

   public abstract static class AbstractCaseSensitiveAccept<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      protected Locale ignoreCaseLocale;

      public ChainableAccept<V> ignoreCase() {
         return ignoreCase(null);
      }

      public abstract ChainableAccept<V> ignoreCase(Locale locale);

      protected final String stringify(final Object obj) {
         if (ignoreCaseLocale == null)
            return obj.toString();
         return obj.toString().toLowerCase(ignoreCaseLocale);
      }
   }

   public static class And<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final Accept<? super V> first;
      public final Accept<? super V> second;

      public And(final Accept<? super V> first, final Accept<? super V> second) {
         Args.notNull("first", first);
         Args.notNull("second", second);

         this.first = first;
         this.second = second;
      }

      @Override
      public boolean accept(final V obj) {
         return first.accept(obj) && second.accept(obj);
      }

      @Override
      public String toString() {
         return "(" + first + " && " + second + ")";
      }
   }

   public static class Anything<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      private static final Anything<?> INSTANCE = new Anything<>();

      @Override
      public boolean accept(final V obj) {
         return true;
      }
   }

   public static class Contains<V> extends AbstractCaseSensitiveAccept<V> {
      private static final long serialVersionUID = 1L;

      public final String searchFor;

      public Contains(final String searchFor) {
         Args.notNull("searchFor", searchFor);

         this.searchFor = searchFor;
      }

      @Override
      public boolean accept(final V obj) {
         if (obj == null)
            return false;
         return stringify(obj).indexOf(searchFor) > -1;
      }

      @Override
      public Contains<V> ignoreCase(Locale locale) {
         if (locale == null) {
            locale = Locale.getDefault();
         }
         final Contains<V> accept = new Contains<>(searchFor.toLowerCase(locale));
         accept.ignoreCaseLocale = locale;
         return accept;
      }
   }

   public static class EndingWith<V> extends AbstractCaseSensitiveAccept<V> {
      private static final long serialVersionUID = 1L;

      public final String suffix;

      public EndingWith(final String suffix) {
         Args.notNull("suffix", suffix);

         this.suffix = stringify(suffix);
      }

      @Override
      public boolean accept(final V obj) {
         if (obj == null)
            return false;
         return stringify(obj).endsWith(suffix);
      }

      @Override
      public EndingWith<V> ignoreCase(Locale locale) {
         if (locale == null) {
            locale = Locale.getDefault();
         }
         final EndingWith<V> accept = new EndingWith<>(stringify(suffix));
         accept.ignoreCaseLocale = locale;
         return accept;
      }
   }

   public static class EqualTo<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final V equivalent;

      public EqualTo(final V equivalent) {
         this.equivalent = equivalent;
      }

      @Override
      public boolean accept(final V obj) {
         return Objects.equals(obj, equivalent);
      }
   }

   public static class GreaterThan<V extends Comparable<V>> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final V compareTo;

      public GreaterThan(final V compareTo) {
         this.compareTo = compareTo;
      }

      @Override
      public boolean accept(final V obj) {
         return ObjectUtils.compare(obj, compareTo) > 0;
      }
   }

   public static class InstanceOf<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final Class<?> type;

      public InstanceOf(final Class<?> type) {
         this.type = type;
      }

      @Override
      public boolean accept(final V obj) {
         return type.isInstance(obj);
      }
   }

   public static class LessThan<V extends Comparable<V>> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final V compareTo;

      public LessThan(final V compareTo) {
         this.compareTo = compareTo;
      }

      @Override
      public boolean accept(final V obj) {
         if (obj == null)
            return false;
         return ObjectUtils.compare(obj, compareTo) < 0;
      }
   }

   public static class Matches<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final Pattern pattern;

      public Matches(final String pattern) {
         Args.notNull("pattern", pattern);
         this.pattern = Pattern.compile(pattern);
      }

      @Override
      public boolean accept(final V obj) {
         if (obj == null)
            return false;
         return pattern.matcher(obj.toString()).matches();
      }
   }

   public static class Not<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final Accept<? super V> accept;

      public Not(final Accept<? super V> accept) {
         Args.notNull("accept", accept);

         this.accept = accept;
      }

      @Override
      public boolean accept(final V obj) {
         return !accept.accept(obj);
      }

      @Override
      public String toString() {
         return "!" + accept;
      }
   }

   public static class Nothing<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      private static final Nothing<?> INSTANCE = new Nothing<>();

      @Override
      public boolean accept(final V obj) {
         return false;
      }
   }

   public static class NotNull<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean accept(final V obj) {
         return obj != null;
      }
   }

   public static class Null<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean accept(final V obj) {
         return obj == null;
      }
   }

   public static class Or<V> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final Accept<? super V> first;
      public final Accept<? super V> second;

      public Or(final Accept<? super V> first, final Accept<? super V> second) {
         Args.notNull("first", first);
         Args.notNull("second", second);

         this.first = first;
         this.second = second;
      }

      @Override
      public boolean accept(final V obj) {
         return first.accept(obj) || second.accept(obj);
      }

      @Override
      public String toString() {
         return "(" + first + " || " + second + ")";
      }
   }

   public static class Property<V, PropertyType> extends AbstractAccept<V> {
      private static final long serialVersionUID = 1L;

      public final Accept<PropertyType> accept;
      public final String propertyPath;

      public Property(final String propertyPath, final Accept<PropertyType> accept) {
         Args.notNull("propertyPath", propertyPath);
         Args.notNull("accept", accept);

         this.propertyPath = propertyPath;
         this.accept = accept;
      }

      @Override
      @SuppressWarnings("unchecked")
      public boolean accept(final V obj) {
         try {
            return accept.accept((PropertyType) ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(obj, propertyPath));
         } catch (final ClassCastException ex) {
            return false;
         }
      }
   }

   public static class StartingWith<V> extends AbstractCaseSensitiveAccept<V> {
      private static final long serialVersionUID = 1L;

      public final String prefix;

      public StartingWith(final String prefix) {
         Args.notNull("prefix", prefix);

         this.prefix = stringify(prefix);
      }

      @Override
      public boolean accept(final V obj) {
         if (obj == null)
            return false;
         return stringify(obj).startsWith(prefix);
      }

      @Override
      public StartingWith<V> ignoreCase(Locale locale) {
         if (locale == null) {
            locale = Locale.getDefault();
         }
         final StartingWith<V> accept = new StartingWith<>(stringify(prefix));
         accept.ignoreCaseLocale = locale;
         return accept;
      }
   }

   public static <V> And<V> and(final Accept<? super V> first, final Accept<? super V> second) {
      return new And<>(first, second);
   }

   @SuppressWarnings("unchecked")
   public static <V> Anything<V> anything() {
      return (Anything<V>) Anything.INSTANCE;
   }

   public static <V> Contains<V> contains(final String searchFor) {
      return new Contains<>(searchFor);
   }

   public static <V> EndingWith<V> endingWith(final String suffix) {
      return new EndingWith<>(suffix);
   }

   public static <V> EqualTo<V> equalTo(final V equivalent) {
      return new EqualTo<>(equivalent);
   }

   public static <V extends Comparable<V>> GreaterThan<V> greaterThan(final V compareTo) {
      return new GreaterThan<>(compareTo);
   }

   public static <V> InstanceOf<V> instanceOf(final Class<?> type) {
      return new InstanceOf<>(type);
   }

   public static <V> Null<V> isNull() {
      return new Null<>();
   }

   public static <V extends Comparable<V>> LessThan<V> lessThan(final V compareTo) {
      return new LessThan<>(compareTo);
   }

   public static <V> Matches<V> matches(final String pattern) {
      return new Matches<>(pattern);
   }

   public static <V> Not<V> not(final Accept<? super V> accept) {
      return new Not<>(accept);
   }

   @SuppressWarnings("unchecked")
   public static <V> Nothing<V> nothing() {
      return (Nothing<V>) Nothing.INSTANCE;
   }

   public static <V> NotNull<V> notNull() {
      return new NotNull<>();
   }

   public static <V> Or<V> or(final Accept<? super V> first, final Accept<? super V> second) {
      return new Or<>(first, second);
   }

   public static <V, PropertyType> Property<V, PropertyType> property(@SuppressWarnings("unused") final Class<V> castingHelper, final String propertyPath,
      final Accept<PropertyType> accept) {
      return new Property<>(propertyPath, accept);
   }

   public static <V, PropertyType> Property<V, PropertyType> property(final String propertyPath, final Accept<PropertyType> accept) {
      return new Property<>(propertyPath, accept);
   }

   public static <V> StartingWith<V> startingWith(final String prefix) {
      return new StartingWith<>(prefix);
   }
}
