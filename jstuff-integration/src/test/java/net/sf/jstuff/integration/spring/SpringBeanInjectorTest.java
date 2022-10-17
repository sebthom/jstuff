/*
 * Copyright 2010-2022 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import static org.assertj.core.api.Assertions.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanInjectorTest {

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

      @Override
      public void afterPropertiesSet() throws Exception {
         afterPropertiesSetCalled = true;
      }

      @Override
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

   @Test
   public void testSpringBeanInjector() throws Exception {
      try {
         SpringBeanInjector.get(); // must fail, since the spring context is not yet opened
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         // expected
      }

      final var ctx = new ClassPathXmlApplicationContext("SpringBeanInjectorTest.xml", SpringBeanInjectorTest.class);

      final SpringBeanInjector injector = SpringBeanInjector.get();

      final var e = new Entity();
      injector.inject(e);
      assertThat(e.springBean).isNotNull();
      assertThat(e.springBean2).isNotNull();
      assertThat(e.postConstructCalled).isFalse();
      assertThat(e.afterPropertiesSetCalled).isFalse();

      final var e2 = new Entity();
      injector.registerSingleton("myBean", e2);
      assertThat(e2.springBean).isNotNull();
      assertThat(e2.springBean2).isNotNull();
      assertThat(e2.postConstructCalled).isTrue();
      assertThat(e2.afterPropertiesSetCalled).isTrue();

      ctx.close();

      assertThat(e2.preDestroyCalled).isTrue();
      assertThat(e2.destroyCalled).isTrue();

      try {
         SpringBeanInjector.get(); // must fail, since the spring context is closed
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         // expected
      }
   }
}
