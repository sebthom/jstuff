/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.core.functional;

import java.io.Serializable;
import java.util.Locale;

import net.sf.jstuff.core.ogn.ObjectGraphNavigatorDefaultImpl;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Accepts
{
	public static abstract class AbstractAccept<T> implements ChainableAccept<T>, Serializable
	{
		private static final long serialVersionUID = 1L;

		public <V extends T> And<V> and(final Accept< ? super V> next)
		{
			Args.notNull("next", next);

			return new And<V>(AbstractAccept.this, next);
		}

		public <V extends T> Or<V> or(final Accept< ? super V> next)
		{
			Args.notNull("next", next);

			return new Or<V>(AbstractAccept.this, next);
		}

		@Override
		public String toString()
		{
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	public static abstract class AbstractCaseSensitiveAccept<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		protected Locale ignoreCaseLocale;

		public ChainableAccept<V> ignoreCase()
		{
			return ignoreCase(null);
		}

		public abstract ChainableAccept<V> ignoreCase(final Locale locale);

		protected String stringify(final Object obj)
		{
			if (ignoreCaseLocale == null) return obj.toString();
			return obj.toString().toLowerCase(ignoreCaseLocale);
		}
	}

	public static class And<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final Accept< ? super V> first;
		public final Accept< ? super V> second;

		public And(final Accept< ? super V> first, final Accept< ? super V> second)
		{
			Args.notNull("first", first);
			Args.notNull("second", second);

			this.first = first;
			this.second = second;
		}

		public boolean accept(final V obj)
		{
			return first.accept(obj) && second.accept(obj);
		}

		@Override
		public String toString()
		{
			return "(" + first.toString() + " && " + second.toString() + ")";
		}
	}

	public static class Contains<V> extends AbstractCaseSensitiveAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final String searchFor;

		public Contains(final String searchFor)
		{
			Args.notNull("searchFor", searchFor);

			this.searchFor = searchFor;
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return false;
			return stringify(obj).indexOf(searchFor) > -1;
		}

		@Override
		public Contains<V> ignoreCase(Locale locale)
		{
			if (locale == null) locale = Locale.getDefault();
			final Contains<V> accept = new Contains<V>(searchFor.toLowerCase(locale));
			accept.ignoreCaseLocale = locale;
			return accept;
		}
	}

	public static class EndingWith<V> extends AbstractCaseSensitiveAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final String suffix;

		public EndingWith(final String suffix)
		{
			Args.notNull("suffix", suffix);

			this.suffix = stringify(suffix);
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return false;
			return stringify(obj).endsWith(suffix);
		}

		@Override
		public EndingWith<V> ignoreCase(Locale locale)
		{
			if (locale == null) locale = Locale.getDefault();
			final EndingWith<V> accept = new EndingWith<V>(stringify(suffix));
			accept.ignoreCaseLocale = locale;
			return accept;
		}
	}

	public static class EqualTo<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final V equivalent;

		public EqualTo(final V equivalent)
		{
			this.equivalent = equivalent;
		}

		public boolean accept(final V obj)
		{
			return ObjectUtils.equals(obj, equivalent);
		}
	}

	public static class GreaterThan<V extends Comparable<V>> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final V compareTo;

		public GreaterThan(final V compareTo)
		{
			this.compareTo = compareTo;
		}

		public boolean accept(final V obj)
		{
			return ObjectUtils.compare(obj, compareTo) > 0;
		}
	}

	public static class LessThan<V extends Comparable<V>> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final V compareTo;

		public LessThan(final V compareTo)
		{
			this.compareTo = compareTo;
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return false;
			return ObjectUtils.compare(obj, compareTo) < 0;
		}
	}

	public static class NonNull<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public boolean accept(final V obj)
		{
			return obj != null;
		}
	}

	public static class Property<V, PropertyType> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final Accept<PropertyType> accept;
		public final String propertyPath;

		public Property(final String propertyPath, final Accept<PropertyType> accept)
		{
			Args.notNull("propertyPath", propertyPath);
			Args.notNull("accept", accept);

			this.propertyPath = propertyPath;
			this.accept = accept;
		}

		@SuppressWarnings("unchecked")
		public boolean accept(final V obj)
		{
			try
			{
				return accept.accept((PropertyType) ObjectGraphNavigatorDefaultImpl.INSTANCE.getValueAt(obj, propertyPath));
			}
			catch (final ClassCastException ex)
			{
				return false;
			}
		}
	}

	public static class Not<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final Accept< ? super V> accept;

		public Not(final Accept< ? super V> accept)
		{
			Args.notNull("accept", accept);

			this.accept = accept;
		}

		public boolean accept(final V obj)
		{
			return !accept.accept(obj);
		}

		@Override
		public String toString()
		{
			return "!" + accept.toString();
		}
	}

	public static class Null<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public boolean accept(final V obj)
		{
			return obj == null;
		}
	}

	public static class Or<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final Accept< ? super V> first;
		public final Accept< ? super V> second;

		public Or(final Accept< ? super V> first, final Accept< ? super V> second)
		{
			Args.notNull("first", first);
			Args.notNull("second", second);

			this.first = first;
			this.second = second;
		}

		public boolean accept(final V obj)
		{
			return first.accept(obj) || second.accept(obj);
		}

		@Override
		public String toString()
		{
			return "(" + first.toString() + " || " + second.toString() + ")";
		}
	}

	public static class StartingWith<V> extends AbstractCaseSensitiveAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final String prefix;

		public StartingWith(final String prefix)
		{
			Args.notNull("prefix", prefix);

			this.prefix = stringify(prefix);
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return false;
			return stringify(obj).startsWith(prefix);
		}

		@Override
		public StartingWith<V> ignoreCase(Locale locale)
		{
			if (locale == null) locale = Locale.getDefault();
			final StartingWith<V> accept = new StartingWith<V>(stringify(prefix));
			accept.ignoreCaseLocale = locale;
			return accept;
		}
	}

	public static <V> And<V> and(final Accept< ? super V> first, final Accept< ? super V> second)
	{
		return new And<V>(first, second);
	}

	public static <V> Contains<V> contains(final String searchFor)
	{
		return new Contains<V>(searchFor);
	}

	public static <V> EndingWith<V> endingWith(final String suffix)
	{
		return new EndingWith<V>(suffix);
	}

	public static <V> EqualTo<V> equalTo(final V equivalent)
	{
		return new EqualTo<V>(equivalent);
	}

	public static <V extends Comparable<V>> GreaterThan<V> greaterThan(final V compareTo)
	{
		return new GreaterThan<V>(compareTo);
	}

	public static <V> Null<V> isNull()
	{
		return new Null<V>();
	}

	public static <V extends Comparable<V>> LessThan<V> lessThan(final V compareTo)
	{
		return new LessThan<V>(compareTo);
	}

	public static <V> NonNull<V> nonNull()
	{
		return new NonNull<V>();
	}

	public static <V> Not<V> not(final Accept< ? super V> accept)
	{
		return new Not<V>(accept);
	}

	public static <V> Or<V> or(final Accept< ? super V> first, final Accept< ? super V> second)
	{
		return new Or<V>(first, second);
	}

	public static <V, PropertyType> Property<V, PropertyType> property(final String propertyPath, final Accept<PropertyType> accept)
	{
		return new Property<V, PropertyType>(propertyPath, accept);
	}

	public static <V, PropertyType> Property<V, PropertyType> property(final Class<V> castingHelper, final String propertyPath,
			final Accept<PropertyType> accept)
	{
		return new Property<V, PropertyType>(propertyPath, accept);
	}

	public static <V> StartingWith<V> startingWith(final String prefix)
	{
		return new StartingWith<V>(prefix);
	}

	protected Accepts()
	{
		super();
	}
}
