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
package net.sf.jstuff.core.comparator;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.StringUtils;
import net.sf.jstuff.core.reflection.ReflectionUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class PropertyComparator<T> implements Comparator<T>, Serializable
{
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.make();

	private final String[] propertyPath;

	public PropertyComparator(final String propertyPath)
	{
		Args.notNull("propertyPath", propertyPath);

		this.propertyPath = StringUtils.split(propertyPath, '.');
	}

	private Object _getPropertyValue(final Object obj, final String propertyName)
	{
		final Class< ? > type = obj.getClass();

		final Method getter = ReflectionUtils.getGetterRecursive(type, propertyName);
		if (getter != null) return ReflectionUtils.invokeMethod(getter, obj);

		final Field field = ReflectionUtils.getFieldRecursive(type, propertyName);
		if (field == null) return null;
		return ReflectionUtils.getFieldValue(field, obj);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public int compare(final T o1, final T o2)
	{
		if (o1 == o2) return 0;
		if (o1 == null) return 1;
		if (o2 == null) return -1;

		Object v1 = o1;
		Object v2 = o2;

		try
		{
			for (final String propertyName : propertyPath)
			{
				v1 = _getPropertyValue(v1, propertyName);
				v2 = _getPropertyValue(v2, propertyName);

				if (v1 == v2) return 0;
				if (v1 == null) return 1;
				if (v2 == null) return -1;
			}
			return ((Comparable) v1).compareTo(v2);
		}
		catch (final RuntimeException ex)
		{
			LOG.warn(
					"Cannot compare [" + o1 + "] with [" + o2 + "] through property path ["
							+ Arrays.toString(propertyPath) + "]", ex);
			return 0;
		}
	}
}