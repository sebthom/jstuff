/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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

import java.util.List;
import java.util.Map.Entry;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

import net.sf.jstuff.core.collection.ObjectCache;
import net.sf.jstuff.core.logging.Logger;
import net.sf.jstuff.core.validation.Assert;

/**
 * Injects spring beans into unmanaged Java objects having {@link org.springframework.beans.factory.annotation.Autowired},
 * {@link org.springframework.beans.factory.annotation.Value} and {@link javax.inject.Inject} annotations.
 *
 * <pre>
 * &lt;context:annotation-config /&gt;
 *
 * &lt;bean class="net.sf.jstuff.integration.spring.SpringBeanInjector" /&gt;
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
public class SpringBeanInjector {
    private static final Logger LOG = Logger.create();

    private static SpringBeanInjector _INSTANCE;

    /**
     * @return the default instance (the last instantiated one by any spring context)
     */
    public static SpringBeanInjector get() {
        Assert.notNull(_INSTANCE, "No SpringBeanInjector instance created yet. Add <bean class=\"" + SpringBeanInjector.class.getName()
                + "\" /> to your spring configuration!");

        return _INSTANCE;
    }

    private final ObjectCache<String, Object> registeredSingletons = new ObjectCache<String, Object>(true);

    @Autowired
    private DefaultListableBeanFactory beanFactory;

    @Autowired
    private List<DestructionAwareBeanPostProcessor> destructors;

    private SpringBeanInjector() {
        Assert.isNull(_INSTANCE, "A instance of " + getClass().getName() + " already exists.");

        LOG.infoNew(this);

        _INSTANCE = this;
    }

    /**
     * Injects declared dependencies but does not execute any life-cycle methods.
     */
    public void inject(final Object unmanagedBean) {
        LOG.entry(unmanagedBean);

        // process @Autowired, @Inject
        beanFactory.autowireBean(unmanagedBean);

        LOG.exit();
    }

    /**
     * Fully initializes the unmanaged bean, registers it with the spring context and enables
     * life-cycle callback methods.
     */
    public void registerSingleton(final String beanName, final Object uninitializedBean) {
        LOG.entry(uninitializedBean);

        // process @Autowired, @Inject
        beanFactory.autowireBean(uninitializedBean);

        // process @PostConstruct, InitializingBean#afterPropertiesSet
        beanFactory.initializeBean(uninitializedBean, beanName);

        // add to spring context
        beanFactory.registerSingleton(beanName, uninitializedBean);

        // register for @Destroy processing
        registeredSingletons.put(beanName, uninitializedBean);

        LOG.exit();
    }

    /**
     * Executes @PreDestroy and {@link DisposableBean#destroy} life-cycle methods.
     */
    private void destroy(final Object unmanagedBean) throws Exception {
        LOG.entry(unmanagedBean);

        // process @PreDestroy
        for (final DestructionAwareBeanPostProcessor destructor : destructors) {
            destructor.postProcessBeforeDestruction(unmanagedBean, "bean");
        }

        if (unmanagedBean instanceof DisposableBean) {
            ((DisposableBean) unmanagedBean).destroy();
        }

        LOG.exit();
    }

    @PreDestroy
    private void onDestroy() {
        for (final Entry<String, Object> s : registeredSingletons.getAll().entrySet()) {
            try {
                // removes the bean from the context, but does not it's call destroy life-cycle methods
                beanFactory.destroySingleton(s.getKey());

                // call life-cycle methods
                destroy(s.getValue());
            } catch (final Exception ex) {
                LOG.error(ex);
            }
        }
        _INSTANCE = null;
    }

}
