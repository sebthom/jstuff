/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.jstuff.integration.spring;

import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Args;
import net.sf.jstuff.core.validation.Assert;

/**
 * <pre>
 * &lt;context:annotation-config /&gt;
 *
 * &lt;bean class="net.sf.jstuff.integration.spring.SpringBeanLocator" /&gt;
 * </pre>
 *
 * or
 *
 * <pre>
 * &lt;context:annotation-config /&gt;
 *
 * &lt;context:component-scan base-package="net.sf.jstuff.integration.spring" /&gt;
 * </pre>
 * 
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
@Component
public class SpringBeanLocator {
    private static final Logger LOG = Logger.create();

    private static SpringBeanLocator _INSTANCE;

    /**
     * @return the default instance (the last instantiated one by any spring context)
     */
    public static SpringBeanLocator get() {
        Assert.notNull(_INSTANCE, "Spring context not initialized yet.");
        return _INSTANCE;
    }

    @Autowired
    private ListableBeanFactory factory;

    private SpringBeanLocator() {
        Assert.isNull(_INSTANCE, "A instance of " + getClass().getName() + " already exists.");

        LOG.infoNew(this);

        _INSTANCE = this;
    }

    /**
     * Returns the spring managed bean with the given type
     *
     * @param beanType the type of the bean
     *
     * @return the spring managed bean with the given name
     */
    @SuppressWarnings("unchecked")
    public <T> T byClass(final Class<T> beanType) {
        Args.notNull("beanType", beanType);

        final Map<?, ?> beans = factory.getBeansOfType(beanType, true, true);

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
     * @param beanName the bean name
     *
     * @return the spring managed bean with the given name
     */
    @SuppressWarnings("unchecked")
    public <T> T byName(final String beanName) {
        Assert.notNull(beanName, "[beanName] must not be null.");
        try {
            return (T) factory.getBean(beanName);
        } catch (final NoSuchBeanDefinitionException e) {
            throw new IllegalArgumentException("No Spring managed bean for name '" + beanName + "' was found.");
        }
    }

    public ListableBeanFactory getBeanFactory() {
        return factory;
    }

    @PreDestroy
    private void onDestroy() {
        _INSTANCE = null;
    }
}
