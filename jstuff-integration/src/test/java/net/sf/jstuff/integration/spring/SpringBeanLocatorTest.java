/*
 * Copyright 2010-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.jstuff.integration.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="https://sebthom.de/">Sebastian Thomschke</a>
 */
public class SpringBeanLocatorTest {

   @Test
   public void testSpringBeanLocator() {
      try {
         SpringBeanLocator.get().byClass(Object.class); // must fail, since the spring context is not yet opened
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         // expected
      }
      final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringBeanLocatorTest.xml", SpringBeanLocatorTest.class);

      assertThat(SpringBeanLocator.get().byClass(SpringBeanLocator.class)).isNotNull();
      final Object bean = SpringBeanLocator.get().byName("springBean");
      assertThat(bean).isNotNull();

      ctx.close();

      try {
         SpringBeanLocator.get().byClass(Object.class); // must fail, since the spring context is closed
         failBecauseExceptionWasNotThrown(IllegalStateException.class);
      } catch (final IllegalStateException ex) {
         // expected
      }
   }
}
