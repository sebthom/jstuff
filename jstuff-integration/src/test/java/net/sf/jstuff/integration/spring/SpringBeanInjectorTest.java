/*********************************************************************
 * Copyright 2010-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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

      public void afterPropertiesSet() throws Exception {
         afterPropertiesSetCalled = true;
      }

      public void destroy() throws Exception {
         destroyCalled = true;
      }

      @PostConstruct
      void onPostConstruct() {
         postConstructCalled = true;
      }

      @PreDestroy
      void onPreDestroy() {
         preDestroyCalled = true;
      }
   }

   public void testSpringBeanInjector() throws Exception {
      try {
         SpringBeanInjector.get(); // must fail, since the spring context is not yet opened
         fail();
      } catch (final IllegalStateException ex) {
         // expected
      }

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
      } catch (final IllegalStateException ex) {
         // expected
      }
   }
}
