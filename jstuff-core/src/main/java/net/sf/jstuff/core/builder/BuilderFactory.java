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
package net.sf.jstuff.core.builder;

import static net.sf.jstuff.core.collection.CollectionUtils.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.jstuff.core.reflection.Proxies;
import net.sf.jstuff.core.reflection.Types;
import net.sf.jstuff.core.validation.Args;

import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BuilderFactory<TARGET_TYPE, BUILDER extends Builder<TARGET_TYPE>>
{
	public static <TARGET_TYPE, BUILDER extends Builder<TARGET_TYPE>> BuilderFactory<TARGET_TYPE, BUILDER> create(
			final Class<BUILDER> builderInterface, final Object... constructorArgs)
	{
		return new BuilderFactory<TARGET_TYPE, BUILDER>(builderInterface, constructorArgs);
	}

	private final Class<BUILDER> builderInterface;
	private final Class<TARGET_TYPE> targetClass;
	private final Object[] constructorArgs;

	@SuppressWarnings("unchecked")
	protected BuilderFactory(final Class<BUILDER> builderInterface, final Object... constructorArgs)
	{
		Args.notNull("builderInterface", builderInterface);
		if (!builderInterface.isInterface()) throw new IllegalArgumentException("[builderInterface] must be an interface!");

		this.builderInterface = builderInterface;
		targetClass = (Class<TARGET_TYPE>) TypeUtils.getTypeArguments(builderInterface, Builder.class).values().iterator().next();

		this.constructorArgs = constructorArgs;
	}

	public BUILDER build()
	{
		return Proxies.create(builderInterface, new InvocationHandler()
			{
				final Map<String, Object> properties = newHashMap();

				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
				{
					if ("build".equals(method.getName()) && method.getParameterTypes().length == 0
							&& method.getReturnType().isAssignableFrom(targetClass))
					{
						final TARGET_TYPE target = Types.newInstance(targetClass, constructorArgs);
						for (final Entry<String, Object> property : properties.entrySet())
							Types.writeProperty(target, property.getKey(), property.getValue());
						return target;
					}

					if ("toString".equals(method.getName()) && method.getParameterTypes().length == 0)
						return builderInterface.getName() + "@" + hashCode();

					if (method.getParameterTypes().length == 1 && method.getReturnType().isAssignableFrom(builderInterface))
					{
						if (args[0] == null)
							throw new IllegalArgumentException("Value for builder property [" + method.getName() + "] must not be null!");
						properties.put(method.getName(), args[0]);
						return proxy;
					}
					throw new UnsupportedOperationException();
				}
			});
	}
}
