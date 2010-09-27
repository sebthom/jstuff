/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.jstuff.integration.spring;

import java.util.Map;

import net.sf.jstuff.core.Assert;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * <bean name="beanLocator" class="net.sf.j5utils.spring.BeanLocator" factory-method="init" destroy-method="destroy" />
 *  
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class BeanLocator implements BeanFactoryAware
{
	private static BeanLocator instance;

	/**
	 * Returns the spring managed bean with the given type
	 *
	 * @param beanType the type of the bean
	 *
	 * @return the spring managed bean with the given name
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getByClass(final Class<T> beanType)
	{
		Assert.argumentNotNull("beanType", beanType);
		Assert.notNull(instance, "Spring context not initialized yet.");

		final Map< ? , ? > beans = instance.factory.getBeansOfType(beanType, true, true);

		if (beans == null || beans.isEmpty())
			throw new IllegalArgumentException("No Spring managed bean for type '" + beanType.getName() //
					+ "' was found.");
		if (beans.size() > 1)
			throw new IllegalStateException("More than one Spring managed bean for type '" + beanType.getName() //
					+ "' was found.");
		return (T) beans.values().iterator().next();
	}

	/**
	 * Returns the spring managed bean with the given name.
	 *
	 * @param   beanName  the bean name
	 *
	 * @return  the spring managed bean with the given name
	 */
	public static Object getByName(final String beanName)
	{
		Assert.notNull(instance, "Spring context not initialized yet.");

		Assert.notNull(beanName, "[beanName] must not be null.");
		try
		{
			return instance.factory.getBean(beanName);
		}
		catch (final NoSuchBeanDefinitionException e)
		{
			throw new IllegalArgumentException("No Spring managed bean for name '" + beanName + "' was found.");
		}
	}

	/**
	 * For use by Spring only.
	 *
	 * @return  the beanLocator instance
	 */
	public static synchronized BeanLocator init()
	{
		Assert.isNull(instance, "A instance of BeanLocator already exists.");
		instance = new BeanLocator();
		return instance;
	}

	private ListableBeanFactory factory;

	private BeanLocator()
	{
		super();
	}

	/**
	 * for use by Spring only.
	 */

	public void destroy()
	{
		instance = null;
	}

	/**
	 * {@inheritDoc}.
	 */
	public void setBeanFactory(final BeanFactory beanFactory)
	{
		Assert.argumentNotNull("beanFactory", beanFactory);

		if (!ListableBeanFactory.class.isAssignableFrom(beanFactory.getClass()))
			throw new IllegalStateException("Bean factory must be of type ListableBeanFactory.");
		this.factory = (ListableBeanFactory) beanFactory;
	}
}
