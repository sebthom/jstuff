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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * @author <a href="http://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanInjectorTest extends TestCase {

    public static class Entity implements InitializingBean, DisposableBean {
        @Autowired
        Object springBean;

        @Inject
        @Named("springBean")
        Object springBean2;

        boolean preDestroyCalled;
        boolean postConstructCalled;
        boolean destroyCalled;
        boolean afterPropertiesSetCalled;

        @PreDestroy
        void onPreDestroy() {
            preDestroyCalled = true;
        }

        @PostConstruct
        void onPostConstruct() {
            postConstructCalled = true;
        }

        @Override
        public void destroy() throws Exception {
            destroyCalled = true;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            afterPropertiesSetCalled = true;
        }
    }

    public void testSpringBeanInjector() throws Exception {
        try {
            SpringBeanInjector.get(); // must fail, since the spring context is not yet opened
            fail();
        } catch (final IllegalStateException ex) {}

        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeanInjectorTest.xml", SpringBeanInjectorTest.class);

        final SpringBeanInjector injector = SpringBeanInjector.get();

        final Entity e = new Entity();
        injector.inject(e);
        assertNotNull(e.springBean);
        assertNotNull(e.springBean2);
        assertFalse(e.postConstructCalled);
        assertFalse(e.afterPropertiesSetCalled);

        final Entity e2 = new Entity();
        injector.registerSingleton("myBean", e2);
        assertNotNull(e2.springBean);
        assertNotNull(e2.springBean2);
        assertTrue(e2.postConstructCalled);
        assertTrue(e2.afterPropertiesSetCalled);

        ctx.close();

        assertTrue(e2.preDestroyCalled);
        assertTrue(e2.destroyCalled);

        try {
            SpringBeanInjector.get(); // must fail, since the spring context is closed
            fail();
        } catch (final IllegalStateException ex) {}
    }
}
