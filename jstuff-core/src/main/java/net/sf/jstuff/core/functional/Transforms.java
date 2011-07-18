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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public abstract class Transforms
{
	public static abstract class AbstractTransform<From, To> implements ChainableTransform<From, To>, Serializable
	{
		private static final long serialVersionUID = 1L;

		public <NextTo> ChainableTransform<From, NextTo> and(final Transform< ? super To, NextTo> next)
		{
			return new And<From, To, NextTo>(this, next);
		}

		@Override
		public String toString()
		{
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	}

	public static class And<From, Via, To> extends AbstractTransform<From, To>
	{
		private static final long serialVersionUID = 1L;

		private final Transform<From, Via> first;
		private final Transform< ? super Via, To> second;

		public And(final Transform<From, Via> first, final Transform< ? super Via, To> second)
		{
			Assert.argumentNotNull("first", first);
			Assert.argumentNotNull("second", second);

			this.first = first;
			this.second = second;
		}

		public final To transform(final From source)
		{
			return second.transform(first.transform(source));
		}
	}

	public static class Cast<From, To> extends AbstractTransform<From, To>
	{
		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		public To transform(final From source)
		{
			return (To) source;
		}
	}

	public interface ChainableTransform<From, To> extends Transform<From, To>
	{
		<NextTo> ChainableTransform<From, NextTo> and(final Transform< ? super To, NextTo> next);
	}

	public static class ObjectToString<From> extends AbstractTransform<From, String>
	{
		private static final long serialVersionUID = 1L;

		public String transform(final From source)
		{
			return source == null ? null : source.toString();
		}
	}

	public static class Prefix<From> extends AbstractTransform<From, String>
	{
		private static final long serialVersionUID = 1L;

		public final String prefix;

		public Prefix(final String prefix)
		{
			Assert.argumentNotNull("prefix", prefix);

			this.prefix = prefix;
		}

		public String transform(final From source)
		{
			return source == null ? null : prefix + source.toString();
		}
	}

	public static class StringToInt extends AbstractTransform<String, Integer>
	{
		private static final long serialVersionUID = 1L;

		public Integer transform(final String source)
		{
			return source == null ? null : Integer.parseInt(source);
		}
	}

	public static class Suffix<From> extends AbstractTransform<From, String>
	{
		private static final long serialVersionUID = 1L;

		public final String suffix;

		public Suffix(final String suffix)
		{
			Assert.argumentNotNull("suffix", suffix);

			this.suffix = suffix;
		}

		public String transform(final From source)
		{
			return source == null ? null : source.toString() + suffix;
		}
	}

	public static class Trim<From> extends AbstractTransform<From, String>
	{
		private static final long serialVersionUID = 1L;

		public String transform(final From source)
		{
			return source == null ? null : source.toString().trim();
		}
	}

	public static <From> ObjectToString<From> objectToString()
	{
		return new ObjectToString<From>();
	}

	public static <From> Prefix<From> prefix(final String prefix)
	{
		return new Prefix<From>(prefix);
	}

	public static StringToInt stringToInt()
	{
		return new StringToInt();
	}

	public static <From> Suffix<From> suffix(final String suffix)
	{
		return new Suffix<From>(suffix);
	}

	protected Transforms()
	{
		super();
	}
}
