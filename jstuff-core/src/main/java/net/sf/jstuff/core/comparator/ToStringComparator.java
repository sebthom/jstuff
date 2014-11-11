/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2014 Sebastian
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
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ToStringComparator<T> implements Comparator<T>, Serializable
{
	private static final long serialVersionUID = 1L;

	private static final ToStringComparator< ? > INSTANCE = new ToStringComparator<Object>();

	public static <T> ToStringComparator<T> create(final Locale locale)
	{
		return new ToStringComparator<T>(locale);
	}

	@SuppressWarnings("unchecked")
	public static <T> ToStringComparator<T> get()
	{
		return (ToStringComparator<T>) INSTANCE;
	}

	// collator is only serializable starting Java 6
	private transient Collator collator;
	private final Locale locale;

	public ToStringComparator()
	{
		this(Locale.getDefault());
	}

	public ToStringComparator(final Locale locale)
	{
		this.locale = locale;
	}

	private Collator _getCollator()
	{
		if (collator == null) collator = Collator.getInstance(locale);
		return collator;
	}

	public int compare(final T o1, final T o2)
	{
		if (o1 == o2) return 0;
		return _getCollator().compare(o1 == null ? null : o1.toString(), o2 == null ? null : o2.toString());
	}

	public Locale getLocale()
	{
		return locale;
	}
}
