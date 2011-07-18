/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import net.sf.jstuff.core.Assert;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
			Assert.argumentNotNull("next", next);

			return new And<V>(AbstractAccept.this, next);
		}

		public <V extends T> Or<V> or(final Accept< ? super V> next)
		{
			Assert.argumentNotNull("next", next);

			return new Or<V>(AbstractAccept.this, next);
		}

		@Override
		public String toString()
		{
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	public static class And<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final Accept< ? super V> first;
		public final Accept< ? super V> second;

		public And(final Accept< ? super V> first, final Accept< ? super V> second)
		{
			Assert.argumentNotNull("first", first);
			Assert.argumentNotNull("second", second);

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

	public interface ChainableAccept<T> extends Accept<T>
	{
		<V extends T> ChainableAccept<V> and(final Accept< ? super V> next);

		<V extends T> ChainableAccept<V> or(final Accept< ? super V> next);
	}

	public static class EndingWith<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final String suffix;

		public EndingWith(final String suffix)
		{
			this.suffix = suffix;
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return false;
			return obj.toString().endsWith(suffix);
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

	public static class LargerThan<V extends Comparable<V>> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final V compareTo;

		public LargerThan(final V compareTo)
		{
			this.compareTo = compareTo;
		}

		public boolean accept(final V obj)
		{
			return ObjectUtils.compare(obj, compareTo) > 0;
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

	public static class NotEndingWith<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final String suffix;

		public NotEndingWith(final String suffix)
		{
			this.suffix = suffix;
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return true;
			return !obj.toString().endsWith(suffix);
		}
	}

	public static class NotStartingWith<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final String prefix;

		public NotStartingWith(final String prefix)
		{
			this.prefix = prefix;
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return true;
			return !obj.toString().startsWith(prefix);
		}
	}

	public static class Or<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final Accept< ? super V> first;
		public final Accept< ? super V> second;

		public Or(final Accept< ? super V> first, final Accept< ? super V> second)
		{
			Assert.argumentNotNull("first", first);
			Assert.argumentNotNull("second", second);

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

	public static class SmallerThan<V extends Comparable<V>> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final V compareTo;

		public SmallerThan(final V compareTo)
		{
			this.compareTo = compareTo;
		}

		public boolean accept(final V obj)
		{
			return ObjectUtils.compare(obj, compareTo) < 0;
		}
	}

	public static class StartingWith<V> extends AbstractAccept<V>
	{
		private static final long serialVersionUID = 1L;

		public final String prefix;

		public StartingWith(final String prefix)
		{
			this.prefix = prefix;
		}

		public boolean accept(final V obj)
		{
			if (obj == null) return false;
			return obj.toString().startsWith(prefix);
		}
	}

	public static <V> And<V> and(final Accept<V> first, final Accept<V> second)
	{
		return new And<V>(first, second);
	}

	public static <V> EndingWith<V> endingWith(final String suffix)
	{
		return new EndingWith<V>(suffix);
	}

	public static <V> EqualTo<V> equalTo(final V equivalent)
	{
		return new EqualTo<V>(equivalent);
	}

	public static <V extends Comparable<V>> LargerThan<V> largerThan(final V compareTo)
	{
		return new LargerThan<V>(compareTo);
	}

	public static <V> NonNull<V> nonNull()
	{
		return new NonNull<V>();
	}

	public static <V> NotEndingWith<V> notEndingWith(final String suffix)
	{
		return new NotEndingWith<V>(suffix);
	}

	public static <V> NotStartingWith<V> notStartingWith(final String prefix)
	{
		return new NotStartingWith<V>(prefix);
	}

	public static <V extends Comparable<V>> SmallerThan<V> smallerThan(final V compareTo)
	{
		return new SmallerThan<V>(compareTo);
	}

	public static <V> StartingWith<V> startingWith(final String prefix)
	{
		return new StartingWith<V>(prefix);
	};

	protected Accepts()
	{
		super();
	}
}
