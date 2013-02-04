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

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import net.sf.jstuff.core.collection.CompositeMap;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public final class MetaClass<T> implements Serializable
{
	private static final long serialVersionUID = 1L;

	private static final Map<Class< ? >, MetaClass< ? >> REGISTRY = new WeakHashMap<Class< ? >, MetaClass< ? >>();

	@SuppressWarnings("unchecked")
	public static <T> MetaClass<T> of(final Class<T> type, final String name, final String description, final MetaClass< ? > parent)
	{
		Args.notNull("type", type);
		Args.notNull("name", name);
		Assert.isFalse(REGISTRY.containsKey(type), "A meta class for [" + type.getName() + "] exists already.");
		synchronized (type)
		{
			final MetaClass< ? > metaClass = REGISTRY.get(type);
			if (metaClass != null) return (MetaClass<T>) metaClass;

			final MetaClass<T> newMetaClass = new MetaClass<T>(type, name, description, parent);
			REGISTRY.put(type, newMetaClass);
			return newMetaClass;
		}
	}

	private final Class<T> type;
	private final transient MetaClass< ? > parent;
	private final transient String name;
	private final transient String description;

	private transient final Map<String, MetaProperty< ? >> properties = newLinkedHashMap();
	private transient final Map<String, MetaProperty< ? >> propertiesReadOnly = Collections.unmodifiableMap(properties);
	private transient final Map<String, MetaProperty< ? >> propertiesRecursivelyReadOnly;

	@SuppressWarnings("unchecked")
	private MetaClass(final Class<T> type, final String name, final String description, final MetaClass< ? > parent)
	{
		this.type = type;
		this.name = name;
		this.description = description;
		this.parent = parent;
		propertiesRecursivelyReadOnly = parent == null ? propertiesReadOnly : CompositeMap.of(propertiesReadOnly, parent.getProperties());
	}

	void addProperty(final MetaProperty< ? > prop)
	{
		Assert.isFalse(properties.containsKey(prop.getName()), "A meta property with name [" + prop.getName()
				+ "] exists already for class [" + type.getName() + "]");
		properties.put(prop.getName(), prop);
	}

	public String getDescription()
	{
		return description;
	}

	public String getName()
	{
		return name;
	}

	public MetaClass< ? > getParent()
	{
		return parent;
	}

	public Map<String, MetaProperty< ? >> getProperties()
	{
		return propertiesReadOnly;
	}

	public Map<String, MetaProperty< ? >> getPropertiesRecursively()
	{
		return propertiesRecursivelyReadOnly;
	}

	public Class<T> getType()
	{
		return type;
	}

	private Object readResolve() throws ObjectStreamException
	{
		synchronized (type)
		{
			final MetaClass< ? > metaClass = REGISTRY.get(this.type);
			if (metaClass != null) return metaClass;
			throw new InvalidObjectException("MetaClass instance for type [" + this.type.getName() + "] not found in registry!");
		}
	}
}
