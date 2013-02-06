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
package net.sf.jstuff.core.ogn;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.sf.jstuff.core.reflection.ReflectionUtils;
import net.sf.jstuff.core.validation.Args;

/**
 * Default object graph navigator implementation.
 *
 * Object path separator is a colon (.), e.g. owner.address.street
 *
 * The implementation currently is limited to address fields and properties. Separate items of arrays, maps or keys cannot be addressed.
 *
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class ObjectGraphNavigatorDefaultImpl implements ObjectGraphNavigator
{
	public static final ObjectGraphNavigatorDefaultImpl INSTANCE = new ObjectGraphNavigatorDefaultImpl(false);

	private final boolean strict;

	public ObjectGraphNavigatorDefaultImpl(final boolean strict)
	{
		this.strict = strict;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValueAt(final Object root, final String path)
	{
		Args.notNull("root", root);
		Args.notNull("path", path);

		Object parent = null;
		Object target = root;
		for (final String chunk : path.split("\\."))
		{
			parent = target;
			if (parent == null) return null;

			final Method getter = ReflectionUtils.getGetterRecursive(parent.getClass(), chunk);
			if (getter == null)
			{
				final Field field = ReflectionUtils.getFieldRecursive(parent.getClass(), chunk);
				if (field == null)
				{
					if (strict)
						throw new IllegalArgumentException("Invalid object navigation path from root object class ["
								+ root.getClass().getName() + "] path: " + path);
					return null;
				}
				target = ReflectionUtils.getFieldValue(field, parent);
			}
			else
				target = ReflectionUtils.invokeMethod(getter, parent);
		}
		return (T) target;
	}

	public ObjectGraphNavigationResult navigateTo(final Object root, final String path)
	{
		Args.notNull("root", root);
		Args.notNull("path", path);

		Object parent = null;
		Object target = root;
		AccessibleObject targetAccessor = null;
		for (final String chunk : path.split("\\."))
		{
			parent = target;
			if (parent == null) return null;

			final Method getter = ReflectionUtils.getGetterRecursive(parent.getClass(), chunk);
			if (getter == null)
			{
				final Field field = ReflectionUtils.getFieldRecursive(parent.getClass(), chunk);
				if (field == null)
				{
					if (strict)
						throw new IllegalArgumentException("Invalid object navigation path from root object class ["
								+ root.getClass().getName() + "] path: " + path);
					return null;
				}
				target = ReflectionUtils.getFieldValue(field, parent);
				targetAccessor = field;
			}
			else
			{
				target = ReflectionUtils.invokeMethod(getter, parent);
				targetAccessor = getter;
			}
		}
		return new ObjectGraphNavigationResult(root, path, parent, targetAccessor, target);
	}

	public boolean isStrict()
	{
		return strict;
	}
}
