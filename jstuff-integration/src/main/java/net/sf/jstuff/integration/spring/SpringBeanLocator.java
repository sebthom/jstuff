/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2012 Sebastian
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

import net.sf.jstuff.core.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <bean name="beanLocator" class="net.sf.jstuff.integration.spring.BeanLocator" factory-method="init" destroy-method="destroy" />
 *  
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@Component
public class SpringBeanLocator implements BeanFactoryAware, DisposableBean
{
	private static final Logger LOG = Logger.make();

	private static SpringBeanLocator DEFAULT_INSTANCE;

	/**
	 * @return the default instance (the last instantiated one by any spring context)
	 */
	public static SpringBeanLocator get()
	{
		Assert.notNull(DEFAULT_INSTANCE, "Spring context not initialized yet.");
		return DEFAULT_INSTANCE;
	}

	@Autowired
	private ListableBeanFactory factory;

	private SpringBeanLocator()
	{
		Assert.isNull(DEFAULT_INSTANCE, "A instance of " + this.getClass().getName() + " already exists.");

		LOG.info("Instantiated.");

		DEFAULT_INSTANCE = this;
	}

	/**
	 * Returns the spring managed bean with the given type
	 *
	 * @param beanType the type of the bean
	 *
	 * @return the spring managed bean with the given name
	 */
	@SuppressWarnings("unchecked")
	public <T> T byClass(final Class<T> beanType)
	{
		Args.notNull("beanType", beanType);

		final Map< ? , ? > beans = factory.getBeansOfType(beanType, true, true);

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
	@SuppressWarnings("unchecked")
	public <T> T byName(final String beanName)
	{
		Assert.notNull(beanName, "[beanName] must not be null.");
		try
		{
			return (T) factory.getBean(beanName);
		}
		catch (final NoSuchBeanDefinitionException e)
		{
			throw new IllegalArgumentException("No Spring managed bean for name '" + beanName + "' was found.");
		}
	}

	/**
	 * <p><b>For use by Spring only!</b></p>
	 * {@inheritDoc}
	 */
	public void destroy()
	{
		DEFAULT_INSTANCE = null;
	}

	public ListableBeanFactory getBeanFactory()
	{
		return factory;
	}

	/**
	 * {@inheritDoc}.
	 */
	public void setBeanFactory(final BeanFactory beanFactory)
	{
		Args.notNull("beanFactory", beanFactory);

		if (!(beanFactory instanceof ListableBeanFactory))
			throw new IllegalStateException("Argument [beanFactory] must be of type "
					+ beanFactory.getClass().getName());

		factory = (ListableBeanFactory) beanFactory;
	}
}
