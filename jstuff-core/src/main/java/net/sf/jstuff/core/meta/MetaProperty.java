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
package net.sf.jstuff.core.meta;

import static java.util.Collections.*;
import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class MetaProperty<P> implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static <P> MetaProperty<P> create(final MetaClass< ? > metaClass, final String name, final Class<P> type, final int lowerBound,
			final int upperBound, final boolean ordered, final boolean unique, final boolean container, final String description,
			final Map<String, ? extends Serializable> properties)
	{
		synchronized (metaClass)
		{
			final MetaProperty<P> p = new MetaProperty<P>();
			p.metaClass = metaClass;
			p.name = name;
			p.lowerBound = lowerBound;
			p.upperBound = upperBound;
			p.type = type;
			p.ordered = ordered;
			p.unique = unique;
			p.description = description == null ? "" : description;
			p.properties = newHashMap(properties);
			p.propertiesReadOnly = unmodifiableMap(p.properties);
			metaClass.addProperty(p);
			return p;
		}
	}

	private MetaClass< ? > metaClass;

	private String name;
	private transient String description;
	private transient int lowerBound;
	private transient int upperBound;
	private transient Class<P> type;
	private transient boolean container;
	private transient boolean ordered;
	private transient boolean unique;
	private transient boolean writable;
	private transient Map<String, ? extends Serializable> properties;
	private transient Map<String, ? extends Serializable> propertiesReadOnly;

	private MetaProperty()
	{
		super();
	}

	public String getDescription()
	{
		return description;
	}

	public int getLowerBound()
	{
		return lowerBound;
	}

	@SuppressWarnings("unchecked")
	public <T> MetaClass<T> getMetaClass()
	{
		return (MetaClass<T>) metaClass;
	}

	public String getName()
	{
		return name;
	}

	public Map<String, ? extends Serializable> getProperties()
	{
		return propertiesReadOnly;
	}

	public Class<P> getType()
	{
		return type;
	}

	public int getUpperBound()
	{
		return upperBound;
	}

	public boolean isContainer()
	{
		return container;
	}

	public boolean isMany()
	{
		return upperBound < 0 || upperBound > 1;
	}

	public boolean isOrdered()
	{
		return ordered;
	}

	public boolean isRequired()
	{
		return lowerBound > 0;
	}

	public boolean isScalarType()
	{
		return type == boolean.class || type == Boolean.class || //
				type == char.class || type == Character.class || //
				type == int.class || //
				type == long.class || //
				type == byte.class || //
				type == short.class || //
				type == float.class || //
				Enum.class.isAssignableFrom(type) || //
				Number.class.isAssignableFrom(type) || //
				CharSequence.class.isAssignableFrom(type) || //
				Date.class.isAssignableFrom(type);
	}

	public boolean isUnique()
	{
		return unique;
	}

	public boolean isWritable()
	{
		return writable;
	}

	private Object readResolve()
	{
		synchronized (metaClass.getType())
		{
			return metaClass.getProperties().get(name);
		}
	}

	public void setContainer(final boolean container)
	{
		this.container = container;
	}
}
