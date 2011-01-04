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
package net.sf.jstuff.core.reflection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.sf.jstuff.core.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BeanUtils
{
	private final static WeakHashMap<Class< ? >, Map<String, PropertyDescriptor>> BEAN_PROPERTIES_CACHE = new WeakHashMap<Class< ? >, Map<String, PropertyDescriptor>>();

	/**
	 * list of properties
	 * @return an unmodifiable collection
	 */
	public final static Map<String, PropertyDescriptor> getBeanProperties(final Class< ? > beanType)
	{
		final Map<String, PropertyDescriptor> properties = BEAN_PROPERTIES_CACHE.get(beanType);
		if (properties == null)
		{
			populateCache(beanType);
			return BEAN_PROPERTIES_CACHE.get(beanType);
		}
		return properties;
	}

	/**
	 * list of properties
	 * @return
	 */
	public final static Collection<PropertyDescriptor> getBeanPropertyDescriptors(final Class< ? > beanType)
	{
		final Map<String, PropertyDescriptor> properties = BEAN_PROPERTIES_CACHE.get(beanType);
		if (properties == null)
		{
			populateCache(beanType);
			return BEAN_PROPERTIES_CACHE.get(beanType).values();
		}
		return properties.values();
	}

	public final static Set<String> getBeanPropertyNames(final Class< ? > beanType)
	{
		final Map<String, PropertyDescriptor> properties = BEAN_PROPERTIES_CACHE.get(beanType);
		if (properties == null)
		{
			populateCache(beanType);
			return BEAN_PROPERTIES_CACHE.get(beanType).keySet();
		}
		return properties.keySet();
	}

	private final static void populateCache(final Class< ? > beanType)
	{
		try
		{
			final BeanInfo beanInfo = Introspector.getBeanInfo(beanType, Object.class);
			final PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			final Map<String, PropertyDescriptor> beanProperties = new HashMap<String, PropertyDescriptor>(props.length);
			for (final PropertyDescriptor prop : props)
				beanProperties.put(prop.getName(), prop);
			BEAN_PROPERTIES_CACHE.put(beanType, Collections.unmodifiableMap(beanProperties));
		}
		catch (final IntrospectionException ex)
		{
			throw new ReflectionException(ex);
		}
	}

	public final static Object valueOf(final String stringValue, final Class< ? > targetType)
	{
		Assert.notNull(targetType, "Argument targetType must not be null");

		if (int.class.isAssignableFrom(targetType) || Integer.class.isAssignableFrom(targetType))
			return Integer.valueOf(stringValue);
		if (float.class.isAssignableFrom(targetType) || Float.class.isAssignableFrom(targetType))
			return Float.valueOf(stringValue);
		if (double.class.isAssignableFrom(targetType) || Double.class.isAssignableFrom(targetType))
			return Double.valueOf(stringValue);
		if (short.class.isAssignableFrom(targetType) || Short.class.isAssignableFrom(targetType))
			return Short.valueOf(stringValue);
		if (byte.class.isAssignableFrom(targetType) || Byte.class.isAssignableFrom(targetType))
			return Byte.valueOf(stringValue);
		if (boolean.class.isAssignableFrom(targetType) || Boolean.class.isAssignableFrom(targetType))
			return Boolean.valueOf(stringValue);
		return stringValue;
	}

	public final static Object[] valuesOf(final String[] stringValues, final Class< ? >[] targetTypes)
	{
		Assert.notNull(stringValues, "Argument stringValues must not be null");
		Assert.notNull(targetTypes, "Argument targetTypes must not be null");
		Assert.isTrue(stringValues.length == targetTypes.length,
				"Argments stringValues and targetTypes must have the same length");

		final Object[] result = new Object[targetTypes.length];

		for (int i = 0, l = targetTypes.length; i < l; i++)
			result[i] = valueOf(stringValues[i], targetTypes[i]);
		return result;
	}

	protected BeanUtils()
	{
		super();
	}
}
